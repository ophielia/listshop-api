/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.auth.api.model.TargetType;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.data.entity.TargetEntity;
import com.meg.listshop.lmt.data.entity.TargetSlotEntity;
import com.meg.listshop.lmt.data.repository.TargetRepository;
import com.meg.listshop.lmt.data.repository.TargetSlotRepository;
import com.meg.listshop.lmt.service.TargetService;
import com.meg.listshop.lmt.service.tag.TagService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class TargetServiceImplMockTest {

    @MockBean
    private UserService userService;

    @MockBean
    private TagService tagService;

    @MockBean
    private TargetService targetService;

    @MockBean
    private TargetRepository targetRepository;

    @MockBean
    private TargetSlotRepository targetSlotRepository;

    @BeforeEach
    public void setUp() throws Exception {
        targetService = new TargetServiceImpl(userService, targetRepository, targetSlotRepository, tagService);
    }

    @Test
    void getTargetsForUserName_Temporary() throws Exception {
        String userName = "user@name.com";
        Long userId = 20L;
        Long targetId = 99L;
        Long originalSlotId = 9999L;
        UserEntity user = new UserEntity();
        user.setUsername(userName);
        user.setId(userId);
        TargetEntity targetOne = new TargetEntity();
        targetOne.setTargetId(targetId);
        targetOne.setProposalId(8888L);
        TargetEntity targetTwo = new TargetEntity();
        targetTwo.setTargetId(targetId);
        targetTwo.setProposalId(9999L);
        List<TargetEntity> targetEntities = new ArrayList<>();
        targetEntities.add(targetTwo);
        targetEntities.add(targetOne);

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(targetRepository.findTargetsByUserId(user.getId())).thenReturn(targetEntities);

        // call under test
        List<TargetEntity> result = targetService.getTargetsForUserName(userName, true);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());

    }

    @Test
    void getTargetsForUserName() throws Exception {
        String userName = "user@name.com";
        Long userId = 20L;
        Long targetId = 99L;
        Long originalSlotId = 9999L;
        UserEntity user = new UserEntity();
        user.setUsername(userName);
        user.setId(userId);
        TargetEntity targetOne = new TargetEntity();
        targetOne.setTargetId(targetId);
        targetOne.setProposalId(8888L);
        TargetEntity targetTwo = new TargetEntity();
        targetTwo.setTargetId(targetId);
        targetTwo.setProposalId(9999L);
        List<TargetEntity> targetEntities = new ArrayList<>();
        targetEntities.add(targetTwo);
        targetEntities.add(targetOne);

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(targetRepository.findTargetsByUserIdAndExpiresIsNull(user.getId())).thenReturn(targetEntities);

        // call under test
        List<TargetEntity> result = targetService.getTargetsForUserName(userName, false);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());

    }

    @Test
    void createTarget() {
        String userName = "user@user.com";
        TargetEntity newTarget = new TargetEntity();
        newTarget.setTargetName("george");
        Long userId = 20L;
        UserEntity user = new UserEntity();
        user.setUsername(userName);
        user.setId(userId);

        ArgumentCaptor<TargetEntity> targetCapture = ArgumentCaptor.forClass(TargetEntity.class);

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(targetRepository.save(targetCapture.capture())).thenReturn(newTarget);

        // call under test
        targetService.createTarget(userName, newTarget);

        TargetEntity capturedTarget = targetCapture.getValue();

        Assertions.assertNotNull(capturedTarget);
        Assertions.assertEquals("george", capturedTarget.getTargetName());
        Assertions.assertNotNull(capturedTarget.getUserId());
        Assertions.assertNull(capturedTarget.getLastUsed());
        Assertions.assertNotNull(capturedTarget.getCreated());
        Assertions.assertNull(capturedTarget.getExpires());

    }

    public void createTarget_Temporary() {
        String userName = "user@user.com";
        TargetEntity newTarget = new TargetEntity();
        newTarget.setTargetName("george");
        newTarget.setTargetType(TargetType.PickUp);
        Long userId = 20L;
        UserEntity user = new UserEntity();
        user.setUsername(userName);
        user.setId(userId);

        ArgumentCaptor<TargetEntity> targetCapture = ArgumentCaptor.forClass(TargetEntity.class);

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(targetRepository.save(targetCapture.capture())).thenReturn(newTarget);

        // call under test
        targetService.createTarget(userName, newTarget);

        TargetEntity capturedTarget = targetCapture.getValue();

        Assertions.assertNotNull(capturedTarget);
        Assertions.assertEquals("george", capturedTarget.getTargetName());
        Assertions.assertNotNull(capturedTarget.getUserId());
        Assertions.assertNull(capturedTarget.getLastUsed());
        Assertions.assertNotNull(capturedTarget.getCreated());
        Assertions.assertNotNull(capturedTarget.getExpires());

    }

    @Test
    void getTargetById() throws Exception {
        String userName = "user@name.com";
        Long userId = 20L;
        Long targetId = 99L;
        Long originalSlotId = 9999L;
        UserEntity user = new UserEntity();
        user.setUsername(userName);
        user.setId(userId);
        TargetEntity target = new TargetEntity();
        target.setTargetId(targetId);
        target.setProposalId(9999L);
        TargetSlotEntity originalSlot = new TargetSlotEntity();
        originalSlot.setId(originalSlotId);
        originalSlot.setTargetId(targetId);
        originalSlot.setSlotOrder(1);
        originalSlot.setTargetTagIds("1;2;3");
        target.addSlot(originalSlot);

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(targetRepository.findTargetByUserIdAndTargetId(userId, targetId)).thenReturn(target);

        // service call
        TargetEntity result = targetService.getTargetById(userName, targetId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(targetId, result.getTargetId(), "target id matches");
        Assertions.assertEquals(1, target.getSlots().size(), "slot count matches");
    }

    @Test
    void deleteTarget() throws Exception {
        String userName = "user@name.com";
        Long userId = 20L;
        Long targetId = 99L;
        Long originalSlotId = 9999L;
        UserEntity user = new UserEntity();
        user.setUsername(userName);
        user.setId(userId);
        TargetEntity target = new TargetEntity();
        target.setTargetId(targetId);
        target.setProposalId(9999L);
        TargetSlotEntity originalSlot = new TargetSlotEntity();
        originalSlot.setId(originalSlotId);
        originalSlot.setTargetId(targetId);
        originalSlot.setSlotOrder(1);
        originalSlot.setTargetTagIds("1;2;3");
        target.addSlot(originalSlot);

        ArgumentCaptor<TargetEntity> targetCapture = ArgumentCaptor.forClass(TargetEntity.class);

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(targetRepository.findTargetByUserIdAndTargetId(userId, targetId)).thenReturn(target);


        // call under test
        targetService.deleteTarget(userName, targetId);

        Mockito.verify(targetRepository).delete(targetCapture.capture());
        Mockito.verify(targetSlotRepository).deleteAll(any(List.class));

        TargetEntity capturedTarget = targetCapture.getValue();
        Assertions.assertNotNull(capturedTarget, "Captured target should exist");
        Assertions.assertNull(capturedTarget.getSlots(), "Captured target should not contain slots");

    }

    @Test
    void updateTarget() throws Exception {
        String newname = "New Name";
        String userName = "user@name.com";
        Long userId = 20L;
        Long targetId = 99L;
        UserEntity user = new UserEntity();
        user.setUsername(userName);
        user.setId(userId);
        TargetEntity target = new TargetEntity();
        target.setTargetId(targetId);
        target.setProposalId(9999L);
        target.setTargetName("old name");
        TargetEntity editTarget = new TargetEntity();
        editTarget.setTargetName(newname);
        editTarget.setTargetId(targetId);

        ArgumentCaptor<TargetEntity> targetCapture = ArgumentCaptor.forClass(TargetEntity.class);

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(targetRepository.findTargetByUserIdAndTargetId(userId, targetId)).thenReturn(target);
        Mockito.when(targetRepository.save(targetCapture.capture())).thenReturn(null);

        // call under test
        targetService.updateTarget(userName, editTarget);

        TargetEntity capturedTarget = targetCapture.getValue();
        Assertions.assertNotNull(capturedTarget, "Captured target should exist");
        Assertions.assertEquals(newname, capturedTarget.getTargetName(), "name has been changed");
    }

    @Test
    void addSlotToTarget() throws Exception {
        String userName = "user@name.com";
        Long userId = 20L;
        Long originalSlotId = 900L;
        Long targetId = 99L;
        UserEntity user = new UserEntity();
        user.setUsername(userName);
        user.setId(userId);
        TargetEntity target = new TargetEntity();
        target.setTargetId(targetId);
        target.setProposalId(9999L);
        TargetSlotEntity addedSlot = new TargetSlotEntity();
        addedSlot.setTargetTagIds("1;2;3");
        TargetSlotEntity originalSlot = new TargetSlotEntity();
        originalSlot.setId(originalSlotId);
        originalSlot.setTargetId(targetId);
        originalSlot.setSlotOrder(1);
        originalSlot.setTargetTagIds("1;2;3");
        target.addSlot(originalSlot);

        ArgumentCaptor<TargetSlotEntity> targetSlotCapture = ArgumentCaptor.forClass(TargetSlotEntity.class);
        ArgumentCaptor<TargetEntity> targetCapture = ArgumentCaptor.forClass(TargetEntity.class);

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(targetRepository.findTargetByUserIdAndTargetId(userId, targetId)).thenReturn(target);
        Mockito.when(targetSlotRepository.save(targetSlotCapture.capture())).thenReturn(addedSlot);
        Mockito.when(targetRepository.save(targetCapture.capture())).thenReturn(null);


        // call under test
        targetService.addSlotToTarget(userName, targetId, addedSlot);

        // verify test
        TargetEntity capturedTarget = targetCapture.getValue();
        Assertions.assertNotNull(capturedTarget, "Captured target should exist");
        Assertions.assertEquals(2, capturedTarget.getSlots().size(), "target should contain 2 slots");
        TargetSlotEntity capturedSlot = targetSlotCapture.getValue();
        Assertions.assertNotNull(capturedSlot, "Captured target slot should exist");
        Assertions.assertEquals("2", capturedSlot.getSlotOrder().toString(), "slot should have order of 2");
        Assertions.assertEquals(targetId, capturedSlot.getTargetId(), "slot should have correct target id");
    }

    @Test
    void deleteSlotFromTarget() throws Exception {
        String userName = "user@name.com";
        Long userId = 20L;
        Long slotId = 500L;
        Long remainingSlotId = 900L;
        Long targetId = 99L;
        UserEntity user = new UserEntity();
        user.setUsername(userName);
        user.setId(userId);
        TargetEntity target = new TargetEntity();
        target.setTargetId(targetId);
        target.setProposalId(9999L);
        TargetSlotEntity targetSlot = new TargetSlotEntity();
        targetSlot.setId(slotId);
        targetSlot.setTargetId(targetId);
        targetSlot.setTargetTagIds("1;2;3");
        TargetSlotEntity remainingSlot = new TargetSlotEntity();
        remainingSlot.setId(remainingSlotId);
        remainingSlot.setTargetId(targetId);
        remainingSlot.setTargetTagIds("1;2;3");
        target.addSlot(targetSlot);
        target.addSlot(remainingSlot);

        ArgumentCaptor<TargetSlotEntity> targetSlotCapture = ArgumentCaptor.forClass(TargetSlotEntity.class);
        ArgumentCaptor<TargetEntity> targetCapture = ArgumentCaptor.forClass(TargetEntity.class);

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(targetRepository.findTargetByUserIdAndTargetId(userId, targetId)).thenReturn(target);
        Mockito.when(targetSlotRepository.findById(slotId)).thenReturn(Optional.of(targetSlot));
        Mockito.when(targetRepository.save(targetCapture.capture())).thenReturn(null);

        // test call
        targetService.deleteSlotFromTarget(userName, targetId, slotId);

        // verify afterwards
        Mockito.verify(targetSlotRepository).delete(targetSlotCapture.capture());
        TargetEntity capturedTarget = targetCapture.getValue();
        Assertions.assertNotNull(capturedTarget, "Captured target should exist");
        Assertions.assertEquals(1, capturedTarget.getSlots().size(), "target should contain 1 slot");
    }

    @Test
    void addTagToTargetSlot() throws Exception {
        String userName = "user@name.com";
        Long userId = 20L;
        Long tagId = 50L;
        Long slotId = 500L;
        Long targetId = 99L;
        UserEntity user = new UserEntity();
        user.setUsername(userName);
        user.setId(userId);
        TargetEntity target = new TargetEntity();
        target.setTargetId(targetId);
        target.setProposalId(9999L);
        TargetSlotEntity targetSlot = new TargetSlotEntity();
        targetSlot.setTargetId(targetId);
        targetSlot.setTargetTagIds("1;2;3");

        ArgumentCaptor<TargetSlotEntity> targetSlotCapture = ArgumentCaptor.forClass(TargetSlotEntity.class);

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(targetRepository.findTargetByUserIdAndTargetId(userId, targetId)).thenReturn(target);
        Mockito.when(targetSlotRepository.findById(slotId)).thenReturn(Optional.of(targetSlot));
        Mockito.when(targetSlotRepository.save(targetSlotCapture.capture())).thenReturn(null);

        // test service call
        targetService.addTagToTargetSlot(userName, targetId, slotId, tagId);

        TargetSlotEntity capturedSlot = targetSlotCapture.getValue();
        Assertions.assertNotNull(capturedSlot, "Captured slot should exist");
        Assertions.assertEquals(capturedSlot.getTargetTagIds(), "1;2;3;50", "Slot should contain tag 50");
    }

    @Test
    void deleteTagFromTargetSlot() throws Exception {
        String userName = "user@name.com";
        Long userId = 20L;
        Long tagId = 50L;
        Long slotId = 500L;
        Long targetId = 99L;
        UserEntity user = new UserEntity();
        user.setUsername(userName);
        user.setId(userId);
        TargetEntity target = new TargetEntity();
        target.setTargetId(targetId);
        target.setProposalId(9999L);
        TargetSlotEntity targetSlot = new TargetSlotEntity();
        targetSlot.setTargetId(targetId);
        targetSlot.setTargetTagIds("1;2;50;3");

        ArgumentCaptor<TargetSlotEntity> targetSlotCapture = ArgumentCaptor.forClass(TargetSlotEntity.class);

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(targetRepository.findTargetByUserIdAndTargetId(userId, targetId)).thenReturn(target);
        Mockito.when(targetSlotRepository.findById(slotId)).thenReturn(Optional.of(targetSlot));
        Mockito.when(targetSlotRepository.save(targetSlotCapture.capture())).thenReturn(null);

        // test service call
        targetService.deleteTagFromTargetSlot(userName, targetId, slotId, tagId);

        TargetSlotEntity capturedSlot = targetSlotCapture.getValue();
        Assertions.assertNotNull(capturedSlot, "Captured slot should exist");
        Assertions.assertEquals(capturedSlot.getTargetTagIds(), "1;2;3", "Slot should not contain tag 50");
    }

    @Test
    void addTagToTarget() throws Exception {
        String userName = "user@name.com";
        Long userId = 20L;
        Long tagId = 50L;
        Long targetId = 99L;
        UserEntity user = new UserEntity();
        user.setUsername(userName);
        user.setId(userId);
        TargetEntity target = new TargetEntity();
        target.setTargetId(targetId);
        target.setTargetTagIds("1;2;3");
        target.setProposalId(9999L);

        ArgumentCaptor<TargetEntity> targetCapture = ArgumentCaptor.forClass(TargetEntity.class);

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(targetRepository.findTargetByUserIdAndTargetId(userId, targetId)).thenReturn(target);
        Mockito.when(targetRepository.save(targetCapture.capture())).thenReturn(null);

        // test service call
        targetService.addTagToTarget(userName, targetId, tagId);

        TargetEntity capturedTarget = targetCapture.getValue();
        Assertions.assertNotNull(capturedTarget, "Should have captured value");
        Assertions.assertNull(capturedTarget.getProposalId(), "Target should have null proposal id");
        Assertions.assertEquals("1;2;3;50", capturedTarget.getTargetTagIds(), "Target should contain new tagId");
    }

    @Test
    void testAddDefaultTargetSlot() throws Exception {
        String userName = "user@name.com";
        Long userId = 20L;
        Long targetId = 99L;
        UserEntity user = new UserEntity();
        user.setUsername(userName);
        user.setId(userId);
        TargetEntity target = new TargetEntity();
        target.setTargetId(targetId);
        target.setTargetTagIds("1;2;3");
        TargetSlotEntity targetSlot = new TargetSlotEntity();
        targetSlot.setSlotDishTagId(TagService.MAIN_DISH_TAG_ID);
        targetSlot.setTargetId(targetId);
        targetSlot.setSlotOrder(1);

        ArgumentCaptor<TargetSlotEntity> slotCapture = ArgumentCaptor.forClass(TargetSlotEntity.class);
        ArgumentCaptor<TargetEntity> targetCapture = ArgumentCaptor.forClass(TargetEntity.class);
        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(targetRepository.findTargetByUserIdAndTargetId(userId, targetId)).thenReturn(target);
        Mockito.when(targetSlotRepository.save(slotCapture.capture())).thenReturn(null);
        Mockito.when(targetRepository.save(targetCapture.capture())).thenReturn(null);

        targetService.addDefaultTargetSlot(userName, target);

        // check that slot has slot order of 1, and filled in target id
        TargetSlotEntity capturedSlot = slotCapture.getValue();
        Assertions.assertNotNull(capturedSlot, "TargetSlotEntity should be captured");
        Assertions.assertEquals(1L, (long) capturedSlot.getSlotOrder(), "order should be 1");
        Assertions.assertEquals(targetId, capturedSlot.getTargetId(), "targetId should be as declared");
        // check that saved target has one slot
        TargetEntity capturedTarget = targetCapture.getValue();
        Assertions.assertNotNull(capturedTarget, "TargetEntity should be captured");
        Assertions.assertNotNull(capturedTarget.getSlots(), "Target should have slots");
        Assertions.assertEquals(1, capturedTarget.getSlots().size(), "Target should have 1 slot");
    }

    @Test
    void deleteTagFromTarget() throws Exception {
        String userName = "user@test.com";
        Long targetId = 99L;
        Long tagId = 1L;
        Long userId = 100L;

        UserEntity user = new UserEntity();
        user.setUsername(userName);
        user.setId(userId);
        TargetEntity target = new TargetEntity();
        target.setTargetTagIds("1;2;3");

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(targetRepository.findTargetByUserIdAndTargetId(userId, targetId)).thenReturn(target);
        ArgumentCaptor<TargetEntity> argument = ArgumentCaptor.forClass(TargetEntity.class);
        Mockito.when(targetRepository.save(argument.capture())).thenReturn(null);

        // test call
        targetService.deleteTagFromTarget(userName, targetId, tagId);

        // check capture
        TargetEntity result = argument.getValue();
        Assertions.assertNotNull(result, "Call made - and capture exists");
        Assertions.assertEquals("2;3", result.getTargetTagIds(), "1 should no longer be in list");
    }
}
