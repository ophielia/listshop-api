package com.meg.atable.lmt.service.impl;

import com.meg.atable.auth.data.entity.UserEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.common.FlatStringUtils;
import com.meg.atable.lmt.data.entity.*;
import com.meg.atable.lmt.data.repository.*;
import com.meg.atable.lmt.service.TagReplaceService;
import com.meg.atable.lmt.service.TargetService;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
@Transactional
public class TagReplaceServiceImpl implements TagReplaceService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TargetRepository targetRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagInstructionRepository tagInstructionRepository;

    @Autowired
    private ProposalSlotRepository proposalSlotRepository;

    @Autowired
    private DishSlotRepository dishSlotRepository;

    @Autowired
    private TargetService targetService;


    @Autowired
    private UserService userService;


    @Override
    public void replaceAllTags() {
        // get all tags to replace
        List<TagEntity> toReplaceList = tagRepository.findTagsByToDeleteTrue();

        // go through each, calling replaceTag
        for (TagEntity tag : toReplaceList) {
            if (tag.getReplacementTagId()==null) {
                continue;
            }
            replaceTag(tag.getId(),tag.getReplacementTagId());
        }
    }


    @Override
    @Transactional
    public void replaceTag(Long toReplaceId, Long replaceWithId) {
        Session session = entityManager.unwrap(Session.class);

        replaceTagInDish(toReplaceId, replaceWithId);
        replaceTagInLists(toReplaceId, replaceWithId);
        replaceTagsInTargets(toReplaceId, replaceWithId);
        replaceTagsInProposal(toReplaceId, replaceWithId);
        replaceAutotagInstruction(toReplaceId, replaceWithId);


        // category tag id
        removeCategoryTags(toReplaceId);
        // list tag statistic - shadow tags
        removeStatsAndShadowTags(toReplaceId);
         // tag relation
        removeTagRelation(toReplaceId);
        // tag search group
        removeTagSearchGroup(toReplaceId);

        // delete the actual tag
        tagRepository.deleteById(toReplaceId);

        // clear session
        session.flush();
        session.clear();

    }


    private void removeTagSearchGroup(Long toReplaceId) {
        StringBuilder baseSql = new StringBuilder();
        baseSql.append("delete from  tag_search_group " +
                "where member_id = ");
        baseSql.append(toReplaceId).append("; ");
        Query query = entityManager.createNativeQuery(baseSql.toString());
        query.executeUpdate();
    }

    private void removeTagRelation(Long toReplaceId) {
        StringBuilder baseSql = new StringBuilder();
        baseSql.append("delete from  tag_relation " +
                "where child_tag_id = ");
        baseSql.append(toReplaceId).append("; ");
        Query query = entityManager.createNativeQuery(baseSql.toString());
        query.executeUpdate();
    }

    private void removeStatsAndShadowTags(Long toReplaceId) {
        StringBuilder baseSql = new StringBuilder();
        baseSql.append("delete from  list_tag_stats " +
                "where tag_id = ");
        baseSql.append(toReplaceId).append("; ");
        Query query = entityManager.createNativeQuery(baseSql.toString());
        query.executeUpdate();

         baseSql = new StringBuilder();
        baseSql.append("delete from  shadow_tags " +
                "where tag_id = ");
        baseSql.append(toReplaceId).append("; ");
        query = entityManager.createNativeQuery(baseSql.toString());
        query.executeUpdate();
    }

    private void removeCategoryTags(Long toReplaceId) {
        StringBuilder baseSql = new StringBuilder();
        baseSql.append("delete from  category_tags " +
                "where tag_id = ");
        baseSql.append(toReplaceId).append("; ");
        Query query = entityManager.createNativeQuery(baseSql.toString());
        query.executeUpdate();

    }

    private void replaceAutotagInstruction(Long toReplaceId, Long replaceWithId) {

        // find candidates
        List<TagInstructionEntity> candidates = findAutoTagCandidates(toReplaceId);
        // for each candidate -
        for (AutoTagInstructionEntity instructionEntity  : candidates) {
            // replace tag
            instructionEntity.setAssignTagId(replaceWithId);
        }
        tagInstructionRepository.saveAll(candidates);
    }

    private List<TagInstructionEntity> findAutoTagCandidates(Long toReplaceId) {
        return tagInstructionRepository.findByAssignTagId(toReplaceId);
    }

    private void replaceTagInLists(Long toReplaceId, Long replaceWithId) {

        // write sql for delete
        StringBuilder baseSql = new StringBuilder();
        StringBuilder withSql = new StringBuilder("with tag_replace as " +
                "                    ( " +
                "                            select " +
                "                            dt.list_id, " +
                "                            count(distinct dt.tag_id) as tag_count " +
                "                            from " +
                "                            list_item dt " +
                "                            where " +
                "                            dt.tag_id in " +
                "                                    ( ");
        withSql.append(toReplaceId).append(",").append(replaceWithId).append(") ");
        withSql.append("                     and list_id is not null " +
                "            group by " +
                "            dt.list_id " +
                ")");

        baseSql.append(withSql);
        baseSql.append("delete " +
                "                    from " +
                "            list_item dt  " +
                "            using tag_replace tr " +
                "                    where " +
                "            tr.list_id = dt.list_id " +
                "            and dt.tag_id = ");
        baseSql.append(toReplaceId).append(" ");
        baseSql.append("            and tr.tag_count = 2; ");

        // execute sql for delete
        Query query = entityManager.createNativeQuery(baseSql.toString());
        query.executeUpdate();

        // write sql for replace
        baseSql = new StringBuilder();
        baseSql.append(withSql);
        baseSql.append("update list_item dt " +
                "set tag_id = ");
        baseSql.append(replaceWithId).append(" ");
        baseSql.append("from tag_replace tr " +
                "where tr.list_id = dt.list_id " +
                "and dt.tag_id = ");
        baseSql.append(toReplaceId).append(" ");
        baseSql.append("and tr.tag_count = 1;");
        // execute sql for replace
        query = entityManager.createNativeQuery(baseSql.toString());
        query.executeUpdate();
    }

    private void replaceTagInDish(Long toReplaceId, Long replaceWithId) {
        // TODO add logging
        // write sql for delete

        StringBuilder baseSql = new StringBuilder();
        StringBuilder withSql = new StringBuilder("with tag_replace as " +
                "(select dt.dish_id , count(distinct dt.tag_id) as tag_count " +
                "from dish_tags dt  " +
                "where dt.tag_id in (");
        withSql.append(toReplaceId).append(",").append(replaceWithId).append(") ");
        withSql.append("group by dt.dish_id) ");
        baseSql.append(withSql);
        baseSql.append("delete  " +
                "from dish_tags dt " +
                "using tag_replace tr  " +
                "where tr.dish_id = dt.dish_id " +
                "and dt.tag_id = ");
        baseSql.append(toReplaceId).append(" ");
        baseSql.append("and tr.tag_count = 2;");

        // execute sql for delete
        Query query = entityManager.createNativeQuery(baseSql.toString());
        query.executeUpdate();

        // write sql for replace
        baseSql = new StringBuilder();
        baseSql.append(withSql);
        baseSql.append("update dish_tags dt " +
                "set tag_id = ");
        baseSql.append(replaceWithId).append(" ");
        baseSql.append("from tag_replace tr " +
                "where tr.dish_id = dt.dish_id " +
                "and dt.tag_id = ");
        baseSql.append(toReplaceId).append(" ");
        baseSql.append("and tr.tag_count = 1;");
        // execute sql for replace
        query = entityManager.createNativeQuery(baseSql.toString());
        query.executeUpdate();


    }

    // next - for proposal, replace in proposal_dish, proposal_slot

    private void replaceTagsInTargets(Long toReplaceId, Long replaceWithId) {
            // find candidates
            List<Object> candidatesRaw = findTargetSlotCandidates(toReplaceId);
            List<Long> candidates = candidatesRaw.stream().map(o -> Long.valueOf(String.valueOf(o))).collect(Collectors.toList());
            List<TargetEntity> targetEntities = targetRepository.findAllById(candidates);
            String replaceId = String.valueOf(toReplaceId);
            String replaceWith = String.valueOf(replaceWithId);
            // for each candidate -
            for (TargetEntity target : targetEntities) {
                // get user for target
                UserEntity user = userService.getUserById(target.getUserId());
                for (TargetSlotEntity slot : target.getSlots()) {
                    if (!slot.getTagIdsAsList().contains(replaceId)) {
                        continue;
                    }
                    targetService.deleteTagFromTargetSlot(user.getEmail(), target.getTargetId(), slot.getId(), toReplaceId);
                    if (!slot.getTagIdsAsList().contains(replaceWith)) {
                        targetService.addTagToTargetSlot(user.getEmail(), target.getTargetId(), slot.getId(), replaceWithId);
                    }
                }
            }

        candidatesRaw = findTargetCandidates(toReplaceId);
        candidates = candidatesRaw.stream().map(o -> Long.valueOf(String.valueOf(o))).collect(Collectors.toList());
        targetEntities = targetRepository.findAllById(candidates);
        // for each candidate -
        for (TargetEntity target : targetEntities) {
            // get user for target
            UserEntity user = userService.getUserById(target.getUserId());
            Set<String> targetTagSet = target.getTagIdsAsSet();
            if (targetTagSet.isEmpty() || !targetTagSet.contains(replaceId)) {
                continue;
            }
            targetService.deleteTagFromTarget(user.getEmail(), target.getTargetId(), toReplaceId);
            if (!targetTagSet.contains(replaceWith)) {
                targetService.addTagToTarget(user.getEmail(), target.getTargetId(), replaceWithId);
            }

        }

    }

    private List<Object> findTargetSlotCandidates(Long toReplaceId) {
        StringBuilder baseSql = new StringBuilder();
        baseSql.append("select distinct target_id from target_slot where " +
                "        (target_tag_ids like '");
        baseSql.append(toReplaceId).append("%' or " +
                "        target_tag_ids like '%;");
        baseSql.append(toReplaceId).append(";%' or " +
                "        target_tag_ids like '%;");
        baseSql.append(toReplaceId).append(";' or " +
                "        target_tag_ids like '%;");
        baseSql.append(toReplaceId).append("')");
        Query query = entityManager.createNativeQuery(baseSql.toString());
        return query.getResultList();
    }

    private List<Object> findTargetCandidates(Long toReplaceId) {
        StringBuilder baseSql = new StringBuilder();
        baseSql.append("select distinct target_id from target where " +
                "        (target_tag_ids like '");
        baseSql.append(toReplaceId).append("%' or " +
                "        target_tag_ids like '%;");
        baseSql.append(toReplaceId).append(";%' or " +
                "        target_tag_ids like '%;");
        baseSql.append(toReplaceId).append(";' or " +
                "        target_tag_ids like '%;");
        baseSql.append(toReplaceId).append("')");
        Query query = entityManager.createNativeQuery(baseSql.toString());
        return query.getResultList();
    }

    private void replaceTagsInProposal(Long toReplaceId, Long replaceWithId) {
        // find candidates
        List<Object> candidatesRaw = findProposalSlotCandidates(toReplaceId);
        List<Long> candidates = candidatesRaw.stream().map(o -> Long.valueOf(String.valueOf(o))).collect(Collectors.toList());
        List<ProposalSlotEntity> proposalSlotEntities = proposalSlotRepository.findAllById(candidates);
        String replaceId = String.valueOf(toReplaceId);
        String replaceWith = String.valueOf(replaceWithId);
        List<ProposalSlotEntity> toSave = new ArrayList<>();
        // for each candidate -
        for (ProposalSlotEntity proposalSlotEntity : proposalSlotEntities) {
            if (proposalSlotEntity.getFlatMatchedTagIds() == null || proposalSlotEntity.getFlatMatchedTagIds().isEmpty()) {
                continue;
            }
            // inflate flat tag list
            Set<String> tagSet = FlatStringUtils.inflateStringToSet(proposalSlotEntity.getFlatMatchedTagIds(), ";");
            if (tagSet.contains(replaceId)) {
                tagSet = tagSet.stream().filter(t -> !t.equals(replaceId)).collect(Collectors.toSet());

                tagSet.add(replaceWith);
                proposalSlotEntity.setFlatMatchedTagIds(FlatStringUtils.flattenSetToString(tagSet, ";"));
                toSave.add(proposalSlotEntity);
            }
        }
        proposalSlotRepository.saveAll(toSave);

        candidatesRaw = findProposalDishSlotCandidates(toReplaceId);
        candidates = candidatesRaw.stream().map(o -> Long.valueOf(String.valueOf(o))).collect(Collectors.toList());
        List<DishSlotEntity> dishSlotEntities = dishSlotRepository.findAllById(candidates);
        List<DishSlotEntity> dishSlotsToSave = new ArrayList<>();
        // for each candidate -
        for (DishSlotEntity dishSlotEntity : dishSlotEntities) {
            if (dishSlotEntity.getMatchedTagIds() == null || dishSlotEntity.getMatchedTagIds().isEmpty()) {
                continue;
            }
            // inflate flat tag list
            Set<String> tagSet = FlatStringUtils.inflateStringToSet(dishSlotEntity.getMatchedTagIds(), ";");
            if (tagSet.contains(replaceId)) {
                tagSet = tagSet.stream().filter(t -> !t.equals(replaceId)).collect(Collectors.toSet());

                tagSet.add(replaceWith);
                dishSlotEntity.setMatchedTagIds(FlatStringUtils.flattenSetToString(tagSet, ";"));
                dishSlotsToSave.add(dishSlotEntity);
            }

        }

        proposalSlotRepository.saveAll(toSave);

    }

    private List<Object> findProposalDishSlotCandidates(Long toReplaceId) {
        StringBuilder baseSql = new StringBuilder();
        baseSql.append("select distinct dish_slot_id from proposal_dish where " +
                "        (matched_tag_ids like '");
        baseSql.append(toReplaceId).append("%' or " +
                "        matched_tag_ids  like '%;");
        baseSql.append(toReplaceId).append(";%' or " +
                "        matched_tag_ids  like '%;");
        baseSql.append(toReplaceId).append(";' or " +
                "        matched_tag_ids  like '%;");
        baseSql.append(toReplaceId).append("')");
        Query query = entityManager.createNativeQuery(baseSql.toString());
        return query.getResultList();
    }

    private List<Object> findProposalSlotCandidates(Long toReplaceId) {
        StringBuilder baseSql = new StringBuilder();
        baseSql.append("select distinct slot_id from proposal_slot where " +
                "        (flat_matched_tag_ids like '");
        baseSql.append(toReplaceId).append("%' or " +
                "        flat_matched_tag_ids   like '%;");
        baseSql.append(toReplaceId).append(";%' or " +
                "        flat_matched_tag_ids   like '%;");
        baseSql.append(toReplaceId).append(";' or " +
                "        flat_matched_tag_ids   like '%;");
        baseSql.append(toReplaceId).append("')");
        Query query = entityManager.createNativeQuery(baseSql.toString());
        return query.getResultList();
    }
}
