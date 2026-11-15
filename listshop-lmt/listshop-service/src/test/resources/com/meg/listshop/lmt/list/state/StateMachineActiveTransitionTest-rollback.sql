/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

update tag set conversion_id = 225744 where tag_id = 33;
delete from user_properties where user_id in (20, 34) and property_key = 'preferred_domain';
delete from factors where conversion_id = 112225744;
