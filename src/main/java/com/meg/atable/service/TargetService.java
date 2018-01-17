package com.meg.atable.service;

import com.meg.atable.data.entity.TargetEntity;
import com.meg.atable.data.entity.TargetSlotEntity;

import java.util.List;

/**
 * Created by margaretmartin on 18/12/2017.
 */
public interface TargetService {

    List<TargetEntity> getTargetsForUserName(String name);

    TargetEntity createTarget(String name, TargetEntity targetEntity);

    TargetEntity getTargetById(String name, Long targetId);

    boolean deleteTarget(String name, Long targetId);

    TargetEntity updateTarget(String name, TargetEntity targetEntity);

    void addSlotToTarget(String name, Long targetId, TargetSlotEntity targetSlotEntity);

    void deleteSlotFromTarget(String name, Long targetId, Long slotId);

    void addTagToTargetSlot(String name, Long targetId, Long slotId, Long tagId);

    void deleteTagFromTargetSlot(String name, Long targetId, Long slotId, Long tagId);

    void deleteTagFromTarget(String name, Long targetId, Long tagId);

    void addTagToTarget(String name, Long targetId, Long tagId);

    TargetEntity fillTagsForTarget(TargetEntity target);

    TargetEntity save(TargetEntity target);
}
