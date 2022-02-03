/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.Application;
import com.meg.listshop.auth.api.model.TargetType;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.entity.TargetEntity;
import com.meg.listshop.lmt.data.entity.TargetSlotEntity;
import com.meg.listshop.lmt.data.repository.TargetRepository;
import com.meg.listshop.lmt.data.repository.TargetSlotRepository;
import com.meg.listshop.lmt.service.TargetService;
import com.meg.listshop.lmt.service.tag.TagService;
import com.meg.listshop.test.TestConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class TargetServiceImplTest {

    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @Autowired
    private UserService userService;

    @Autowired
    private TagService tagService;

    @Autowired
    private TargetService targetService;

    @Autowired
    private TargetRepository targetRepository;

    @Autowired
    private TargetSlotRepository targetSlotRepository;

    private static boolean setUpComplete = false;
    private static UserEntity userAccount;
    private static UserEntity newUserAccount;
    private static TagEntity tag1;
    private static TagEntity tag21;
    private static TagEntity tag31;
    private static TargetEntity target1;
    private static TargetEntity target2;
    private static TargetEntity target3;
    private static TargetSlotEntity targetSlotEntity;
    private static TagEntity dishTypeTag;

    private Long targetIdToDelete;
    private Long targetIdToEdit;

    @Before
    public void setUp() throws Exception {
        TargetEntity newTarget = new TargetEntity();
        newTarget.setTargetName("george");

        TargetEntity result = targetService.createTarget(TestConstants.USER_1_NAME, newTarget);
targetIdToDelete = result.getTargetId();

        result = targetService.createTarget(TestConstants.USER_1_NAME,newTarget);
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

        TargetEntity result = targetService.createTarget(TestConstants.USER_1_NAME, newTarget);

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

        TargetEntity result = targetService.createTarget(TestConstants.USER_1_NAME, newTarget);

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

    @Test
    public void deleteTarget() throws Exception {
        targetService.deleteTarget(TestConstants.USER_1_NAME, targetIdToDelete);

        TargetEntity result = targetService.getTargetById(TestConstants.USER_1_NAME, targetIdToDelete);
        Assert.assertNull(result);
    }

    @Test
    public void updateTarget() throws Exception {
        String newname = "New Name";
        TargetEntity toEdit = targetService.getTargetById(TestConstants.USER_1_NAME, targetIdToEdit);
        toEdit.setTargetName(newname);

        targetService.updateTarget(TestConstants.USER_1_NAME, toEdit);

        TargetEntity result = targetService.getTargetById(TestConstants.USER_1_NAME, targetIdToEdit);
        Assert.assertEquals(newname, result.getTargetName());
    }

    @Test
    public void addSlotToTarget() throws Exception {
        TargetEntity targetEntity = new TargetEntity();
        targetEntity.setTargetName("new Target slots");
        targetEntity = targetService.createTarget(TestConstants.USER_1_NAME, targetEntity);
        int size = targetEntity.getSlots() != null ? targetEntity.getSlots().size() : 0;
        TargetSlotEntity slotEntity = new TargetSlotEntity();
        slotEntity.setSlotDishTagId(TestConstants.TAG_MAIN_DISH);

        targetService.addSlotToTarget(TestConstants.USER_1_NAME, targetEntity.getTargetId(), slotEntity);
        targetEntity = targetService.getTargetById(TestConstants.USER_1_NAME, targetEntity.getTargetId());
        List<TargetSlotEntity> result = targetEntity.getSlots();
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.get(size));
        Assert.assertEquals(targetEntity.getTargetId(), result.get(size).getTargetId());
        Assert.assertTrue(size + 1 == result.get(size).getSlotOrder());
        Assert.assertEquals(TestConstants.TAG_MAIN_DISH, result.get(size).getSlotDishTagId());
    }

    @Test
    public void deleteSlotFromTarget() throws Exception {
        targetService.deleteSlotFromTarget(TestConstants.USER_1_NAME, TestConstants.TARGET_2_ID, TestConstants.TARGET_SLOT_1_ID);

        TargetEntity targetEntity = targetService.getTargetById(TestConstants.USER_1_NAME, TestConstants.TARGET_2_ID);
        List<TargetSlotEntity> result = targetEntity.getSlots();
        Assert.assertNotNull(result);
        Optional<TargetSlotEntity> foundSlot = targetEntity.getSlots().stream()
                .filter(t -> t.getId().longValue() == TestConstants.TARGET_2_ID.longValue()).findFirst();
        Assert.assertFalse(foundSlot.isPresent());
    }

    @Test
    public void addTagToTargetSlot() throws Exception {
        TargetEntity target = targetService.getTargetById(TestConstants.USER_1_NAME, TestConstants.TARGET_2_ID);
        TargetSlotEntity slot = target.getSlots().get(0);
        Long slotId = slot.getId();
        String slottags = slot.getTargetTagIds() == null ? "" : slot.getTargetTagIds();
        Assert.assertFalse(slottags.contains(String.valueOf(TestConstants.TAG_CROCKPOT)));
        targetService.addTagToTargetSlot(TestConstants.USER_1_NAME, TestConstants.TARGET_2_ID, slotId, TestConstants.TAG_CROCKPOT);

        target = targetService.getTargetById(TestConstants.USER_1_NAME, TestConstants.TARGET_2_ID);
        Optional<TargetSlotEntity> slotOpt = target.getSlots().stream().filter(sl -> sl.getId().equals(slotId)).findFirst();
        Assert.assertTrue(slotOpt.isPresent());
        slot = slotOpt.get();
        slottags = slot.getTargetTagIds();
        Assert.assertNotNull(slottags);
        Assert.assertTrue(slottags.contains(String.valueOf(TestConstants.TAG_CROCKPOT)));
    }

    @Test
    public void deleteTagFromTargetSlot() throws Exception {
        TargetEntity target = targetService.getTargetById(TestConstants.USER_1_NAME, TestConstants.TARGET_2_ID);
        TargetSlotEntity slot = target.getSlots().get(0);
        Long slotId = slot.getId();
        String slottags = slot.getTargetTagIds() == null ? "" : slot.getTargetTagIds();
        Assert.assertTrue(slottags.contains(String.valueOf(TestConstants.TAG_SOUP)));
        targetService.deleteTagFromTargetSlot(TestConstants.USER_1_NAME, target.getTargetId(), slotId, TestConstants.TAG_SOUP);

        target = targetService.getTargetById(TestConstants.USER_1_NAME, TestConstants.TARGET_2_ID);
        for (TargetSlotEntity slotEntity : target.getSlots()) {
            if (!slotId.equals(slotEntity.getId())) {
                continue;
            }
            slottags = slotEntity.getTargetTagIds();
            Assert.assertNotNull(slottags);
            Assert.assertFalse(slottags.contains(String.valueOf(TestConstants.TAG_SOUP)));

        }
    }

    @Test
    public void addTagToTarget() throws Exception {
        TargetEntity target = targetService.getTargetById(TestConstants.USER_1_NAME, TestConstants.TARGET_2_ID);
        String targettags = target.getTargetTagIds() == null ? "" : target.getTargetTagIds();
        Assert.assertFalse(targettags.contains(String.valueOf(tag1.getId())));
        targetService.addTagToTarget(TestConstants.USER_1_NAME, target.getTargetId(),  tag1.getId());

        target = targetService.getTargetById(TestConstants.USER_1_NAME, TestConstants.TARGET_2_ID);
        targettags = target.getTargetTagIds();
        Assert.assertNotNull(targettags);
        Assert.assertTrue(targettags.contains(String.valueOf(tag1.getId())));
    }

    @Test
    public void testAddDefaultTargetSlot() throws Exception {
        TargetEntity target = targetService.getTargetById(TestConstants.USER_1_NAME, TestConstants.TARGET_2_ID);
TargetSlotEntity slotResult = targetService.addDefaultTargetSlot(TestConstants.USER_1_NAME, target);
        Assert.assertNotNull(slotResult);
        Assert.assertEquals(TagService.MAIN_DISH_TAG_ID,slotResult.getSlotDishTagId());
    }

    @Test
    public void deleteTagFromTarget() throws Exception {
        TargetEntity target = targetService.getTargetById(TestConstants.USER_1_NAME, TestConstants.TARGET_2_ID);
        String targettags = target.getTargetTagIds() != null ?target.getTargetTagIds():"";
        Assert.assertTrue(targettags.contains(String.valueOf(TestConstants.TAG_EASE_OF_PREP)));
        targetService.deleteTagFromTarget(TestConstants.USER_1_NAME, target.getTargetId(), TestConstants.TAG_EASE_OF_PREP);

        target = targetService.getTargetById(TestConstants.USER_1_NAME, TestConstants.TARGET_2_ID);
        targettags = target.getTargetTagIds();
        Assert.assertNotNull(targettags);
        Assert.assertFalse(targettags.contains(String.valueOf(TestConstants.TAG_EASE_OF_PREP)));

    }
}