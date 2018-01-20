package com.meg.atable.service.impl;

import com.meg.atable.Application;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.entity.TargetEntity;
import com.meg.atable.data.entity.TargetSlotEntity;
import com.meg.atable.data.repository.TargetRepository;
import com.meg.atable.data.repository.TargetSlotRepository;
import com.meg.atable.service.tag.TagService;
import com.meg.atable.service.TargetService;
import org.junit.Assert;
import org.junit.Before;
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
    private static UserAccountEntity userAccount;
    private static UserAccountEntity newUserAccount;
    private static TagEntity tag1;
    private static TagEntity tag21;
    private static TagEntity tag31;
    private static TargetEntity target1;
    private static TargetEntity target2;
    private static TargetEntity target3;
    private static TargetSlotEntity targetSlotEntity;
    private static TagEntity dishTypeTag;

    @Before
    public void setUp() throws Exception {
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
        userAccount = userService.save(new UserAccountEntity(userName, "password"));
        newUserAccount = userService.save(new UserAccountEntity("newUser", "password"));

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
        List<TargetEntity> result = targetService.getTargetsForUserName(userAccount.getUsername());


        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());

    }

    @Test
    public void createTarget() throws Exception {
        TargetEntity newTarget = new TargetEntity();
        newTarget.setTargetName("george");

        TargetEntity result = targetService.createTarget(newUserAccount.getUsername(), newTarget);

        Assert.assertNotNull(result);
        Assert.assertEquals("george", result.getTargetName());
        Assert.assertNotNull(result.getTargetId());
        Assert.assertNotNull(result.getUserId());
        Assert.assertNull(result.getLastUsed());
        Assert.assertNotNull(result.getCreated());

    }

    @Test
    public void getTargetById() throws Exception {
        TargetEntity result = targetService.getTargetById(userAccount.getUsername(), target1.getTargetId());

        Assert.assertNotNull(result);
        Assert.assertEquals(target1.getTargetId(), result.getTargetId());
        Assert.assertEquals(target1.getUserId(), result.getUserId());
        Assert.assertNotNull(result.getCreated());
    }

    @Test
    public void deleteTarget() throws Exception {
        Long id = target2.getTargetId();
        targetService.deleteTarget(userAccount.getUsername(), id);

        TargetEntity result = targetService.getTargetById(userAccount.getUsername(), id);
        Assert.assertNull(result);
    }

    @Test
    public void updateTarget() throws Exception {
        String newname = "New Name";
        target1.setTargetName(newname);

        targetService.updateTarget(userAccount.getUsername(), target1);

        TargetEntity result = targetService.getTargetById(userAccount.getUsername(), target1.getTargetId());
        Assert.assertEquals(newname, result.getTargetName());
    }

    @Test
    public void addSlotToTarget() throws Exception {
        TargetEntity targetEntity = new TargetEntity();
        targetEntity.setTargetName("new Target slots");
        targetEntity = targetService.createTarget(newUserAccount.getUsername(), targetEntity);
        int size = targetEntity.getSlots() != null ? targetEntity.getSlots().size() : 0;
        TargetSlotEntity slotEntity = new TargetSlotEntity();
        slotEntity.setSlotDishTagId(dishTypeTag.getId());

        targetService.addSlotToTarget(newUserAccount.getUsername(), targetEntity.getTargetId(), slotEntity);
        targetEntity = targetService.getTargetById(newUserAccount.getUsername(), targetEntity.getTargetId());
        List<TargetSlotEntity> result = targetEntity.getSlots();
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.get(size));
        Assert.assertEquals(targetEntity.getTargetId(), result.get(size).getTargetId());
        Assert.assertTrue(size + 1 == result.get(size).getSlotOrder());
        Assert.assertEquals(dishTypeTag.getId(), result.get(size).getSlotDishTagId());
    }

    @Test
    public void deleteSlotFromTarget() throws Exception {

        targetService.deleteSlotFromTarget(newUserAccount.getUsername(), target3.getTargetId(), targetSlotEntity.getId());

        TargetEntity targetEntity = targetService.getTargetById(newUserAccount.getUsername(), target3.getTargetId());
        List<TargetSlotEntity> result = targetEntity.getSlots();
        Assert.assertNotNull(result);
        Optional<TargetSlotEntity> foundSlot = targetEntity.getSlots().stream()
                .filter(t -> t.getId().longValue() == targetSlotEntity.getId().longValue()).findFirst();
        Assert.assertFalse(foundSlot.isPresent());
    }

    @Test
    public void addTagToTargetSlot() throws Exception {
        TargetEntity target = targetService.getTargetById(userAccount.getUsername(), target2.getTargetId());
        TargetSlotEntity slot = target.getSlots().get(0);
        Long slotId = slot.getId();
        String slottags = slot.getTargetTagIds() == null ? "" : slot.getTargetTagIds();
        Assert.assertFalse(slottags.contains(String.valueOf(tag1.getId())));
        targetService.addTagToTargetSlot(userAccount.getUsername(), target.getTargetId(), slotId, tag1.getId());

        target = targetService.getTargetById(userAccount.getUsername(), target2.getTargetId());
        slot = target.getSlots().get(0);
        slotId = slot.getId();
        slottags = slot.getTargetTagIds();
        Assert.assertNotNull(slottags);
        Assert.assertTrue(slottags.contains(String.valueOf(tag1.getId())));
    }

    @Test
    public void deleteTagFromTargetSlot() throws Exception {
        TargetEntity target = targetService.getTargetById(newUserAccount.getUsername(), target3.getTargetId());
        TargetSlotEntity slot = target.getSlots().get(0);
        Long slotId = slot.getId();
        String slottags = slot.getTargetTagIds() == null ? "" : slot.getTargetTagIds();
        Assert.assertTrue(slottags.contains(String.valueOf(tag1.getId())));
        targetService.deleteTagFromTargetSlot(userAccount.getUsername(), target.getTargetId(), slotId, tag1.getId());

        target = targetService.getTargetById(newUserAccount.getUsername(), target3.getTargetId());
        slot = target.getSlots().get(0);
        slotId = slot.getId();
        slottags = slot.getTargetTagIds();
        Assert.assertNotNull(slottags);
        Assert.assertFalse(slottags.contains(String.valueOf(tag1.getId())));

    }

    @Test
    public void addTagToTarget() throws Exception {
        TargetEntity target = targetService.getTargetById(userAccount.getUsername(), target2.getTargetId());
        String targettags = target.getTargetTagIds() == null ? "" : target.getTargetTagIds();
        Assert.assertFalse(targettags.contains(String.valueOf(tag1.getId())));
        targetService.addTagToTarget(userAccount.getUsername(), target.getTargetId(),  tag1.getId());

        target = targetService.getTargetById(userAccount.getUsername(), target2.getTargetId());
        targettags = target.getTargetTagIds();
        Assert.assertNotNull(targettags);
        Assert.assertTrue(targettags.contains(String.valueOf(tag1.getId())));
    }

    @Test
    public void deleteTagFromTarget() throws Exception {
        TargetEntity target = targetService.getTargetById(newUserAccount.getUsername(), target3.getTargetId());
        String targettags = target.getTargetTagIds() != null ?target.getTargetTagIds():"";
        Assert.assertTrue(targettags.contains(String.valueOf(tag1.getId())));
        targetService.deleteTagFromTarget(newUserAccount.getUsername(), target.getTargetId(), tag1.getId());

        target = targetService.getTargetById(newUserAccount.getUsername(), target3.getTargetId());
        targettags = target.getTargetTagIds();
        Assert.assertNotNull(targettags);
        Assert.assertFalse(targettags.contains(String.valueOf(tag1.getId())));

    }
}