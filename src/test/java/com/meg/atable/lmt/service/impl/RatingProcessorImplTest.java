package com.meg.atable.lmt.service.impl;

import com.meg.atable.Application;
import com.meg.atable.lmt.data.entity.DishEntity;
import com.meg.atable.lmt.data.entity.TagEntity;
import com.meg.atable.lmt.service.DishService;
import com.meg.atable.lmt.service.Instruction;
import com.meg.atable.lmt.service.tag.AutoTagSubject;
import com.meg.atable.lmt.service.tag.TagService;
import com.meg.atable.lmt.service.tag.impl.RatingTagProcessorImpl;
import com.meg.atable.lmt.service.tag.impl.TagProcessorImpl;
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
public class RatingProcessorImplTest {

    @Autowired
    RatingTagProcessorImpl tagProcessor;

    @Autowired
    DishService dishService;

    @Autowired
    TagService tagService;

    @Test
    public void fillInstructions() {

        // test that fill instructions results in 2 instructions loaded
        List<Instruction> list = tagProcessor.getInstructions();
        Assert.assertNotNull(list);
        Assert.assertEquals(8, list.size());
    }

    @Test
    public void processTagForInstruction() {
        // get instructions
        tagProcessor.fillInstructions();
        List<Instruction> instructions = tagProcessor.getInstructions();
        Instruction cheap = instructions.stream().filter(i -> i.getAssignTagId().equals(344L)).findFirst().get();;
        Instruction elegant = instructions.stream().filter(i -> i.getAssignTagId().equals(396L)).findFirst().get();;

        // test dish -dish tag 1 - Israeli Couscous
        DishEntity dishEntity = dishService.getDishForUserById(USER_3_NAME, TestConstants.DISH_1_ID);
        List<TagEntity> tags = tagService.getTagsForDish(USER_3_NAME, TestConstants.DISH_1_ID);
        Set<Long> tagIds = tags.stream().map(TagEntity::getId).collect(Collectors.toSet());
        AutoTagSubject subject = new AutoTagSubject(dishEntity, false);
        subject.setTagIdsForDish(tagIds);
        Long assignedId = tagProcessor.processTagForInstruction(cheap, subject);
        Assert.assertNull(assignedId);
        assignedId = tagProcessor.processTagForInstruction(elegant, subject);
        Assert.assertNotNull(assignedId);
        Assert.assertEquals(396L, assignedId.longValue());

    }
}