--- new user, and single list
delete
from users
where user_id in (101010);
INSERT INTO users (user_id, email, enabled, last_password_reset_date, password, username)
VALUES (101010, 'adminrestcontroller@test.com', true, NULL,
        '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi',
        'adminrestcontroller@test.com');

INSERT INTO tag (user_id, tag_id, description, name, tag_type, tag_type_default, is_verified,
                 power)
VALUES (101010, 999999, NULL, 'some green thing', 'Ingredient', NULL, NULL, NULL);

INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id)
VALUES (999999, 999999, 388);-- Produce
INSERT INTO tag (user_id, tag_id, description, name, tag_type, tag_type_default, is_verified,
                 power)
VALUES (101010, 888888, NULL, 'some black thing', 'Ingredient', NULL, NULL, NULL);


-- for food suggestion test
insert into tag (tag_id, description, name, tag_type,  created_on, is_group)  VALUES
    (888999, 'description', 'butterlike', 'Ingredient' , now(), false) ;
insert into tag (tag_id, description, name, tag_type,  created_on, is_group)  VALUES
    (8881009, 'description', 'Mr. Butter', 'Ingredient' , now(), false) ;
insert into tag (tag_id, description, name, tag_type,  created_on, is_group)  VALUES
    (8881019, 'description', 'Grandpa Butter', 'Ingredient' , now(), false) ;

insert into tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES
    (888999, 888999, 8881009);
insert into tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES
    (8881000, 8881009, 8881019);
insert into tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES
    (8881001, 8881019, null);

insert into food_categories (category_id, category_code, name) values
    (3, '3L', 'things that come from a cow');
insert into test.public.food_categories (category_id, category_code, name) values
    (13, '13L', 'things that come from a laboratory');
insert into test.public.food_categories (category_id, category_code, name) values
    (18, '18L', 'things that come from we dont know where');

insert into foods (food_id,name, category_id) values
    (9000,'butterlike substance, stick', 3);
insert into foods (food_id,name, category_id) values
    (9001,'butterlike substance, pat', 3);
insert into foods (food_id,name, category_id) values
    (9002,'some kind of butterlike substance, unspecified', 13);
insert into foods (food_id,name, category_id) values
    (9003,'sticky stuff', 13);
insert into foods (food_id,name, category_id) values
    (9004,'butterlike spread', 18);

insert into food_category_mapping (food_category_mapping_id,category_id, tag_id) values (1,3,8881019);

insert into food_conversions (conversion_id, food_id, fdc_id, amount, unit_name, gram_weight, unit_id) values
    (99000, 9000, 900, 1.0, '', 150.0, 1000)  ;



