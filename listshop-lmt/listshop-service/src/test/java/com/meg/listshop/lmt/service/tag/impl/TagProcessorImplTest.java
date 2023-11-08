package com.meg.listshop.lmt.service.tag.impl;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.pojos.AutoTagSubject;
import com.meg.listshop.lmt.data.pojos.Instruction;
import com.meg.listshop.lmt.service.DishService;
import com.meg.listshop.lmt.service.tag.TagService;
import com.meg.listshop.test.TestConstants;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.meg.listshop.test.TestConstants.USER_3_NAME;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class TagProcessorImplTest {

    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

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
    @Sql(value = {"/sql/com/meg/atable/lmt/tag/impl/TagProcessorImplTest_fillAlternateInstructions.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/com/meg/atable/lmt/tag/impl/TagProcessorImplTest_restoreOriginal.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void fillAlternateInstructions() {
        // 2 instructions
        // 1 for tag "Meat" which assigns to tag "Meat" (id: 346)
        // 1 for text "Vegetarian" which assigns to tag "Vegetarian" (id: 199)

        tagProcessor.fillInstructions();
        // test that fill instructions results in 2 instructions loaded
        List<Instruction> list = tagProcessor.getInstructions();
        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());
    }

    @Test
    @Sql(value = {"/sql/com/meg/atable/lmt/tag/impl/TagProcessorImplTest_restoreOriginal.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void processDishForInstructions() {

        // get instructions
        tagProcessor.fillInstructions();
        List<Instruction> instructions = tagProcessor.getInstructions();
        Instruction vegetarian = instructions.stream().filter(i -> i.getAssignTagId().equals(199L)).findFirst().get();
        Instruction meat = instructions.stream().filter(i -> i.getAssignTagId().equals(346L)).findFirst().get();

        // test dish -dish tag 1 - Israeli Couscous
        DishEntity dishEntity = dishService.getDishForUserById(USER_3_NAME, TestConstants.DISH_1_ID);
        List<TagEntity> tags = tagService.getTagsForDish(USER_3_NAME, TestConstants.DISH_1_ID);
        Set<Long> tagIds = tags.stream().map(TagEntity::getId).collect(Collectors.toSet());
        AutoTagSubject subject = new AutoTagSubject(dishEntity, false);
        subject.setTagIdsForDish(tagIds);
        Long assignedId = tagProcessor.processTagForInstruction(vegetarian, subject);
        //Assert.assertNotNull(assignedId);
        Assert.assertEquals(199L, assignedId.longValue());
        assignedId = tagProcessor.processTagForInstruction(vegetarian, subject);
        Assert.assertNotNull(assignedId);


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


        // 47 chicken cassolet - should be tagged meat
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


    }

    @Test
    @Sql(value = {"/sql/com/meg/atable/lmt/tag/impl/TagProcessorImplTest_restoreOriginal.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void processDishForInstruction_NewTag() throws BadParameterException {
        // add new tag
        TagEntity parentTag = tagService.getTagById(88L);
        TagEntity meatCake = new TagEntity();
        meatCake.setName("meat cake");
        meatCake.setTagType(TagType.Ingredient);
        meatCake = tagService.createTag(88L, meatCake, null);
        Long newTagId = meatCake.getId();
        Set<Long> newTagSet = new HashSet<>();
        newTagSet.add(newTagId);

        // add tag to dish which doesn't have meat
        tagService.addTagsToDish(TestConstants.USER_3_ID, TestConstants.DISH_1_ID, newTagSet);
        // get instructions
        tagProcessor.fillInstructions();
        List<Instruction> instructions = tagProcessor.getInstructions();
        Instruction vegetarian = instructions.stream().filter(i -> i.getAssignTagId().equals(199L)).findFirst().get();
        Instruction meat = instructions.stream().filter(i -> i.getAssignTagId().equals(346L)).findFirst().get();

        // test dish -dish tag 1 - Israeli Couscous
        DishEntity dishEntity = dishService.getDishForUserById(USER_3_NAME, TestConstants.DISH_1_ID);
        List<TagEntity> tags = tagService.getTagsForDish(USER_3_NAME, TestConstants.DISH_1_ID);
        Set<Long> tagIds = tags.stream().map(TagEntity::getId).collect(Collectors.toSet());
        AutoTagSubject subject = new AutoTagSubject(dishEntity, false);
        subject.setTagIdsForDish(tagIds);
        Long assignedId = tagProcessor.processTagForInstruction(meat, subject);
        Assert.assertNotNull(assignedId);
        Assert.assertEquals(346L, assignedId.longValue());

        // clean up by removing tag afterwards
        // add tag to dish which doesn't have meat
        tagService.removeTagsFromDish(TestConstants.USER_3_ID, TestConstants.DISH_1_ID, newTagSet);

    }

}
