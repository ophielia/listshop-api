/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.data.entity.TargetEntity;
import com.meg.listshop.lmt.data.entity.TargetSlotEntity;
import com.meg.listshop.lmt.data.repository.TargetRepository;
import com.meg.listshop.lmt.data.repository.TargetSlotRepository;
import com.meg.listshop.lmt.service.TargetService;
import com.meg.listshop.lmt.service.tag.TagService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class TargetServiceImplMockTest {

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

    @Before
    public void setUp() throws Exception {
        targetService = new TargetServiceImpl(userService, targetRepository, targetSlotRepository, tagService);
    }

    /*
       @Before
       public void setUp() throws Exception {
           TargetEntity newTarget = new TargetEntity();
           newTarget.setTargetName("george");

           TargetEntity result = targetService.createTarget(TestConstants.USER_1_EMAIL, newTarget);
   targetIdToDelete = result.getTargetId();

           result = targetService.createTarget(TestConstants.USER_1_EMAIL, newTarget);
           targetIdToEdit = result.getTargetId();
           if (setUpComplete) {
               return;
           }
           // make tags
           dishTypeTag = new TagEntity("dishTypeTag", "main1");
           tag1 = new TagEntity("tag1", "main1");
           tag21 = new TagEntity("tag1", "main1");
           tag31 = new TagEntity("tag1", "main1");

           tag1 = tagService.save(tag1);
           dishTypeTag = tagService.save(dishTypeTag);
           tag21 = tagService.save(tag21);
           tag31 = tagService.save(tag31);
           String tagString = tag1.getId() + ";" + tag21.getId();
           // make users
           String userName = "targetServiceTest";
           userAccount = userService.save(new UserEntity(userName, "password"));
           newUserAccount = userService.save(new UserEntity("newUser", "password"));

           TargetEntity targetEntity = new TargetEntity();
           targetEntity.setUserId(userAccount.getId());
           targetEntity.setTargetName("testTarget");
           targetEntity.setTargetTagIds(tagString);
           targetEntity.setSlots(null);
           targetEntity.setCreated(new Date());
           target1 = targetRepository.save(targetEntity);
           TargetSlotEntity slot = new TargetSlotEntity();
           slot.setTargetId(target1.getTargetId());
           slot.setSlotOrder(1);
           slot.setSlotDishTagId(dishTypeTag.getId());
           targetSlotRepository.save(slot);
           target1.addSlot(slot);
           targetRepository.save(target1);


           targetEntity = new TargetEntity();
           targetEntity.setUserId(userAccount.getId());
           targetEntity.setTargetName("testTarget");
           targetEntity.setTargetTagIds(tagString);
           targetEntity.setSlots(null);
           targetEntity.setCreated(new Date());

           target2 = targetRepository.save(targetEntity);
           targetSlotEntity = new TargetSlotEntity();
           targetSlotEntity.setTargetId(target2.getTargetId());
           targetSlotEntity.setSlotDishTagId(dishTypeTag.getId());
           targetSlotEntity = targetSlotRepository.save(targetSlotEntity);
           target2.addSlot(targetSlotEntity);
           target2 = targetRepository.save(targetEntity);

           targetEntity = new TargetEntity();
           targetEntity.setUserId(newUserAccount.getId());
           targetEntity.setTargetName("testTarget3");
           targetEntity.setTargetTagIds(tagString);
           targetEntity.setSlots(null);
           targetEntity.setCreated(new Date());

           target3 = targetRepository.save(targetEntity);
           targetSlotEntity = new TargetSlotEntity();
           targetSlotEntity.setTargetId(target3.getTargetId());
           targetSlotEntity.setSlotDishTagId(dishTypeTag.getId());
           targetSlotEntity.setTargetTagIds(tag1.getId().toString());
           targetSlotEntity = targetSlotRepository.save(targetSlotEntity);
           target3.addSlot(targetSlotEntity);
           target3 = targetRepository.save(target3);
           setUpComplete = true;
       }


       @Test
       public void getTargetsForUserName() throws Exception {
           List<TargetEntity> result = targetService.getTargetsForUserName(TestConstants.USER_3_NAME, false);


           Assert.assertNotNull(result);
           Assert.assertEquals(2, result.size());

       }

       @Test
       public void getTargetsForUserName_WithTemporary() throws Exception {
           List<TargetEntity> result = targetService.getTargetsForUserName(TestConstants.USER_3_NAME, true);


           Assert.assertNotNull(result);
           Assert.assertEquals(2, result.size());

       }

       @Test
       public void createTarget() throws Exception {
           TargetEntity newTarget = new TargetEntity();
           newTarget.setTargetName("george");

           TargetEntity result = targetService.createTarget(TestConstants.USER_1_EMAIL, newTarget);

           Assert.assertNotNull(result);
           Assert.assertEquals("george", result.getTargetName());
           Assert.assertNotNull(result.getTargetId());
           Assert.assertNotNull(result.getUserId());
           Assert.assertNull(result.getLastUsed());
           Assert.assertNotNull(result.getCreated());
           Assert.assertNull(result.getExpires());

       }

       @Test
       public void createTarget_Temporary() {
           TargetEntity newTarget = new TargetEntity();
           newTarget.setTargetName("george");
           newTarget.setTargetType(TargetType.PickUp);

           TargetEntity result = targetService.createTarget(TestConstants.USER_1_EMAIL, newTarget);

           Assert.assertNotNull(result);
           Assert.assertEquals("george", result.getTargetName());
           Assert.assertNotNull(result.getTargetId());
           Assert.assertNotNull(result.getUserId());
           Assert.assertNull(result.getLastUsed());
           Assert.assertNotNull(result.getCreated());
           Assert.assertNotNull(result.getExpires());

       }

       @Test
       public void getTargetById() throws Exception {
           TargetEntity result = targetService.getTargetById(TestConstants.USER_3_NAME, TestConstants.TARGET_1_ID);

           Assert.assertNotNull(result);
           Assert.assertEquals(TestConstants.TARGET_1_ID, result.getTargetId());
           Assert.assertEquals(TestConstants.USER_3_ID, result.getUserId());
           Assert.assertNotNull(result.getCreated());
       }
   */

    @Test
    public void deleteTarget() throws Exception {
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
        Assert.assertNotNull("Captured target should exist", capturedTarget);
        Assert.assertNull("Captured target should not contain slots", capturedTarget.getSlots());

    }

    @Test
    public void updateTarget() throws Exception {
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
        Assert.assertNotNull("Captured target should exist", capturedTarget);
        Assert.assertEquals("name has been changed", newname, capturedTarget.getTargetName());
    }

    @Test
    public void addSlotToTarget() throws Exception {
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
        Assert.assertNotNull("Captured target should exist", capturedTarget);
        Assert.assertEquals("target should contain 2 slots", 2, capturedTarget.getSlots().size());
        TargetSlotEntity capturedSlot = targetSlotCapture.getValue();
        Assert.assertNotNull("Captured target slot should exist", capturedSlot);
        Assert.assertEquals("slot should have order of 2", "2", capturedSlot.getSlotOrder().toString());
        Assert.assertEquals("slot should have correct target id", targetId, capturedSlot.getTargetId());
    }

    @Test
    public void deleteSlotFromTarget() throws Exception {
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
        Assert.assertNotNull("Captured target should exist", capturedTarget);
        Assert.assertEquals("target should contain 1 slot", 1, capturedTarget.getSlots().size());
    }

    @Test
    public void addTagToTargetSlot() throws Exception {
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
        Assert.assertNotNull("Captured slot should exist", capturedSlot);
        Assert.assertEquals("Slot should contain tag 50", capturedSlot.getTargetTagIds(), "1;2;3;50");
    }

    @Test
    public void deleteTagFromTargetSlot() throws Exception {
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
        Assert.assertNotNull("Captured slot should exist", capturedSlot);
        Assert.assertEquals("Slot should not contain tag 50", capturedSlot.getTargetTagIds(), "1;2;3");
    }

    @Test
    public void addTagToTarget() throws Exception {
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
        Assert.assertNotNull("Should have captured value", capturedTarget);
        Assert.assertNull("Target should have null proposal id", capturedTarget.getProposalId());
        Assert.assertEquals("Target should contain new tagId", "1;2;3;50", capturedTarget.getTargetTagIds());
    }

    @Test
    public void testAddDefaultTargetSlot() throws Exception {
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
        Assert.assertNotNull("TargetSlotEntity should be captured", capturedSlot);
        Assert.assertEquals("order should be 1", 1L, (long) capturedSlot.getSlotOrder());
        Assert.assertEquals("targetId should be as declared", targetId, capturedSlot.getTargetId());
        // check that saved target has one slot
        TargetEntity capturedTarget = targetCapture.getValue();
        Assert.assertNotNull("TargetEntity should be captured", capturedTarget);
        Assert.assertNotNull("Target should have slots", capturedTarget.getSlots());
        Assert.assertEquals("Target should have 1 slot", 1, capturedTarget.getSlots().size());
    }

    @Test
    public void deleteTagFromTarget() throws Exception {
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
        Assert.assertNotNull("Call made - and capture exists", result);
        Assert.assertEquals("1 should no longer be in list", "2;3", result.getTargetTagIds());
    }
}