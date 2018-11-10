package com.meg.atable.lmt.service.impl;

import com.meg.atable.Application;
import com.meg.atable.lmt.api.model.FatTag;
import com.meg.atable.lmt.api.model.TagType;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.lmt.data.entity.TagEntity;
import com.meg.atable.lmt.data.repository.MealPlanRepository;
import com.meg.atable.lmt.data.repository.ShoppingListRepository;
import com.meg.atable.lmt.service.ShoppingListProperties;
import com.meg.atable.lmt.service.ShoppingListService;
import com.meg.atable.lmt.service.tag.TagService;
import com.meg.atable.lmt.service.tag.TagStructureService;
import com.meg.atable.test.TestConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class TagStructureServiceImplTest {


    @Autowired
    private ShoppingListService shoppingListService;
    @Autowired
    private ShoppingListRepository shoppingListRepository;
    @Autowired
    private MealPlanRepository mealPlanRepository;
    @Autowired
    private TagStructureService tagStructureService;
    @Autowired
    private UserService userService;
    @Autowired
    private TagService tagService;
    @Autowired
    private ShoppingListProperties shoppingListProperties;
    private UserAccountEntity userAccount;  // user_id 500
    private UserAccountEntity addUserAccount;  // user_id 501
    private TagEntity tag1; // 500
    private TagEntity cheddarTag; // 18

    @Before
    public void setUp() {
        userAccount = userService.getUserByUserName(TestConstants.USER_1_NAME);
        addUserAccount = userService.getUserByUserName(TestConstants.USER_2_NAME);
        // make tags
        tag1 = tagService.getTagById(TestConstants.TAG_1_ID);
        cheddarTag = tagService.getTagById(18L); // 18 is cheddar tag id;ﬂ
    }

    @Test
    public void testGetTagsWithChildren() {
        List<FatTag> results = tagStructureService.getTagsWithChildren(null);

        Assert.assertNotNull(results);
        FatTag meatCategory = results.stream().filter(t -> t.getId().equals(371L)).findFirst().orElse(null);
        Assert.assertNotNull(meatCategory);
        FatTag beefCategory = meatCategory.getChildren().stream().filter(t -> t.getId().equals(372L)).findFirst().orElse(null);
        Assert.assertNotNull(beefCategory);
        FatTag stewMeatCategory = beefCategory.getChildren().stream().filter(t -> t.getId().equals(251L)).findFirst().orElse(null);
        Assert.assertNotNull(stewMeatCategory);

        results = tagStructureService.getTagsWithChildren(Collections.singletonList(TagType.Rating));
        boolean allgood = true;
        for (FatTag resultTag : results) {
            if (!resultTag.getTagType().equals(TagType.Rating)) {
                allgood = false;
                break;
            }
        }
        Assert.assertTrue(allgood);
    }
}