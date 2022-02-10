ALTER TABLE list_item
    DROP CONSTRAINT fk_list__list_id TO;

ALTER TABLE public.list_item
    ADD CONSTRAINT fk1ddq3ct1ulogjn5ijs8ert7hw FOREIGN KEY (list_id)
        REFERENCES public.list (list_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION;

ALTER TABLE meal_plan_slot
    DROP CONSTRAINT fk_meal_plan__meal_plan_slot;

ALTER TABLE meal_plan_slot
    ADD CONSTRAINT fkhhja2slk7gr34nhgcnlyw21ge FOREIGN KEY (meal_plan_id)
        REFERENCES meal_plan (meal_plan_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION;



alter table dish_tags
    drop constraint fk_dish__dish_tags;

ALTER TABLE dish_tags
    ADD CONSTRAINT fkbh371e2vv53a3arqea0hf3jkl FOREIGN KEY (dish_id)
        REFERENCES dish (dish_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION;