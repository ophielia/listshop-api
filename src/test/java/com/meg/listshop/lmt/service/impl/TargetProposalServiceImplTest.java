package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.data.entity.TargetEntity;
import com.meg.listshop.lmt.data.entity.TargetSlotEntity;
import com.meg.listshop.lmt.service.TargetService;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class TargetProposalServiceImplTest {

    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();


    @Autowired
    private TargetService targetService;

    private static final String testUserName = "rufus";


    @Test
    public void createProposal() throws Exception {
        //TargetEntity target = targetService.getTargetById("rufus",9650L);
        TargetEntity target = createTarget3();


        // TODO targetProposalService.createTargetProposal(testUserName,targetId1);
    }


    private TargetEntity createTarget3() {
        // this is a small target with three slots,one slot with two tags, and one tag without quite so
        // many matches
        TargetEntity target2 = new TargetEntity();
        target2.setTargetName("target2");
        target2.addTargetTagId(95L);
        target2 = targetService.createTarget(testUserName, target2);
        TargetSlotEntity targetSlotEntity = new TargetSlotEntity();
        targetSlotEntity.setSlotDishTagId(5L);  // Main Dish
        targetSlotEntity.addTagId(155L);
        targetService.addSlotToTarget(testUserName, target2.getTargetId(), targetSlotEntity);
        targetSlotEntity = new TargetSlotEntity();
        targetSlotEntity.setSlotDishTagId(5L);  // Main Dish - another
        targetSlotEntity.addTagId(71L);
        targetService.addSlotToTarget(testUserName, target2.getTargetId(), targetSlotEntity);
        targetSlotEntity = new TargetSlotEntity();
        targetSlotEntity.setSlotDishTagId(5L);  // Main Dish - another
        targetSlotEntity.addTagId(61L);
        targetSlotEntity.addTagId(111L); // Soup
        targetService.addSlotToTarget(testUserName, target2.getTargetId(), targetSlotEntity);
        return targetService.getTargetById(testUserName, target2.getTargetId());

    }

}