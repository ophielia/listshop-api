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

    @Test
    public void deleteTarget() throws Exception {
        targetService.deleteTarget(TestConstants.USER_1_EMAIL, targetIdToDelete);

        TargetEntity result = targetService.getTargetById(TestConstants.USER_1_EMAIL, targetIdToDelete);
        Assert.assertNull(result);
    }

    @Test
    public void updateTarget() throws Exception {
        String newname = "New Name";
        TargetEntity toEdit = targetService.getTargetById(TestConstants.USER_1_EMAIL, targetIdToEdit);
        toEdit.setTargetName(newname);

        targetService.updateTarget(TestConstants.USER_1_EMAIL, toEdit);

        TargetEntity result = targetService.getTargetById(TestConstants.USER_1_EMAIL, targetIdToEdit);
        Assert.assertEquals(newname, result.getTargetName());
    }

}