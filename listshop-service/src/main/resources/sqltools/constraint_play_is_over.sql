-- rollback for migration

ALTER TABLE list_item
    DROP CONSTRAINT IF EXISTS fk_list__list_id;

ALTER TABLE public.list_item
    ADD CONSTRAINT fk1ddq3ct1ulogjn5ijs8ert7hw FOREIGN KEY (list_id)
        REFERENCES public.list (list_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION;

ALTER TABLE meal_plan_slot
    DROP CONSTRAINT IF EXISTS fk_meal_plan__meal_plan_slot;

ALTER TABLE meal_plan_slot
    ADD CONSTRAINT fkhhja2slk7gr34nhgcnlyw21ge FOREIGN KEY (meal_plan_id)
        REFERENCES meal_plan (meal_plan_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION;

ALTER TABLE dish
    DROP CONSTRAINT IF EXISTS fk_dish__user_id;

ALTER TABLE dish
    ADD CONSTRAINT fk4cvbymf9m9quckcouehn0p414 FOREIGN KEY (user_id)
        REFERENCES users (user_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION;

alter table dish_tags
    DROP CONSTRAINT IF EXISTS fk_dish__dish_tags;

ALTER TABLE dish_tags
    ADD CONSTRAINT fkbh371e2vv53a3arqea0hf3jkl FOREIGN KEY (dish_id)
        REFERENCES dish (dish_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION;

ALTER TABLE target_slot
    DROP CONSTRAINT IF EXISTS fk_target__target_slot;

-- proposals
-- proposal_dish and proposal_slot
ALTER TABLE proposal_dish
    DROP CONSTRAINT IF EXISTS fk_proposal_dish__proposal_slot;

ALTER TABLE proposal
    DROP CONSTRAINT proposal_pkey CASCADE;

ALTER TABLE proposal_slot
    DROP CONSTRAINT IF EXISTS proposal_slot_pkey CASCADE;

ALTER TABLE proposal_slot
    DROP CONSTRAINT IF EXISTS fk_proposal_slot__proposal;

ALTER TABLE proposal_approach
    DROP CONSTRAINT IF EXISTS fk_proposal_approach__proposal_context;

ALTER TABLE proposal_context
    DROP CONSTRAINT IF EXISTS fk_proposal_context__proposal;

-- to user_id
ALTER TABLE ONLY list_tag_stats
    DROP CONSTRAINT IF EXISTS fk_stats__user_id;

ALTER TABLE ONLY list
    DROP CONSTRAINT IF EXISTS fk_list__user_id;

ALTER TABLE ONLY meal_plan
    DROP CONSTRAINT IF EXISTS fk_meal_plan__user_id;

ALTER TABLE ONLY proposal
    DROP CONSTRAINT IF EXISTS fk_proposal__user_id;

ALTER TABLE ONLY target
    DROP CONSTRAINT IF EXISTS fk_target__user_id;

ALTER TABLE ONLY user_devices
    DROP CONSTRAINT IF EXISTS fk_user_devices__user_id;

ALTER TABLE authority
    DROP CONSTRAINT IF EXISTS fk_authority__user_id;

ALTER TABLE authority
    ADD CONSTRAINT fkka37hl6mopj61rfbe97si18p8 FOREIGN KEY (user_id)
        REFERENCES users (user_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION;


drop table tokens;
drop sequence token_sequence;