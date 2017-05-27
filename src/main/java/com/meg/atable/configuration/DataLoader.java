package com.meg.atable.configuration;

import com.meg.atable.model.Dish;
import com.meg.atable.model.Tag;
import com.meg.atable.model.User;
import com.meg.atable.service.DishService;
import com.meg.atable.service.TagService;
import com.meg.atable.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class DataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private DishService dishService;

    private UserService userService;

    private TagService tagService;

    private Logger log = Logger.getLogger(DataLoader.class);

    @Autowired
    public void setDishService(DishService dishService) {
        this.dishService = dishService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setTagService(TagService tagService) {
        this.tagService = tagService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        User user = new User("rufus","password");
        user = userService.save(user);

        Tag main1 = new Tag("main1","main1");
        Tag main2 = new Tag("main2","main2");
        Tag main3 = new Tag("main3","main3");

        main1 = tagService.save(main1);
        main2 = tagService.save(main2);
        main3 = tagService.save(main3);

        Tag sub1 = tagService.createTag(main1,"sub1_1");
        Tag sub2 = tagService.createTag(main1,"sub1_2");
        Tag sub3 = tagService.createTag(main1,"sub1_3");

        Tag sub4 = tagService.createTag(main2,"sub2_1");
        Tag sub5 = tagService.createTag(main2,"sub2_2");

        Dish dish = new Dish(user,"yummy dish");
        dish.getTags().add(sub1);
        dish.getTags().add(sub2);
        dish = dishService.save(dish);

        dish = new Dish(user,"not so yummy");
        dish.getTags().add(main1);
        dish.getTags().add(main2);

        dish = dishService.save(dish);

    }
}