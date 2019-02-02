package com.meg.atable.lmt.service.impl;

import com.meg.atable.Application;
import com.meg.atable.lmt.data.entity.DishEntity;
import com.meg.atable.lmt.service.DishService;
import com.meg.atable.lmt.service.Instruction;
import com.meg.atable.lmt.service.tag.AutoTagSubject;
import com.meg.atable.test.TestConstants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class TextProcessorImplTest {

    @Autowired
    TextProcessorImpl textProcessor;

    @Autowired
    DishService dishService;

    @Test
    public void fillInstructions() {
        // 2 instructions
        // 1 for text "Soup" which assigns to tag "Soup" (id: 301)
        // 1 for text "Crockpot" which assigns to tag "Crockpot" (id: 323)

        textProcessor.fillInstructions();
        // test that fill instructions results in 2 instructions loaded
        List<Instruction> list = textProcessor.getInstructions();
        Assert.assertNotNull(list);
        Assert.assertEquals(2,list.size());
    }

    @Test
    public void processTagForInstruction() {
        // get instructions
        textProcessor.fillInstructions();
        List<Instruction> instructions = textProcessor.getInstructions();
        Instruction soup = instructions.stream().filter(i -> i.getAssignTagId().equals(301L)).findFirst().get();
        Instruction crockpot = instructions.stream().filter(i -> i.getAssignTagId().equals(323L)).findFirst().get();

        // test dish -dish tag 1 - Israeli Couscous
        DishEntity dishEntity = dishService.getDishForUserById(TestConstants.USER_3_NAME, TestConstants.DISH_1_ID);
        AutoTagSubject subject = new AutoTagSubject(dishEntity, false);
        Long assignedId = textProcessor.processTagForInstruction(soup, subject);
        Assert.assertNull(assignedId);
        assignedId = textProcessor.processTagForInstruction(crockpot,subject);
        Assert.assertNull(assignedId);

        // test dish - 25 - Ham and Potato Soup
        dishEntity = dishService.getDishForUserById(TestConstants.USER_3_NAME, 25L);
        subject = new AutoTagSubject(dishEntity, false);
        assignedId = textProcessor.processTagForInstruction(soup,subject);
        Assert.assertNotNull(assignedId);
        Assert.assertEquals(301L,assignedId.longValue());
        assignedId = textProcessor.processTagForInstruction(crockpot,subject);
        Assert.assertNull(assignedId);

        // test dish - 81 - Crock Pot Jambalaya
        dishEntity = dishService.getDishForUserById(TestConstants.USER_3_NAME, 81L);
        subject = new AutoTagSubject(dishEntity, false);
        assignedId = textProcessor.processTagForInstruction(soup,subject);
        Assert.assertNull(assignedId);
        assignedId = textProcessor.processTagForInstruction(crockpot,subject);
        Assert.assertNotNull(assignedId);
        Assert.assertEquals(323L,assignedId.longValue());
    }
}