package com.meg.atable.service.impl;

import com.meg.atable.Application;
import com.meg.atable.api.model.MealPlanType;
import com.meg.atable.api.model.TagType;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.MealPlanEntity;
import com.meg.atable.data.entity.SlotEntity;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.repository.DishRepository;
import com.meg.atable.data.repository.MealPlanRepository;
import com.meg.atable.data.repository.SlotRepository;
import com.meg.atable.data.repository.TagRepository;
import com.meg.atable.service.DishSearchCriteria;
import com.meg.atable.service.DishSearchService;
import com.meg.atable.service.MealPlanService;
import com.meg.atable.test.TestConstants;
import io.jsonwebtoken.lang.Collections;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class DishSearchServiceImplTest {

@Autowired
private DishSearchService dishSearchService;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserService userService;


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

    @Before
    public void setUp() {
        userAccount = userService.getUserByUserName(TestConstants.USER_1_NAME);
    }

    @Test
    public void findMealsByTag() throws Exception {
        DishSearchCriteria criteria = new DishSearchCriteria(userAccount.getId());
        criteria.setIncludedTagIds(Arrays.asList(TestConstants.TAG_1_ID,TestConstants.TAG_2_ID,TestConstants.TAG_3_ID));
        criteria.setExcludedTagIds(Arrays.asList(TestConstants.TAG_4_ID));

        List<DishEntity> dishlist = dishSearchService.findDishes(criteria);
        Assert.assertNotNull(dishlist);
    }

    @Test
    public void testDishProposalSearch() {
        /*
            List<DishTagSearchResult> retrieveDishResultsForTags(Long userId, TargetSlotEntity targetSlotEntity, int size, List<String> tagListForSlot,
                                                         Map<Long, List<Long>> searchGroups, List<Long> sqlFilteredDishes);
         */
    }


}