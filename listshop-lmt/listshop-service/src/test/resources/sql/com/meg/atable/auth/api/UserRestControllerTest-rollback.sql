/*
 * The List Shop
 *
 * Copyright (c) 2022-2026.
 */

delete from tokens where user_id in (500, 10999) ;

delete from authority where user_id in (10999) ;

delete from user_properties where user_id = 999;

delete from users where user_id in (61, 10999, 999) ;
