delete from category_tags where category_id in (select category_id from list_category where layout_id in (999, 998));
delete from  list_category where layout_id in (999, 998);
delete from  list_layout where layout_id in (999, 998);

delete from category_tags where category_id in (
    select category_id from list_category where layout_id in (select layout_id
                                                              from list_layout where user_id = 121212));
delete from  list_category where layout_id in (select layout_id
                                               from list_layout where user_id = 121212);
delete from  list_layout where layout_id in (select layout_id
                                             from list_layout where user_id = 121212);


delete from tag where tag_id in (9991234,
                                  9991235,
                                  9991236,
                                  9991237);

delete from users where user_id = 99999;
delete from users where user_id = 101010;
delete from users where user_id = 121212;