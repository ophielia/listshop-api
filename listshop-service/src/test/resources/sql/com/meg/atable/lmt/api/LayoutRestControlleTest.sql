--- new user, and single list
delete
from users
where user_id in (99999);
INSERT INTO users (user_id, email, enabled, last_password_reset_date, password, username)
VALUES (99999, 'username@testitytest.com', true, NULL, '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi',
        'email@email.com');
delete
from users
where user_id in (101010);
INSERT INTO users (user_id, email, enabled, last_password_reset_date, password, username)
VALUES (101010, 'user@emptyuser.com', true, NULL, '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi',
        'user@emptyuser.com');

delete
from users
where user_id in (121212);
INSERT INTO users (user_id, email, enabled, last_password_reset_date, password, username)
VALUES (121212, 'user@brandnewuser.com', true, NULL, '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi',
        'user@brandnewuser.com');




insert into list_layout (layout_id, name, user_id, is_default)
values (999, 'Special Layout', 99999, true);


insert into list_category (category_id, name, layout_id, display_order, is_default)
values ( 999901, 'Special Category', 999, 20, false);

INSERT INTO tag (tag_id, description, name, tag_type, tag_type_default, is_verified,
                 power)
VALUES (9991234, NULL, 'mapped_tag 4', 'Ingredient', NULL, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, tag_type_default, is_verified,
                 power)
VALUES (9991235, NULL, 'mapped_tag 5', 'Ingredient', NULL, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, tag_type_default, is_verified,
                 power)
VALUES (9991236, NULL, 'mapped_tag 6', 'Ingredient', NULL, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, tag_type_default, is_verified,
                 power)
VALUES (9991237, NULL, 'mapped_tag 7', 'Ingredient', NULL, NULL, NULL);

-- just 34 and 35 and 37 are assigned to category
INSERT INTO category_tags (category_id, tag_id)
VALUES (999901, 9991234);
INSERT INTO category_tags (category_id, tag_id)
VALUES (999901, 9991235);
INSERT INTO category_tags (category_id, tag_id)
VALUES (999901, 9991237);

-- 2nd layout - all three are assigned
insert into list_layout (layout_id, name, user_id, is_default)
values (998, 'Everyday Layout', 99999, false);


insert into list_category (category_id, name, layout_id, display_order, is_default)
values ( 998901, 'Forbidden Area', 998, 20, false);

INSERT INTO category_tags (category_id, tag_id)
VALUES (998901, 9991234);
INSERT INTO category_tags (category_id, tag_id)
VALUES (998901, 9991235);
INSERT INTO category_tags (category_id, tag_id)
VALUES (998901, 9991236);
