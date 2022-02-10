ALTER TABLE list_item
    DROP CONSTRAINT fk1ddq3ct1ulogjn5ijs8ert7hw;

ALTER TABLE list_item
    ADD CONSTRAINT fk_list__list_id FOREIGN KEY (list_id)
        REFERENCES list (list_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE;

ALTER TABLE meal_plan_slot
    DROP CONSTRAINT fkhhja2slk7gr34nhgcnlyw21ge;

ALTER TABLE meal_plan_slot
    ADD CONSTRAINT fk_meal_plan__meal_plan_slot FOREIGN KEY (meal_plan_id)
        REFERENCES meal_plan (meal_plan_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE;

alter table dish_tags
    drop constraint fkbh371e2vv53a3arqea0hf3jkl;

ALTER TABLE dish_tags
    ADD CONSTRAINT fk_dish__dish_tags FOREIGN KEY (dish_id)
        REFERENCES dish (dish_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE;

-- next up
-- foreign key cascade to user_id for lists, dishes, meal_plans
-- currently only exist for authority and dish
--        ALTER TABLE ONLY public.authority
--          ADD CONSTRAINT fkka37hl6mopj61rfbe97si18p8 FOREIGN KEY (user_id) REFERENCES public.users (user_id);
-- user id also referenced from -  authority, list_tag_stats, dish,list, meal_plan,
--       proposal, target, user_devices,