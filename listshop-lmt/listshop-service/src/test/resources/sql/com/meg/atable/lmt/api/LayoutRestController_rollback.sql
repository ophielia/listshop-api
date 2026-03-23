delete from category_tags where category_id in (select category_id from list_category where layout_id in (1000128,999, 998));
delete from  list_category where layout_id in (1000128,999, 998);
delete from  list_layout where layout_id in (1000128,999, 998);

delete from category_tags where category_id in (
    select category_id from list_category where layout_id in (select layout_id
                                                              from list_layout where user_id in ( 121212, 34)));
delete from  list_category where layout_id in (select layout_id
                                               from list_layout where  user_id in ( 121212, 34));
delete from  list_layout where layout_id in (select layout_id
                                             from list_layout where  user_id in ( 121212, 34));


delete from tag where tag_id in (9991234,
                                  9991235,
                                  9991236,
                                 1000126,
                                  9991237);

delete from user_devices where user_id in (99999, 101010, 121212);
delete from users where user_id = 99999;
delete from users where user_id = 101010;
delete from users where user_id = 121212;


/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */


/* tags for automatic assignment */
delete from tag_relation where parent_tag_id = 1000123;
delete from tag_relation where child_tag_id = 1000123;
delete from tag_relation where child_tag_id = 1000128;
delete from category_tags where tag_id in (select tag_id from tag where tag_id in (1000123, 1000124, 1000125,1000128));
delete from category_tags where tag_id in (select tag_id from tag where name = 'Aaron Burr, sir');
delete from tag where tag_id in (1000123, 1000124, 1000125, 1000128);
delete from tag where name = 'Aaron Burr, sir';
