-- corresponds to v3__migration
-- cleaning before adding constraints
delete
from proposal_context c
where not exists(select from proposal where proposal_id = c.proposal_id);
delete
from list l
where not exists(select from users where user_id = l.user_id);
delete
from meal_plan_slot l
where meal_plan_id in
      (select meal_plan_id from meal_plan l where not exists(select from users where user_id = l.user_id));
delete
from meal_plan l
where not exists(select from users where user_id = l.user_id);

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

ALTER TABLE dish
    ADD CONSTRAINT fk_dish__user_id FOREIGN KEY (user_id)
        REFERENCES users (user_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE;

ALTER TABLE dish
    DROP CONSTRAINT fk4cvbymf9m9quckcouehn0p414;

alter table dish_tags
    drop constraint IF EXISTS fkbh371e2vv53a3arqea0hf3jkl;

ALTER TABLE dish_tags
    ADD CONSTRAINT fk_dish__dish_tags FOREIGN KEY (dish_id)
        REFERENCES dish (dish_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE;

-- targets and target_slots
ALTER TABLE target_slot
    ADD CONSTRAINT fk_target__target_slot FOREIGN KEY (target_id)
        REFERENCES target (target_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE;

-- proposals
-- proposal_dish and proposal_slot
ALTER TABLE proposal_slot
    ADD CONSTRAINT proposal_slot_pkey PRIMARY KEY (slot_id);

ALTER TABLE proposal
    ADD CONSTRAINT proposal_pkey PRIMARY KEY (proposal_id);

ALTER TABLE proposal_dish
    ADD CONSTRAINT fk_proposal_dish__proposal_slot FOREIGN KEY (slot_id)
        REFERENCES proposal_slot (slot_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE;



ALTER TABLE proposal_slot
    ADD CONSTRAINT fk_proposal_slot__proposal FOREIGN KEY (proposal_id)
        REFERENCES proposal (proposal_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE;

ALTER TABLE proposal_approach
    ADD CONSTRAINT fk_proposal_approach__proposal_context FOREIGN KEY (proposal_context_id)
        REFERENCES proposal_context (proposal_context_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE;

ALTER TABLE proposal_context
    ADD CONSTRAINT fk_proposal_context__proposal FOREIGN KEY (proposal_id)
        REFERENCES proposal (proposal_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE;


-- to user_id
ALTER TABLE ONLY list_tag_stats
    ADD CONSTRAINT fk_stats__user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;

ALTER TABLE ONLY list
    ADD CONSTRAINT fk_list__user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;

ALTER TABLE ONLY meal_plan
    ADD CONSTRAINT fk_meal_plan__user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;

ALTER TABLE ONLY proposal
    ADD CONSTRAINT fk_proposal__user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;

ALTER TABLE ONLY target
    ADD CONSTRAINT fk_target__user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;

ALTER TABLE ONLY user_devices
    ADD CONSTRAINT fk_user_devices__user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;

ALTER TABLE authority
    DROP CONSTRAINT IF EXISTS fkka37hl6mopj61rfbe97si18p8;

ALTER TABLE authority
    ADD CONSTRAINT fk_authority__user_id FOREIGN KEY (user_id)
        REFERENCES users (user_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE;

-- next up
-- done foreign key cascade child objects
--   done   target
--   done    proposal
-- foreign key cascade to user_id for lists, dishes, meal_plans
-- currently only exist for authority and dish
--        ALTER TABLE ONLY public.authority
--          ADD CONSTRAINT fkka37hl6mopj61rfbe97si18p8 FOREIGN KEY (user_id) REFERENCES public.users (user_id);
-- user id also referenced from -  list_tag_stats, dish,list, meal_plan,
--       proposal, target, user_devices,