package com.meg.atable.service.impl;

import com.meg.atable.Application;
import com.meg.atable.api.model.TagType;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.entity.TargetEntity;
import com.meg.atable.data.repository.DishRepository;
import com.meg.atable.data.repository.TagRepository;
import com.meg.atable.service.DishSearchCriteria;
import com.meg.atable.service.DishSearchService;
import com.meg.atable.service.TargetProposalService;
import com.meg.atable.service.TargetService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class TargetProposalServiceImplTest {

@Autowired
private DishSearchService dishSearchService;


    @Autowired
    private TargetProposalService targetProposalService;

    @Autowired
    private TargetService targetService;

    @Autowired
    private UserService userService;


    @Autowired
    private TagRepository tagRepository;

    private static boolean setUpComplete = false;
    private static UserAccountEntity userAccount;

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
/*
    @Before
    public void setUp() {
        if (setUpComplete) {
            return;
        }
        String userName = "mealPlanTest";
        userAccount = userService.save(new UserAccountEntity(userName, "password"));


         tag1 = ServiceTestUtils.buildTag("tag1", TagType.Ingredient);

         tag2 = ServiceTestUtils.buildTag("tag2", TagType.Ingredient);

         tag3 = ServiceTestUtils.buildTag("tag3", TagType.Ingredient);

         tag4 = ServiceTestUtils.buildTag("tag4", TagType.Ingredient);
         tag5 = ServiceTestUtils.buildTag("tag5", TagType.Ingredient);
         tagRepository.save(Arrays.asList(tag1,tag2,tag3,tag4,tag5));

         dish1 = ServiceTestUtils.buildDish(userAccount.getId(),"dish1",
                 Arrays.asList(tag1,tag2));
        dish2 = ServiceTestUtils.buildDish(userAccount.getId(),"dish2",
                Arrays.asList(tag3,tag4));
        dish3 = ServiceTestUtils.buildDish(userAccount.getId(),"dish3",
                Arrays.asList(tag1,tag3,tag5));
        dishRepository.save(Arrays.asList(dish1,dish2,dish3));


        setUpComplete = true;

    }
*/
    @Test
    public void createProposal() throws Exception {
       TargetEntity target = targetService.getTargetById("rufus",9650L);
       targetProposalService.createTargetProposal(target);
    }


}