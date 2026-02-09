with targeted_tags as (
select t.tag_id, t.name, t.user_id, u.username
from tag t
         join users u on u.user_id = t.user_id
    and u.user_id = 34
    and t.tag_type = 'Ingredient'
    and t.tag_id in (51568,
                     51641,
                     51565,
                     51960,
                     51564,
                     51656,
                     51229,
                     50994,
                     50974,
                     50949,
                     51658))
select * from dish_items i
join targeted_tags t on t.tag_id = i.tag_id;

delete from category_tags where tag_id in (51568,
                                                 51641,
                                                 51565,
                                                 51960,
                                                 51564,
                                                 51656,
                                                 51229,
                                                 50994,
                                                 50974,
                                                 50949,
                                                 51658);

delete from tag_relation where child_tag_id in (51568,
                                                 51641,
                                                 51565,
                                                 51960,
                                                 51564,
                                                 51656,
                                                 51229,
                                                 50994,
                                                 50974,
                                                 50949,
                                                 51658);

delete from tag_relation where parent_tag_id in (51568,
                                                51641,
                                                51565,
                                                51960,
                                                51564,
                                                51656,
                                                51229,
                                                50994,
                                                50974,
                                                50949,
                                                51658);
delete from tag_relation where child_tag_id in (51568,
                                                51641,
                                                51565,
                                                51960,
                                                51564,
                                                51656,
                                                51229,
                                                50994,
                                                50974,
                                                50949,
                                                51658);
delete from tag where tag_id in (51568,
                                 51641,
                                 51565,
                                 51960,
                                 51564,
                                 51656,
                                 51229,
                                 50994,
                                 50974,
                                 50949,
                                 51658);
