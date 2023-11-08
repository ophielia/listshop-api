--
-- Name: authority authority_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.authority
    ADD CONSTRAINT authority_pkey PRIMARY KEY (authority_id);


--
-- Name: auto_tag_instructions auto_tag_instructions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.auto_tag_instructions
    ADD CONSTRAINT auto_tag_instructions_pkey PRIMARY KEY (instruction_id);


ALTER TABLE ONLY public.dish
    ADD CONSTRAINT dish_pkey PRIMARY KEY (dish_id);

ALTER TABLE ONLY public.list_category
    ADD CONSTRAINT list_category_pkey PRIMARY KEY (category_id);


ALTER TABLE ONLY public.list_item
    ADD CONSTRAINT list_item_pkey PRIMARY KEY (item_id);

ALTER TABLE ONLY public.list_layout
    ADD CONSTRAINT list_layout_pkey PRIMARY KEY (layout_id);

ALTER TABLE ONLY public.list
    ADD CONSTRAINT list_pkey PRIMARY KEY (list_id);

ALTER TABLE ONLY public.list_tag_stats
    ADD CONSTRAINT list_tag_stats_pkey PRIMARY KEY (list_tag_stat_id);

ALTER TABLE ONLY public.meal_plan
    ADD CONSTRAINT meal_plan_pkey PRIMARY KEY (meal_plan_id);

ALTER TABLE ONLY public.meal_plan_slot
    ADD CONSTRAINT meal_plan_slot_pkey PRIMARY KEY (meal_plan_slot_id);

ALTER TABLE ONLY public.proposal_context
    ADD CONSTRAINT proposal_context_pkey PRIMARY KEY (proposal_context_id);

ALTER TABLE ONLY public.shadow_tags
    ADD CONSTRAINT shadow_tags_pkey PRIMARY KEY (shadow_tag_id);

ALTER TABLE ONLY public.tag
    ADD CONSTRAINT tag_pkey PRIMARY KEY (tag_id);

ALTER TABLE ONLY public.tag_relation
    ADD CONSTRAINT tag_relation_pkey PRIMARY KEY (tag_relation_id);

ALTER TABLE ONLY public.tag_search_group
    ADD CONSTRAINT tag_search_group_pkey PRIMARY KEY (tag_search_group_id);

ALTER TABLE ONLY public.target
    ADD CONSTRAINT target_pkey PRIMARY KEY (target_id);

ALTER TABLE ONLY public.target_slot
    ADD CONSTRAINT target_slot_pkey PRIMARY KEY (target_slot_id);

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);

ALTER TABLE ONLY public.list_item
    ADD CONSTRAINT fk1ddq3ct1ulogjn5ijs8ert7hw FOREIGN KEY (list_id) REFERENCES public.list (list_id);


ALTER TABLE ONLY public.tag_relation
    ADD CONSTRAINT fk3vyajpbcb8wl8380yntahtgtf FOREIGN KEY (parent_tag_id) REFERENCES public.tag (tag_id);

ALTER TABLE ONLY public.dish
    ADD CONSTRAINT fk4cvbymf9m9quckcouehn0p414 FOREIGN KEY (user_id) REFERENCES public.users (user_id);

ALTER TABLE ONLY public.tag_relation
    ADD CONSTRAINT fk6x8vvlp985udfs7g15uuxj42c FOREIGN KEY (child_tag_id) REFERENCES public.tag (tag_id);

ALTER TABLE ONLY public.dish_tags
    ADD CONSTRAINT fkbh371e2vv53a3arqea0hf3jkl FOREIGN KEY (dish_id) REFERENCES public.dish (dish_id);


--
-- Name: category_tags fkclr8vrg8b1cwgwjsgcd5jtj6a; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category_tags
    ADD CONSTRAINT fkclr8vrg8b1cwgwjsgcd5jtj6a FOREIGN KEY (tag_id) REFERENCES public.tag (tag_id);


--
-- Name: meal_plan_slot fkdit15dhtc9j583c1pp21c8ss0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.meal_plan_slot
    ADD CONSTRAINT fkdit15dhtc9j583c1pp21c8ss0 FOREIGN KEY (dish_dish_id) REFERENCES public.dish (dish_id);


--
-- Name: meal_plan_slot fkhhja2slk7gr34nhgcnlyw21ge; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.meal_plan_slot
    ADD CONSTRAINT fkhhja2slk7gr34nhgcnlyw21ge FOREIGN KEY (meal_plan_id) REFERENCES public.meal_plan (meal_plan_id);


--
-- Name: authority fkka37hl6mopj61rfbe97si18p8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.authority
    ADD CONSTRAINT fkka37hl6mopj61rfbe97si18p8 FOREIGN KEY (user_id) REFERENCES public.users (user_id);


--
-- Name: list_item fklcvoij9ynqfllhxgn9v6qpsh8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.list_item
    ADD CONSTRAINT fklcvoij9ynqfllhxgn9v6qpsh8 FOREIGN KEY (tag_id) REFERENCES public.tag (tag_id);


--
-- Name: category_tags fkns9s1sef980caqqamoee8srdw; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category_tags
    ADD CONSTRAINT fkns9s1sef980caqqamoee8srdw FOREIGN KEY (category_id) REFERENCES public.list_category (category_id);


--
-- Name: dish_tags fkpy8j9ypbt3d59bjs0hgl3wcct; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dish_tags
    ADD CONSTRAINT fkpy8j9ypbt3d59bjs0hgl3wcct FOREIGN KEY (tag_id) REFERENCES public.tag (tag_id);


--
-- Name: list_category fkrhcs3i2p15y79hn00y5ic41gn; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.list_category
    ADD CONSTRAINT fkrhcs3i2p15y79hn00y5ic41gn FOREIGN KEY (layout_id) REFERENCES public.list_layout (layout_id);


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: margaretmartin
--

GRANT
ALL
ON SCHEMA public TO postgres;
GRANT ALL
ON SCHEMA public TO bankuser;


--
-- Name: TABLE authority; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.authority TO bankuser;


--
-- Name: TABLE auto_tag_instructions; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.auto_tag_instructions TO bankuser;


--
-- Name: TABLE list_stat_configs; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.list_stat_configs TO bankuser;


--
-- Name: TABLE list_tag_stats; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.list_tag_stats TO bankuser;


--
-- Name: TABLE calculated_stats; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.calculated_stats TO bankuser;



GRANT ALL
ON TABLE public.category_tags TO bankuser;


--
-- Name: TABLE dish; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.dish TO bankuser;


--
-- Name: TABLE dish_tags; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.dish_tags TO bankuser;



--
-- Name: TABLE list; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.list TO bankuser;


--
-- Name: TABLE list_category; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.list_category TO bankuser;


--
-- Name: TABLE list_item; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.list_item TO bankuser;


--
-- Name: TABLE list_layout; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.list_layout TO bankuser;


--
-- Name: TABLE meal_plan; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.meal_plan TO bankuser;


--
-- Name: TABLE meal_plan_slot; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.meal_plan_slot TO bankuser;


--
-- Name: TABLE proposal; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.proposal TO bankuser;


--
-- Name: TABLE proposal_approach; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.proposal_approach TO bankuser;


--
-- Name: TABLE proposal_context; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.proposal_context TO bankuser;


--
-- Name: TABLE proposal_dish; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.proposal_dish TO bankuser;


--
-- Name: TABLE proposal_slot; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.proposal_slot TO bankuser;


--
-- Name: TABLE tag_relation; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.tag_relation TO bankuser;


--
-- Name: TABLE shadow_tags; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.shadow_tags TO bankuser;


--
-- Name: TABLE tag; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.tag TO bankuser;


--
-- Name: TABLE tag_search_group; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.tag_search_group TO bankuser;


--
-- Name: TABLE target; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.target TO bankuser;


--
-- Name: TABLE target_slot; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.target_slot TO bankuser;


--
-- Name: TABLE user_devices; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.user_devices TO bankuser;


--
-- Name: TABLE users; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL
ON TABLE public.users TO bankuser;

-- corresponds to v3__migration
-- cleaning before adding constraints


ALTER TABLE list_item
    ADD CONSTRAINT fk_list__list_id FOREIGN KEY (list_id)
        REFERENCES list (list_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE;


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
    ADD CONSTRAINT fk_stats__user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON
DELETE
CASCADE;

ALTER TABLE ONLY list
    ADD CONSTRAINT fk_list__user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON
DELETE
CASCADE;

ALTER TABLE ONLY meal_plan
    ADD CONSTRAINT fk_meal_plan__user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON
DELETE
CASCADE;

ALTER TABLE ONLY proposal
    ADD CONSTRAINT fk_proposal__user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON
DELETE
CASCADE;

ALTER TABLE ONLY target
    ADD CONSTRAINT fk_target__user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON
DELETE
CASCADE;

ALTER TABLE ONLY user_devices
    ADD CONSTRAINT fk_user_devices__user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON
DELETE
CASCADE;

ALTER TABLE authority
DROP
CONSTRAINT IF EXISTS fkka37hl6mopj61rfbe97si18p8;

ALTER TABLE authority
    ADD CONSTRAINT fk_authority__user_id FOREIGN KEY (user_id)
        REFERENCES users (user_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE;
