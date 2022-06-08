/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

insert into tokens (token_id, user_id, token_type, created_on, token_value)
values (88, 500, 'PasswordReset', now(), 'token_password_reset');
insert into tokens (token_id, user_id, token_type, created_on, token_value)
values (89, 500, 'PasswordReset', now() - interval '6 days', 'token_password_reset_expired');


insert into public.users (user_id, email, enabled, last_password_reset_date, password, username, creation_date,
                          last_login)
values (999, 'rufus@barkingmad.com', true, null, '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi',
        'rufus@barkingmad.com', null, null);

insert into user_properties (user_property_id, user_id, property_key, property_value)
values (950, 999, 'test_property', 'ho hum value'),
       (951, 999, 'another_property', 'good value');