-- data for user_account
INSERT INTO users (user_id, password, username, enabled) VALUES (1, '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'rufus',true);
INSERT INTO users (user_id, password, username, enabled) VALUES (20, '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'me',true);
INSERT INTO users (user_id, password, username, enabled) VALUES (23, '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'carrie',true);
INSERT INTO users (user_id, password, username, enabled) VALUES (26, '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'mom',true);
INSERT INTO users (user_id, password, username, enabled) VALUES (29, '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'michelle',true);

-- authorities for users
INSERT INTO public.authority(	authority_id, name, user_id)	VALUES (1, 'ROLE_USER', 1);
INSERT INTO public.authority(	authority_id, name, user_id)	VALUES (2, 'ROLE_USER', 20);
INSERT INTO public.authority(	authority_id, name, user_id)	VALUES (3, 'ROLE_USER', 23);
INSERT INTO public.authority(	authority_id, name, user_id)	VALUES (4, 'ROLE_USER', 26);
INSERT INTO public.authority(	authority_id, name, user_id)	VALUES (5, 'ROLE_USER', 29);

-- data for dish
INSERT INTO dish (dish_id, description, dish_name, user_id) VALUES (18, NULL, 'yummy dish', 1);
INSERT INTO dish (dish_id, description, dish_name, user_id) VALUES (19, NULL, 'not so yummy', 1);
INSERT INTO dish (dish_id, description, dish_name, user_id) VALUES (21, NULL, 'dishname-me', 20);
INSERT INTO dish (dish_id, description, dish_name, user_id) VALUES (22, NULL, 'dishname2-me', 20);
INSERT INTO dish (dish_id, description, dish_name, user_id) VALUES (24, NULL, 'dishname-carrie', 23);
INSERT INTO dish (dish_id, description, dish_name, user_id) VALUES (25, NULL, 'dishname2-carrie', 23);
INSERT INTO dish (dish_id, description, dish_name, user_id) VALUES (27, NULL, 'dishname-mom', 26);
INSERT INTO dish (dish_id, description, dish_name, user_id) VALUES (28, NULL, 'dishname2-mom', 26);
INSERT INTO dish (dish_id, description, dish_name, user_id) VALUES (30, NULL, 'dishname-michelle', 29);
INSERT INTO dish (dish_id, description, dish_name, user_id) VALUES (31, NULL, 'dishname2-michelle', 29);

-- data for tag
INSERT INTO tag (tag_id, description, name) VALUES (2, NULL, 'main1');
INSERT INTO tag (tag_id, description, name) VALUES (4, NULL, 'main2');
INSERT INTO tag (tag_id, description, name) VALUES (6, NULL, 'main3');
INSERT INTO tag (tag_id, description, name) VALUES (8, NULL, 'sub1_1');
INSERT INTO tag (tag_id, description, name) VALUES (10, NULL, 'sub1_2');
INSERT INTO tag (tag_id, description, name) VALUES (12, NULL, 'sub1_3');
INSERT INTO tag (tag_id, description, name) VALUES (14, NULL, 'sub2_1');
INSERT INTO tag (tag_id, description, name) VALUES (16, NULL, 'sub2_2');

-- data for dish_tags
INSERT INTO dish_tags (dish_id, tag_id) VALUES (18, 8);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (18, 10);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (19, 2);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (19, 4);

-- data for tag_relation
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (3, 2, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (5, 4, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (7, 6, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (9, 8, 2);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (11, 10, 2);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (13, 12, 2);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (15, 14, 4);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (17, 16, 4);
