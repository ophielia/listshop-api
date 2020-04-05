package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.Application;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.repository.DishRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.service.DishSearchCriteria;
import com.meg.listshop.lmt.service.DishSearchService;
import com.meg.listshop.test.TestConstants;
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
public class DishSearchServiceImplTest {

@Autowired
private DishSearchService dishSearchService;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserService userService;


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

    @Before
    public void setUp() {
        userAccount = userService.getUserByUserEmail(TestConstants.USER_1_NAME);
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