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
