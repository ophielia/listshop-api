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

