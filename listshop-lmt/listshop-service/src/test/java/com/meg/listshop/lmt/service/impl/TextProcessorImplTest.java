package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.pojos.AutoTagSubject;
import com.meg.listshop.lmt.data.pojos.Instruction;
import com.meg.listshop.lmt.dish.DishService;
import com.meg.listshop.test.TestConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@Testcontainers
@ActiveProfiles("test")
class TextProcessorImplTest {

    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @Autowired
    TextProcessorImpl textProcessor;

    @Autowired
    DishService dishService;

    @Test
    void fillInstructions() {
        // 2 instructions
        // 1 for text "Soup" which assigns to tag "Soup" (id: 301)
        // 1 for text "Crockpot" which assigns to tag "Crockpot" (id: 323)

        textProcessor.fillInstructions();
        // test that fill instructions results in 2 instructions loaded
        List<Instruction> list = textProcessor.getInstructions();
        Assertions.assertNotNull(list);
        Assertions.assertEquals(2, list.size());
    }

    @Test
    void processTagForInstruction() {
        // get instructions
        textProcessor.fillInstructions();
        List<Instruction> instructions = textProcessor.getInstructions();
        Instruction soup = instructions.stream().filter(i -> i.getAssignTagId().equals(301L)).findFirst().get();
        Instruction crockpot = instructions.stream().filter(i -> i.getAssignTagId().equals(323L)).findFirst().get();

        // test dish -dish tag 1 - Israeli Couscous
        DishEntity dishEntity = dishService.getDishForUserById(TestConstants.USER_3_NAME, TestConstants.DISH_1_ID);
        AutoTagSubject subject = new AutoTagSubject(dishEntity, false);
        Long assignedId = textProcessor.processTagForInstruction(soup, subject);
        Assertions.assertNull(assignedId);
        assignedId = textProcessor.processTagForInstruction(crockpot, subject);
        Assertions.assertNull(assignedId);

        // test dish - 25 - Ham and Potato Soup
        dishEntity = dishService.getDishForUserById(TestConstants.USER_3_NAME, 25L);
        subject = new AutoTagSubject(dishEntity, false);
        assignedId = textProcessor.processTagForInstruction(soup, subject);
        Assertions.assertNotNull(assignedId);
        Assertions.assertEquals(301L, assignedId.longValue());
        assignedId = textProcessor.processTagForInstruction(crockpot, subject);
        Assertions.assertNull(assignedId);

        // test dish - 81 - Crock Pot Jambalaya
        dishEntity = dishService.getDishForUserById(TestConstants.USER_3_NAME, 81L);
        subject = new AutoTagSubject(dishEntity, false);
        assignedId = textProcessor.processTagForInstruction(soup, subject);
        Assertions.assertNull(assignedId);
        assignedId = textProcessor.processTagForInstruction(crockpot, subject);
        Assertions.assertNotNull(assignedId);
        Assertions.assertEquals(323L, assignedId.longValue());
    }
}
