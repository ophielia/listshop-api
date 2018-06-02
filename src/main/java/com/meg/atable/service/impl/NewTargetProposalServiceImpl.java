package com.meg.atable.service.impl;

import com.meg.atable.api.exception.ObjectNotFoundException;
import com.meg.atable.api.exception.ObjectNotYoursException;
import com.meg.atable.api.exception.ProposalProcessingException;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.common.FlatStringUtils;
import com.meg.atable.data.entity.*;
import com.meg.atable.data.repository.ProposalContextRepository;
import com.meg.atable.data.repository.ProposalRepository;
import com.meg.atable.data.repository.TargetRepository;
import com.meg.atable.service.*;
import com.meg.atable.service.tag.TagService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
@Transactional
public class NewTargetProposalServiceImpl implements NewTargetProposalService {

    private static final Logger logger = LogManager.getLogger(NewTargetProposalServiceImpl.class);

    ProposalProcessorFactory proposalProcessorFactory;

    @Autowired
    TargetRepository targetRepository;

    @Autowired
    ProposalRepository proposalRepository;

    @Autowired
    ProposalContextRepository contextRepository;

    @Autowired
    UserService userService;

    @Autowired
    DishService dishService;

    @Autowired
    private MealPlanService mealPlanService;

    @Autowired
    private TagService tagService;

    @Override
    public ProposalEntity generateProposal(String userName, Long targetId) throws ObjectNotYoursException, ObjectNotFoundException {
        // get target for user
        TargetEntity target = getTargetForUser(userName, targetId);
        // get proposal context for target
        ProposalContextEntity context = getContextForTarget(targetId);
        ProposalEntity proposal = null;
        if (context != null) {
            proposal = getProposalForUser(userName, context.getProposalId());
        }

        // create search object
        ProposalRequestBuilder builder = new ProposalRequestBuilder();
        ProposalRequest request = builder.create()
                .withSearchType(ProposalSearchType.NewSearch)
                .withTarget(target)
                .withProposal(proposal)
                .withContext(context)
                .build();

        // process proposal request
        ProcessResult result = processProposalRequest(request);

        return persistResults(target,context, proposal, result);
    }

    @Override
    public ProposalEntity refreshProposal(String userName, Long proposalId) throws ProposalProcessingException {
        return refreshOrFillInProposal(userName,proposalId, null);
    }

    @Override
    public ProposalEntity fillInProposal(String userName, Long proposalId, Integer slotNr) throws ProposalProcessingException {
        return refreshOrFillInProposal(userName,proposalId, slotNr);
    }



    @Override
    public ProposalEntity proposalForMealPlan(String userName, Long mealPlanId, Long targetId, Integer slotId) throws ProposalProcessingException {
        boolean newSearch = true;
        ProposalEntity proposal = null;

        // get proposal context for target
        ProposalContextEntity context = getContextForTarget(targetId);
        if (context != null) {
            newSearch = false;
            try {
                proposal = getProposalForUser(userName, context.getProposalId());
            } catch (ObjectNotFoundException e) {
                // the context was found, but not the proposal.
                // strange - but not can be handled
                // log and continue
                logger.error("Proposal [" + context.getProposalId() + "] not found for existing context in MealPlan search.", e);
                proposal = new ProposalEntity();
            }
        } else {
            context = new ProposalContextEntity();
            context.setMealPlanId(mealPlanId);
        }

        // get  target
        TargetEntity target = null;
        try {
            target = getTargetForUser(userName, targetId);
        } catch (ObjectNotFoundException | ObjectNotYoursException e) {
            throw new ProposalProcessingException("Target [" + targetId + "] not found for MealPlan search.", e);
        }

        // get mealplan
        MealPlanEntity mealPlan;
        try {
            mealPlan = mealPlanService.getMealPlanById(userName, mealPlanId);
            mealPlanService.fillInDishTags(mealPlan);
        } catch (ObjectNotFoundException | ObjectNotYoursException e) {
            throw new ProposalProcessingException("MealPlan [" + mealPlanId + "] not found for MealPlan search.", e);
        }


        // check for changes
        boolean hasChanged = !newSearch && (hasTargetChange(target, context) || hasProposalChange(proposal, context));


        ProposalSearchType searchType = hasChanged || newSearch ? ProposalSearchType.NewSearch : ProposalSearchType.RefreshSearch;

        // create search object
        ProposalRequestBuilder builder = new ProposalRequestBuilder();
        ProposalRequest request = builder.create()
                .withSearchType(searchType)
                .withProposal(proposal)
                .withTarget(target)
                .withMealPlan(mealPlan)
                .withContext(context)
                .build();

        // process proposal request
        ProcessResult result = processProposalRequest(request);

        // persist results
        // process proposal
        return persistResults(target,context, proposal, result);

    }

    @Override
    public ProposalEntity fillInformationForProposal(ProposalEntity proposalEntity) {
        if (proposalEntity == null) {
            return null;
        }

        // get list of tag ids
        // Set<Long> tagIds = proposalEntity.getAllTagIds();

        // get target
        TargetEntity target = getTargetForProposal(proposalEntity);
        proposalEntity.setTargetName(target.getTargetName());
        // get all tag ids from target
        Set<Long> tagIds = target.getAllTagIds();
        // retrieve tags for ids
        Map<Long, TagEntity> tagDictionary = tagService.getDictionaryForIds(tagIds);

        // set target tags
        List<TagEntity> targetTags = getTagsForList(target.getTargetTagIds(),tagDictionary);
        proposalEntity.setTargetTags(targetTags);
        // fill slots
        for (TargetSlotEntity targetSlot: target.getSlots()) {
            proposalEntity.fillSlotTags(targetSlot.getSlotOrder(), targetSlot.getTagIdsAsList(),tagDictionary);
        }

        // get list of dish ids
        List<Long> dishIds = proposalEntity.getAllDishIds();
        Map<Long, DishEntity> dishDictionary = dishService.getDictionaryForIdList(dishIds);
        proposalEntity.fillInAllDishes(dishDictionary);

        return proposalEntity;

    }

    private ProposalEntity refreshOrFillInProposal(String userName, Long proposalId, Integer slotNr) throws ProposalProcessingException {
        // get proposal for user
        ProposalEntity proposal = null;
        try {
            proposal = getProposalForUser(userName, proposalId);
        } catch (ObjectNotFoundException e) {
            final String msg = "Can't refresh Proposal [" + proposalId + "] which can't be found.";
            throw new ProposalProcessingException(msg, e);
        }
        // get proposal context for target
        ProposalContextEntity context = getContextForProposal(proposalId);
        if (context == null) {
            final String msg = "Can't refresh Proposal [" + proposalId + "] without a context.";
            throw new ProposalProcessingException(msg);
        }

        TargetEntity target = null;
        try {
            target = getTargetForUser(userName, context.getTargetId());
        } catch (ObjectNotYoursException | ObjectNotFoundException  e) {
            final String msg = "Can't retreive target [" + context.getTargetId() + "] for context.";
            throw new ProposalProcessingException(msg,e);
        }

        // check for changes
        boolean hasChanged = hasTargetChange(target, context) || hasProposalChange(proposal, context);


        ProposalSearchType searchType = hasChanged ? ProposalSearchType.NewSearch : ProposalSearchType.RefreshSearch;
        if (slotNr != null) {
            searchType = ProposalSearchType.FillInSearch;
        }

        // create search object
        ProposalRequestBuilder builder = new ProposalRequestBuilder();
        ProposalRequest request = builder.create()
                .withSearchType(searchType)
                .withProposal(proposal)
                .withTarget(target)
                .withContext(context)
                .build();

        // process proposal request
        ProcessResult result = processProposalRequest(request);

        // persist results
        return persistResults(target,context, proposal, result);
    }


    private List<TagEntity> getTagsForList(String targetTagIds, Map<Long, TagEntity> dictionary) {
        return FlatStringUtils.inflateStringToList(targetTagIds,";")
                .stream()
                .filter(t -> dictionary.containsKey(new Long(t)))
                .map(t -> dictionary.get(new Long(t)))
                .collect(Collectors.toList());

    }

    private TargetEntity getTargetForProposal(ProposalEntity proposalEntity) {
        return targetRepository.findTargetByProposalId(proposalEntity.getId());
    }


    private ProposalEntity persistResults(TargetEntity target, ProposalContextEntity context, ProposalEntity proposal, ProcessResult result) {


        // persist results
        if (proposal.getId() == null) {
            proposal.setCreated(new Date());
            proposal = proposalRepository.save(proposal);
        }
        if (context.getId() == null) {
            context = contextRepository.save(context);
        }
        // process proposal
        List<ProposalSlotEntity> resultSlots = mergeProposalSlots(proposal, result.getResultSlots());
        proposal.setSlots(resultSlots);
        boolean canRefresh = result.getResultApproaches() != null && !result.getResultApproaches().isEmpty();
        proposal.setIsRefreshable(canRefresh);
        proposalRepository.save(proposal);

        // process context
        List<ContextApproachEntity> resultApproaches = mergeContextApproaches(context, result.getResultApproaches());
        context.setProposalId(proposal.getId());
        context.setTargetId(target.getTargetId());
        context.setApproaches(resultApproaches);
        context.setProposalHashCode(proposal.getPickedHashCode());
        context.setTargetHashCode(target.getContentHashCode());
        context.setCurrentApproachIndex(result.getCurrentApproach());
        if (result.getCurrentApproachType() != null) {
            context.setCurrentApproachType(result.getCurrentApproachType());
        }
        contextRepository.save(context);

        return proposal;
    }

    private List<ContextApproachEntity> mergeContextApproaches(ProposalContextEntity context, List<ContextApproachEntity> resultApproaches) {
        List<ContextApproachEntity> finalList = new ArrayList<>();
        Iterator<ContextApproachEntity> resultSlotIterator = resultApproaches.iterator();
        Iterator<ContextApproachEntity> existingSlotIterator = context.getApproaches().iterator();
        while (resultSlotIterator.hasNext()) {
            ContextApproachEntity resultApproach = resultSlotIterator.next();
            while (existingSlotIterator.hasNext()) {
                ContextApproachEntity existingApproach = existingSlotIterator.next();
                if (existingApproach.getApproachNumber().equals(resultApproach.getApproachNumber())) {
                    resultSlotIterator.remove();
                    existingSlotIterator.remove();
                    existingApproach.setInstructions(resultApproach.getInstructions());
                    finalList.add(existingApproach);
                    break;
                }
            }
        }

        // add any results not found in existing to final List
        resultApproaches.stream().forEach(s -> s.setContext(context));
        finalList.addAll(resultApproaches);
        return finalList;
    }

    private List<ProposalSlotEntity> mergeProposalSlots(ProposalEntity proposal, List<ProposalSlotEntity> resultSlots) {
        List<ProposalSlotEntity> finalList = new ArrayList<>();
        Iterator<ProposalSlotEntity> resultSlotIterator = resultSlots.iterator();
        Iterator<ProposalSlotEntity> existingSlotIterator = proposal.getSlots().iterator();
        while (resultSlotIterator.hasNext()) {
            ProposalSlotEntity resultSlot = resultSlotIterator.next();
            while (existingSlotIterator.hasNext()) {
                ProposalSlotEntity existingSlot = existingSlotIterator.next();
                if (existingSlot.getSlotNumber().equals(resultSlot.getSlotNumber())) {
                    resultSlotIterator.remove();
                    existingSlotIterator.remove();
                    copySlotResults(resultSlot, existingSlot);
                    finalList.add(existingSlot);
                    break;
                }
            }
        }

        // add any results not found in existing to final List
        resultSlots.stream().forEach(s -> s.setProposal(proposal));
        finalList.addAll(resultSlots);
        return finalList;
    }

    private List<DishSlotEntity> copySlotResults(ProposalSlotEntity resultSlot, ProposalSlotEntity existingSlot) {

        List<DishSlotEntity> finalDishList = new ArrayList<>();
        // MM result dish list may need to be converted
        Iterator<DishSlotEntity> resultSlotIterator = resultSlot.getDishSlots().iterator();
        Iterator<DishSlotEntity> existingSlotIterator = existingSlot.getDishSlots().iterator();
        while (resultSlotIterator.hasNext()) {
            DishSlotEntity resultDish = resultSlotIterator.next();
            while (existingSlotIterator.hasNext()) {
                DishSlotEntity existingDish = existingSlotIterator.next();
                if (existingDish.getDishId().equals(resultDish.getDishId())) {
                    resultSlotIterator.remove();
                    existingSlotIterator.remove();
                    existingDish.setMatchedTagIds(resultDish.getMatchedTagIds());
                    finalDishList.add(existingDish);
                    break;
                }
            }
        }

        // add any results not found in existing to final List
        resultSlot.getDishSlots().stream().forEach(s -> s.setSlot(existingSlot)); // MM again - may need some conversion here
        finalDishList.addAll(resultSlot.getDishSlots());
        return finalDishList;
    }

    private ProcessResult processProposalRequest(ProposalRequest request) {
        ProposalProcessor processor = proposalProcessorFactory.getProposalProcessor(request);

        return processor.processProposal(request);
    }

    private ProposalContextEntity getContextForTarget(Long targetId) {
        return contextRepository.findByTargetId(targetId);
    }

    private TargetEntity getTargetForUser(String userName, Long targetId) throws ObjectNotYoursException, ObjectNotFoundException {
        UserAccountEntity user = userService.getUserByUserName(userName);

        TargetEntity target = targetRepository.findTargetByUserIdAndTargetId(user.getId(), targetId);
        if (target == null) {
            final String msg = "No target found by id for user [" + userName + "] and target [" + targetId + "]";
            throw new ObjectNotFoundException(msg, targetId, "Target");
        }

        return target;
    }

    private ProposalContextEntity getContextForProposal(Long targetProposalId) {
        return contextRepository.findByProposalId(targetProposalId);
    }

    private ProposalEntity getProposalForUser(String userName, Long proposalId) throws ObjectNotFoundException {
        UserAccountEntity user = userService.getUserByUserName(userName);

        ProposalEntity proposal = proposalRepository.findProposalByUserIdAndId(user.getId(), proposalId);
        if (proposal == null) {
            final String msg = "No proposal found by id for user [" + userName + "] and proposal [" + proposalId + "]";
            throw new ObjectNotFoundException(msg, proposalId, "Proposal");
        }

        return proposal;
    }


    private boolean hasTargetChange(TargetEntity target, ProposalContextEntity context) {
        if (target == null) {
            return true;
        }
        if (context == null) {
            return true;
        }
        if (context.getTargetHashCode() == null) {
            return true;
        }

        return context.getTargetHashCode().equals(target.getContentHashCode());
    }

    private boolean hasProposalChange(ProposalEntity proposal, ProposalContextEntity context) {
        if (proposal == null) {
            return true;
        }
        if (context == null) {
            return true;
        }
        if (context.getProposalHashCode() == null) {
            return true;
        }
        return !context.getProposalHashCode().equals(proposal.getPickedHashCode());
    }

}
