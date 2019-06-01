INSERT INTO tag (tag_id, description, name, tag_type, tag_type_default, assign_select, search_select, is_verified,
                 power)
VALUES (1400000, NULL, '1400000', 'Ingredient', NULL, NULL, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, tag_type_default, assign_select, search_select, is_verified,
                 power)
VALUES (1400001, NULL, '1400001', 'Ingredient', NULL, NULL, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, tag_type_default, assign_select, search_select, is_verified,
                 power)
VALUES (1400002, NULL, '1400002', 'Ingredient', NULL, NULL, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, tag_type_default, assign_select, search_select, is_verified,
                 power)
VALUES (1400003, NULL, '1400003', 'Ingredient', NULL, NULL, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, tag_type_default, assign_select, search_select, is_verified,
                 power)
VALUES (1400004, NULL, '1400004', 'Ingredient', NULL, NULL, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, tag_type_default, assign_select, search_select, is_verified,
                 power)
VALUES (1400005, NULL, '1400005', 'Ingredient', NULL, NULL, false, NULL, NULL);


INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id)
VALUES (50000, 1400000, 1400004);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id)
VALUES (50001, 1400001, 1400000);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id)
VALUES (50002, 1400002, 1400001);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id)
VALUES (50003, 1400003, 1400000);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id)
VALUES (50004, 1400004, null);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id)
VALUES (50005, 1400005, 1400000);



update tag
set created_on = created_on - interval '1 day'
where created_on is not null;
update tag
set created_on = now() - interval '1 minute'
where tag_id in (1400003, 1400002);