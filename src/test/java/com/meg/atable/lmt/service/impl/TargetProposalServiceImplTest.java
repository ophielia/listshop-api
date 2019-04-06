package com.meg.atable.lmt.service.impl;

import com.meg.atable.Application;
import com.meg.atable.auth.data.entity.UserEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.lmt.data.entity.DishEntity;
import com.meg.atable.lmt.data.entity.TagEntity;
import com.meg.atable.lmt.data.entity.TargetEntity;
import com.meg.atable.lmt.data.entity.TargetSlotEntity;
import com.meg.atable.lmt.data.repository.DishRepository;
import com.meg.atable.lmt.data.repository.TagRepository;
import com.meg.atable.lmt.service.DishSearchService;
import com.meg.atable.lmt.service.ProposalService;
import com.meg.atable.lmt.service.TargetService;
import org.junit.Before;
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

@Autowired
private DishSearchService dishSearchService;


    @Autowired
    private ProposalService targetProposalService;

    @Autowired
    private TargetService targetService;

    @Autowired
    private UserService userService;


    @Autowired
    private TagRepository tagRepository;

    private static boolean setUpComplete = false;
    private static UserEntity userAccount;

    @Autowired
    private DishRepository dishRepository;
    private static TagEntity tag1;
    private static TagEntity tag2;
    private static TagEntity tag3;
    private static TagEntity tag4;
    private  static TagEntity tag5;

    private static DishEntity dish1;
    private static DishEntity dish2;
    private static DishEntity dish3;
    private static DishEntity dish4;
    private static DishEntity dish5;

    private static final String testUserName = "rufus";
    private static  Long targetId1;
    private static  Long targetId2;
    private static  Long targetId3;
    private static  Long targetId4;
    private static  Long targetId5;

    @Before
    public void setUp() {
        if (setUpComplete) {
            return;
        }
        TargetEntity target1 = createTarget1();
        TargetEntity target2 = createTarget2();
        TargetEntity target3 = createTarget3();
        TargetEntity target4 = createTarget4();
        TargetEntity target5 = createTarget5();

        targetId1 = target1.getTargetId();
        targetId2 = target2.getTargetId();
        targetId3 = target3.getTargetId();
        targetId4 = target4.getTargetId();
        targetId5 = target5.getTargetId();

        setUpComplete = true;
    }

    @Test
    public void createProposal() throws Exception {
        //TargetEntity target = targetService.getTargetById("rufus",9650L);
        TargetEntity target = createTarget3();


        // MM targetProposalService.createTargetProposal(testUserName,targetId1);
    }

    private TargetEntity createTarget1() {
        // this is a small target with only two slots, but lots of matches
        TargetEntity target1 = new TargetEntity();
        target1.setTargetName("target1");
        target1.addTargetTagId(95L);
        target1 = targetService.createTarget(testUserName, target1);
        TargetSlotEntity targetSlotEntity = new TargetSlotEntity();
        targetSlotEntity.setSlotDishTagId(5L);  // Main Dish
        targetSlotEntity.addTagId(155L);
        targetService.addSlotToTarget(testUserName, target1.getTargetId(), targetSlotEntity);
        targetSlotEntity = new TargetSlotEntity();
        targetSlotEntity.setSlotDishTagId(5L);  // Main Dish - another
        targetSlotEntity.addTagId(71L);
        targetSlotEntity.addTagId(61L);
        targetService.addSlotToTarget(testUserName, target1.getTargetId(), targetSlotEntity);
        return targetService.getTargetById(testUserName, target1.getTargetId());

    }


    private TargetEntity createTarget2() {
        // this is a small target with three slots,and a decent amount of matches
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
        targetService.addSlotToTarget(testUserName, target2.getTargetId(), targetSlotEntity);
        return targetService.getTargetById(testUserName, target2.getTargetId());

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

    private TargetEntity createTarget4() {
        // this is a small target with four slots,one slot with two tags, and one tag without quite so
        // many matches, and one slot with a tag with few hits
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
        targetSlotEntity = new TargetSlotEntity();
        targetSlotEntity.setSlotDishTagId(5L);  // Main Dish - another
        targetSlotEntity.addTagId(201L); // Vegetarian
        targetService.addSlotToTarget(testUserName, target2.getTargetId(), targetSlotEntity);
        return targetService.getTargetById(testUserName, target2.getTargetId());

    }


    private TargetEntity createTarget5() {
        // maybe a more realistic target
        TargetEntity target2 = new TargetEntity();
        target2.setTargetName("target2");
        target2.addTargetTagId(169L);// carrots
        target2.addTargetTagId(53L);// rice
        target2.addTargetTagId(61L);// black pepper

        target2 = targetService.createTarget(testUserName, target2);
        TargetSlotEntity targetSlotEntity = new TargetSlotEntity();
        targetSlotEntity.setSlotDishTagId(5L);  // Main Dish
        targetSlotEntity.addTagId(198L); // pasta dish type
        targetService.addSlotToTarget(testUserName, target2.getTargetId(), targetSlotEntity);
        targetSlotEntity = new TargetSlotEntity();
        targetSlotEntity.setSlotDishTagId(5L);  // Main Dish - another
        targetSlotEntity.addTagId(17L); // crockpot
        targetService.addSlotToTarget(testUserName, target2.getTargetId(), targetSlotEntity);
        targetSlotEntity = new TargetSlotEntity();
        targetSlotEntity.setSlotDishTagId(5L);  // Main Dish - another
        targetSlotEntity.addTagId(111L); // Soup
        targetService.addSlotToTarget(testUserName, target2.getTargetId(), targetSlotEntity);
        return targetService.getTargetById(testUserName, target2.getTargetId());

    }
}