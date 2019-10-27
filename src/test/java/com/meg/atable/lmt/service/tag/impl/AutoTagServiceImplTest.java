package com.meg.atable.lmt.service.tag.impl;

import com.meg.atable.Application;
import com.meg.atable.lmt.data.entity.DishEntity;
import com.meg.atable.lmt.data.entity.TagEntity;
import com.meg.atable.lmt.service.DishService;
import com.meg.atable.lmt.service.tag.AutoTagService;
import com.meg.atable.lmt.service.tag.TagService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static com.meg.atable.test.TestConstants.USER_3_NAME;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class AutoTagServiceImplTest {
    @Autowired
    private AutoTagService autoTagService;

    @Autowired
    private DishService dishService;

    @Autowired
    private TagService tagService;


    @Test
    public void doAutoTag() throws Exception {
        // test for dish
        //406 chicken breasts
        //64 chicken kiev  - me, user3
        // 406 should be found by search group 434

        boolean override = false;
        DishEntity dishEntity = dishService.getDishForUserById(USER_3_NAME, 64L);

        autoTagService.doAutoTag(dishEntity, override);

        // dish 64 should be assigned the new tag of 346
        // it has tag 406, which is part of the search group 434, which is
        // assigned to 346 in a TagInstruction
        List<TagEntity> tags = tagService.getTagsForDish(USER_3_NAME, 64L);

        Optional<TagEntity> tag = tags.stream()
                .filter(t -> t.getId().equals(346L))
                .findFirst();
        Assert.assertTrue(tag.isPresent());
    }

    @Test
    public void doAutoTag_Tag_Shadowed() throws Exception {
        // test for dish
        //406 chicken breasts
        //3 tarragon cream chicken  - me, user3
        // 406 should be found by search group 434
        // but shadow tag exists, so shouldn't be found

        boolean override = false;
        DishEntity dishEntity = dishService.getDishForUserById(USER_3_NAME, 3L);

        autoTagService.doAutoTag(dishEntity, override);

        // dish 64 should be assigned the new tag of 346
        // it has tag 406, which is part of the search group 434, which is
        // assigned to 346 in a TagInstruction
        List<TagEntity> tags = tagService.getTagsForDish(USER_3_NAME, 3L);

        Optional<TagEntity> tag = tags.stream()
                .filter(t -> t.getId().equals(199L))
                .findFirst();
        Assert.assertFalse(tag.isPresent());
    }

    @Test
    public void doAutoTag_Invert() throws Exception {
        // test for dish
        //199 vegetarian
        //24 ble  - me, user3
        // invert - shouldn't find 406 or 434 (chicken)
        //          and not a dessert
        // should assign 199

        boolean override = false;
        DishEntity dishEntity = dishService.getDishForUserById(USER_3_NAME, 24L);

        autoTagService.doAutoTag(dishEntity, override);

        // dish 64 should be assigned the new tag of 346
        // it has tag 406, which is part of the search group 434, which is
        // assigned to 346 in a TagInstruction
        List<TagEntity> tags = tagService.getTagsForDish(USER_3_NAME, 24L);

        Optional<TagEntity> tag = tags.stream()
                .filter(t -> t.getId().equals(199L))
                .findFirst();
        Assert.assertTrue(tag.isPresent());
    }

    @Test
    public void doAutoTag_Invert_Shadowed() throws Exception {
        // test for dish
        //199 vegetarian
        //87 mixed veggies  - me, user3
        // invert - shouldn't find 406 or 434 (chicken)
        //          and not a dessert
        // should NOT assign 199 - because it's shadowd

        boolean override = false;
        DishEntity dishEntity = dishService.getDishForUserById(USER_3_NAME, 87L);

        autoTagService.doAutoTag(dishEntity, override);

        List<TagEntity> tags = tagService.getTagsForDish(USER_3_NAME, 87L);

        Optional<TagEntity> tag = tags.stream()
                .filter(t -> t.getId().equals(199))
                .findFirst();
        Assert.assertFalse(tag.isPresent());
    }

    @Test
    public void doAutoTag_Invert_Dessert() throws Exception {
        // test for dish
        //199 vegetarian
        //30 chocolate crinkles - me, user3
        // invert - shouldn't find 406 or 434 (chicken)
        //          and not a dessert
        // should NOT assign 199 - because it's a dessert

        boolean override = false;
        DishEntity dishEntity = dishService.getDishForUserById(USER_3_NAME, 30L);

        autoTagService.doAutoTag(dishEntity, override);

        List<TagEntity> tags = tagService.getTagsForDish(USER_3_NAME, 30L);

        Optional<TagEntity> tag = tags.stream()
                .filter(t -> t.getId().equals(199))
                .findFirst();
        Assert.assertFalse(tag.isPresent());
    }
}