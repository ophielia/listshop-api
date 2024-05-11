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
                 power, conversion_id)
VALUES (101010, 777777, NULL, 'poltry', 'Ingredient', NULL, NULL, NULL,227959);
INSERT INTO tag (user_id, tag_id, description, name, tag_type, tag_type_default, is_verified,
                 power, conversion_id)
VALUES (101010, 666666, NULL, 'to-mahhhh-toes', 'Ingredient', NULL, NULL, NULL,227959);


-- for food suggestion test
insert into tag (tag_id, description, name, tag_type,  created_on, is_group)  VALUES
    (888999, 'description', 'butterlike', 'Ingredient' , now(), false) ;
insert into tag (tag_id, description, name, tag_type,  created_on, is_group)  VALUES
    (8881009, 'description', 'Mr. Butter', 'Ingredient' , now(), true) ;
insert into tag (tag_id, description, name, tag_type,  created_on, is_group)  VALUES
    (8881019, 'description', 'Grandpa Butter', 'Ingredient' , now(), true) ;
insert into tag (tag_id, description, name, tag_type,  created_on, is_group)  VALUES
    (9991019, 'description', 'Grandma Cream', 'Ingredient' , now(), true) ;
insert into tag (tag_id, description, name, tag_type,  created_on, is_group, internal_status)  VALUES
    (9991029, 'description', 'one molecule from plastic', 'Ingredient' , now(), false, 65) ;
insert into tag (tag_id, description, name, tag_type,  created_on, is_group)  VALUES
    (9991039, 'description', 'Processed Food', 'Ingredient' , now(), true) ;

insert into tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES
    (888999, 888999, 8881009);
insert into tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES
    (8881000, 8881009, 8881019);
insert into tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES
    (8881001, 8881019, null);
insert into tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES
    (8881002, 9991039, null);
insert into tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES
    (8881003,9991029 , 9991039);

insert into food_categories (category_id, category_code, name) values
    (3, '3L', 'things that come from a cow');
insert into food_categories (category_id, category_code, name) values
    (13, '13L', 'things that come from a laboratory');
insert into food_categories (category_id, category_code, name) values
    (18, '18L', 'things that come from we dont know where');

insert into foods (food_id,conversion_id,name, category_id, has_factor) values
    (9000,99000,'butterlike substance, stick', 3, true);
insert into foods (food_id,conversion_id,name, category_id, has_factor) values
    (9001,99001,'butterlike substance, pat', 3,true);
insert into foods (food_id,conversion_id,name, category_id, has_factor) values
    (9002,99002,'some kind of butterlike substance, unspecified', 13,true);
insert into foods (food_id,conversion_id,name, category_id, has_factor) values
    (9003,99003,'sticky stuff', 13,true);
insert into foods (food_id,conversion_id,name, category_id, has_factor) values
    (9004,99004,'butterlike spread', 18,true);
insert into foods (food_id,conversion_id,name, category_id, has_factor) values
    (9005,99005,'cant call it cheese', 18,true);
insert into foods (food_id,conversion_id,name, category_id, has_factor) values
    (10101011,101010,'black oregano - pre-assigned', 18,true);

insert into food_category_mapping (food_category_mapping_id,category_id, tag_id) values (1,3,8881019);

insert into food_conversions (food_conversion_id, conversion_id, food_id, fdc_id, amount, unit_name, gram_weight, unit_id) values
    (9900000,99000, 9000, 900, 1.0, '', 150.0, 1000)  ;
insert into food_conversions (food_conversion_id, conversion_id, food_id, fdc_id, amount, unit_name, gram_weight, unit_id) values
    (9900011,99001, 9005, 901, 1.0, '', 150.0, 1000)  ;
insert into food_conversions (food_conversion_id, conversion_id, food_id, fdc_id, amount, unit_name, gram_weight, unit_id) values
    (10101011,101010, 10101011, 101, 1.0, '', 100.0, 1000)  ;
insert into food_conversions (conversion_id, food_id, fdc_id, amount, unit_name, gram_weight, unit_id, food_conversion_id, integral, marker, sub_amount, info, unit_size, unit_default)
    values (227959, 106417, 173619, 1, 'unit', 88, 1011, 234058, 'drumstick', null, null, null, null, true);


insert into factors (factor_id, factor, to_unit, from_unit, conversion_id) VALUES
     (1234567, 1.5, 1013, 1000, 99001);
insert into factors (factor_id, factor, to_unit, from_unit, conversion_id) VALUES
     (2345678, 100, 1013, 1000, 101010);
insert into factors (factor_id, factor, to_unit, from_unit, conversion_id, reference_id, marker, unit_size, unit_default) values
    (1065, 88,  1013, 1011,227959, 106417, null, null, false);

-- tomato test
insert into factors (factor_id, factor, to_unit, from_unit, conversion_id, reference_id, marker, unit_size, unit_default) values (1140, 180, 1013, 1000, 225744, 104748, 'chopped', null, false);
insert into factors (factor_id, factor, to_unit, from_unit, conversion_id, reference_id, marker, unit_size, unit_default) values (1141, 182, 1013, 1011, 225744, 105738, null, 'large', false);
insert into factors (factor_id, factor, to_unit, from_unit, conversion_id, reference_id, marker, unit_size, unit_default) values (1142, 123, 1013, 1011, 225744, 105738, null, 'medium', true);
insert into factors (factor_id, factor, to_unit, from_unit, conversion_id, reference_id, marker, unit_size, unit_default) values (1144, 91, 1013, 1011, 225744, 105738, null, 'small', false);
insert into factors (factor_id, factor, to_unit, from_unit, conversion_id, reference_id, marker, unit_size, unit_default) values (1145, 27, 1013, 1022, 225744, 105738, null, null, false);
insert into factors (factor_id, factor, to_unit, from_unit, conversion_id, reference_id, marker, unit_size, unit_default) values (1146, 31, 1013, 1050, 225744, 104539, 'wedge', null, false);
insert into factors (factor_id, factor, to_unit, from_unit, conversion_id, reference_id, marker, unit_size, unit_default) values (1147, 20, 1013, 1022, 225744, 105738, null, null, false);

insert into food_conversions (conversion_id, food_id, fdc_id, amount, unit_name, gram_weight, unit_id, food_conversion_id, integral, marker, sub_amount, info, unit_size, unit_default) values (225744, 104748, 170457, 1, 'cup', 180, 1000, 228179, null, 'chopped', null, null, null, false);
insert into food_conversions (conversion_id, food_id, fdc_id, amount, unit_name, gram_weight, unit_id, food_conversion_id, integral, marker, sub_amount, info, unit_size, unit_default) values (225744, 105738, 170457, 1, 'unit', 182, 1011, 228180, null, null, null, 'whole 3"" dia', 'large', false);
insert into food_conversions (conversion_id, food_id, fdc_id, amount, unit_name, gram_weight, unit_id, food_conversion_id, integral, marker, sub_amount, info, unit_size, unit_default) values (225744, 105738, 170457, 1, 'unit', 123, 1011, 228182, null, null, null, '2-3/5" dia', 'medium', true);
insert into food_conversions (conversion_id, food_id, fdc_id, amount, unit_name, gram_weight, unit_id, food_conversion_id, integral, marker, sub_amount, info, unit_size, unit_default) values (225744, 105738, 170457, 1, 'unit', 91, 1011, 229672, null, null, null, '2-2/5" dia', 'small', false);
insert into food_conversions (conversion_id, food_id, fdc_id, amount, unit_name, gram_weight, unit_id, food_conversion_id, integral, marker, sub_amount, info, unit_size, unit_default) values (225744, 105738, 170457, 1, 'slice', 27, 1022, 229673, null, null, null, '1/2" thick', null, false);
insert into food_conversions (conversion_id, food_id, fdc_id, amount, unit_name, gram_weight, unit_id, food_conversion_id, integral, marker, sub_amount, info, unit_size, unit_default) values (225744, 104539, 170457, 1, 'wedge', 31, 1050, 229970, null, 'wedge', null, '1/4 tomato', null, false);
insert into food_conversions (conversion_id, food_id, fdc_id, amount, unit_name, gram_weight, unit_id, food_conversion_id, integral, marker, sub_amount, info, unit_size, unit_default) values (225744, 105738, 170457, 1, 'slice', 20, 1022, 230808, null, null, null, '1/4" thick', null, false);



update tag set conversion_id = 99001 where tag_id = 9991029;
update tag set conversion_id = 225744 where tag_id = 666666;
update tag set is_liquid = false where tag_id = 9991029;


