package com.meg.atable.lmt.service.tag.impl;

import com.meg.atable.Application;
import com.meg.atable.lmt.data.entity.DishEntity;
import com.meg.atable.lmt.data.entity.TagEntity;
import com.meg.atable.lmt.service.DishService;
import com.meg.atable.lmt.service.Instruction;
import com.meg.atable.lmt.service.tag.AutoTagSubject;
import com.meg.atable.lmt.service.tag.TagService;
import com.meg.atable.test.TestConstants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.meg.atable.test.TestConstants.USER_3_NAME;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class TagProcessorImplTest {

    @Autowired
    TagProcessorImpl tagProcessor;

    @Autowired
    DishService dishService;

    @Autowired
    TagService tagService;

    @Test
    public void fillInstructions() {
        // 2 instructions
        // 1 for tag "Meat" which assigns to tag "Meat" (id: 346)
        // 1 for text "Vegetarian" which assigns to tag "Vegetarian" (id: 199)

        tagProcessor.fillInstructions();
        // test that fill instructions results in 2 instructions loaded
        List<Instruction> list = tagProcessor.getInstructions();
        Assert.assertNotNull(list);
        Assert.assertTrue(list.size() >= 2);
    }

    @Test
    public void processTagForInstruction() {
        // get instructions
        tagProcessor.fillInstructions();
        List<Instruction> instructions = tagProcessor.getInstructions();
        Instruction vegetarian = instructions.stream().filter(i -> i.getAssignTagId().equals(199L)).findFirst().get();

        // test dish -dish tag 1 - Israeli Couscous
        DishEntity dishEntity = dishService.getDishForUserById(USER_3_NAME, TestConstants.DISH_1_ID);
        List<TagEntity> tags = tagService.getTagsForDish(USER_3_NAME, TestConstants.DISH_1_ID);
        Set<Long> tagIds = tags.stream().map(TagEntity::getId).collect(Collectors.toSet());
        AutoTagSubject subject = new AutoTagSubject(dishEntity, false);
        subject.setTagIdsForDish(tagIds);
        Long assignedId = tagProcessor.processTagForInstruction(vegetarian, subject);
        Assert.assertNotNull(assignedId);
        Assert.assertEquals(199L, assignedId.longValue());
        assignedId = tagProcessor.processTagForInstruction(vegetarian, subject);
        Assert.assertNotNull(assignedId);

        /*

        // test meaty dish - should be 346 found, and 199 not found
        // 28 porcupines
        dishEntity = dishService.getDishForUserById(USER_3_NAME, 28L);
        tags = tagService.getTagsForDish(USER_3_NAME, 28L);
        tagIds = tags.stream().map(TagEntity::getId).collect(Collectors.toSet());
        subject = new AutoTagSubject(dishEntity, false);
        subject.setTagIdsForDish(tagIds);
        assignedId = tagProcessor.processTagForInstruction(meat, subject);
        Assert.assertNotNull(assignedId);
        Assert.assertEquals(346L, assignedId.longValue());
        assignedId = tagProcessor.processTagForInstruction(vegetarian, subject);
        Assert.assertNull(assignedId);


        // 47 chicken cassolet
        dishEntity = dishService.getDishForUserById(USER_3_NAME, 47L);
        tags = tagService.getTagsForDish(USER_3_NAME, 47L);
        tagIds = tags.stream().map(TagEntity::getId).collect(Collectors.toSet());
        subject = new AutoTagSubject(dishEntity, false);
        subject.setTagIdsForDish(tagIds);
        assignedId = tagProcessor.processTagForInstruction(meat, subject);
        Assert.assertNotNull(assignedId);
        Assert.assertEquals(346L, assignedId.longValue());
        assignedId = tagProcessor.processTagForInstruction(vegetarian, subject);
        Assert.assertNull(assignedId);

        // test dish - 25 - Ham and Potato Soup - should be tagged meat
        dishEntity = dishService.getDishForUserById(USER_3_NAME, 25L);
        tags = tagService.getTagsForDish(USER_3_NAME, 25L);
        tagIds = tags.stream().map(TagEntity::getId).collect(Collectors.toSet());
        subject = new AutoTagSubject(dishEntity, false);
        subject.setTagIdsForDish(tagIds);
        assignedId = tagProcessor.processTagForInstruction(meat, subject);
        Assert.assertNotNull(assignedId);
        Assert.assertEquals(346L, assignedId.longValue());
        assignedId = tagProcessor.processTagForInstruction(vegetarian, subject);
        Assert.assertNull(assignedId);
         */

    }
}