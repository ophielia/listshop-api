package com.meg.atable.service.impl;

import com.meg.atable.api.exception.ObjectNotFoundException;
import com.meg.atable.api.model.TargetType;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.entity.TargetEntity;
import com.meg.atable.data.entity.TargetSlotEntity;
import com.meg.atable.data.repository.TargetRepository;
import com.meg.atable.data.repository.TargetSlotRepository;
import com.meg.atable.service.TargetService;
import com.meg.atable.service.tag.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.meg.atable.data.entity.TargetSlotEntity.IDENTIFIER;

/**
 * Created by margaretmartin on 18/12/2017.
 */
@Service
public class TargetServiceImpl implements TargetService {

    private final UserService userService;

    private final TargetRepository targetRepository;


    private final TargetSlotRepository targetSlotRepository;

    private final TagService tagService;

    @Autowired
    public TargetServiceImpl(UserService userService, TargetRepository targetRepository, TargetSlotRepository targetSlotRepository, TagService tagService) {
        this.userService = userService;
        this.targetRepository = targetRepository;
        this.targetSlotRepository = targetSlotRepository;
        this.tagService = tagService;
    }

    @Override
    public List<TargetEntity> getTargetsForUserName(String name, boolean includeTemporary) {
        UserAccountEntity user = userService.getUserByUserName(name);
        if (includeTemporary) {
            return targetRepository.findTargetsByUserId(user.getId());
        } else {
            return targetRepository.findTargetsByUserIdAndExpiresIsNull(user.getId());
        }
    }

    @Override
    public TargetEntity createTarget(String name, TargetEntity targetEntity) {
        if (targetEntity == null) {
            return null;
        }

        UserAccountEntity user = userService.getUserByUserName(name);

        targetEntity.setUserId(user.getId());
        targetEntity.setCreated(new Date());
        if (targetEntity.getTargetType() == null) {
            targetEntity.setTargetType(TargetType.Standard);
        }

        if (TargetType.PickUp.equals(targetEntity.getTargetType())) {
            LocalDateTime expires = LocalDateTime.now().plus(EXPIRES_AFTER_MINUTES, ChronoUnit.MINUTES );
            targetEntity.setExpires(expires);
        }
        return targetRepository.save(targetEntity);
    }

    @Override
    public TargetEntity getTargetById(String name, Long targetId) {
        UserAccountEntity user = userService.getUserByUserName(name);

        return targetRepository.findTargetByUserIdAndTargetId(user.getId(), targetId);
    }

    @Override
    public boolean deleteTarget(String name, Long targetId) {
        TargetEntity target = getTargetById(name, targetId);
        if (target == null) {
            return false;
        }
        List<TargetSlotEntity> slots = target.getSlots();
        targetSlotRepository.deleteAll(slots);
        target.setSlots(null);
        targetRepository.delete(target);

        return true;
    }

    @Override
    public TargetEntity updateTarget(String name, TargetEntity targetEntity) {
        TargetEntity existing = getTargetById(name, targetEntity.getTargetId());

        if (existing == null) {
            // trouble retrieving the target - maybe
            // doesn't belong to the user
            return null;
        }

        // copy name from passed target to existing target (none
        // of the other fields can be updated)
        existing.setTargetName(targetEntity.getTargetName());

        return targetRepository.save(existing);
    }

    @Override
    public void addSlotToTarget(String name, Long targetId, TargetSlotEntity targetSlotEntity) {
        TargetEntity targetEntity = getTargetById(name, targetId);
        targetEntity.setProposalId(null);

        targetSlotEntity.setTargetId(targetEntity.getTargetId());
        List<TargetSlotEntity> slots = targetEntity.getSlots();
        if (slots == null) {
            slots = new ArrayList<>();
        }
        OptionalInt maxSlotOrder = slots.stream()
                .mapToInt(TargetSlotEntity::getSlotOrder).max();
        Integer max = maxSlotOrder.isPresent() ? maxSlotOrder.getAsInt() : 0;
        targetSlotEntity.setSlotOrder(max + 1);

        TargetSlotEntity result;
        result = targetSlotRepository.save(targetSlotEntity);

        targetEntity.addSlot(result);

        targetRepository.save(targetEntity);
    }

    @Override
    public void deleteSlotFromTarget(String name, Long targetId, Long slotId) {
        TargetEntity targetEntity = getTargetById(name, targetId);

        targetEntity.setProposalId(null);
        Optional<TargetSlotEntity> targetSlotEntityOpt = targetSlotRepository.findById(slotId);
        if (!targetSlotEntityOpt.isPresent()) {
            throw new ObjectNotFoundException(slotId, IDENTIFIER);
        }
        TargetSlotEntity targetSlotEntity = targetSlotEntityOpt.get();
        targetEntity.removeSlot(targetSlotEntity);

        targetRepository.save(targetEntity);
        targetSlotRepository.delete(targetSlotEntity);

    }

    @Override
    public void addTagToTargetSlot(String name, Long targetId, Long slotId, Long tagId) {
        TargetEntity targetEntity = getTargetById(name, targetId);
        if (targetEntity == null) {
            return;
        }
        targetEntity.setProposalId(null);
        Optional<TargetSlotEntity> targetSlotEntityOpt = targetSlotRepository.findById(slotId);
        if (!targetSlotEntityOpt.isPresent()) {
            throw new ObjectNotFoundException(slotId, "TargetSlotEntity");
        }
        TargetSlotEntity targetSlotEntity = targetSlotEntityOpt.get();

        targetSlotEntity.addTagId(tagId);
        targetSlotRepository.save(targetSlotEntity);
    }

    @Override
    public void deleteTagFromTargetSlot(String name, Long targetId, Long slotId, Long tagId) {
        TargetEntity targetEntity = getTargetById(name, targetId);
        if (targetEntity == null) {
            return;
        }
        targetEntity.setProposalId(null); // MM get rid of this
        Optional<TargetSlotEntity> targetSlotEntityOpt = targetSlotRepository.findById(slotId);
        if (!targetSlotEntityOpt.isPresent()) {
            throw new ObjectNotFoundException(slotId, "TargetSlotEntity");
        }
        TargetSlotEntity targetSlotEntity = targetSlotEntityOpt.get();

        targetSlotEntity.removeTagId(tagId);
        targetSlotRepository.save(targetSlotEntity);
    }

    @Override
    public void deleteTagFromTarget(String name, Long targetId, Long tagId) {
        TargetEntity targetEntity = getTargetById(name, targetId);
        if (targetEntity == null) {
            return;
        }
        targetEntity.setProposalId(null);
        targetEntity.removeTargetTagId(tagId);
        targetRepository.save(targetEntity);
    }

    @Override
    public void addTagToTarget(String name, Long targetId, Long tagId) {
        TargetEntity targetEntity = getTargetById(name, targetId);
        if (targetEntity == null) {
            return;
        }
        targetEntity.addTargetTagId(tagId);
        targetEntity.setProposalId(null);
        targetRepository.save(targetEntity);
    }

    @Override
    public TargetEntity fillTagsForTarget(TargetEntity target) {
        if (target == null) {
            return null;
        }

        // get list of tag ids
        Set<Long> tagIds = target.getAllTagIds();
        // retrieve tags for ids
        Map<Long, TagEntity> dictionary = tagService.getDictionaryForIds(tagIds);
        // fill in target (and contained slots)
        target.fillInAllTags(dictionary);
        return target;
    }

    @Override
    public TargetEntity save(TargetEntity target) {
        return targetRepository.save(target);
    }

    @Override
    public TargetSlotEntity addDefaultTargetSlot(String userName, TargetEntity target) {
        TargetSlotEntity toCreate = new TargetSlotEntity();
        toCreate.setSlotDishTagId(TagService.MAIN_DISH_TAG_ID);

        TargetSlotEntity slot = createNewSlot(userName,target,toCreate);

        target.addSlot(slot);

        targetRepository.save(target);

        return slot;
    }



    private TargetSlotEntity createNewSlot(String name, TargetEntity targetEntity, TargetSlotEntity targetSlotEntity) {
        targetSlotEntity.setTargetId(targetEntity.getTargetId());
        List<TargetSlotEntity> slots = targetEntity.getSlots();
        if (slots == null) {
            slots = new ArrayList<>();
        }
        OptionalInt maxSlotOrder = slots.stream()
                .mapToInt(TargetSlotEntity::getSlotOrder).max();
        Integer max = maxSlotOrder.isPresent() ? maxSlotOrder.getAsInt() : 0;
        targetSlotEntity.setSlotOrder(max + 1);

        TargetSlotEntity result;
        result = targetSlotRepository.save(targetSlotEntity);

        return result;

    }
}
