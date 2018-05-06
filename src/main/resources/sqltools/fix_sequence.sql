begin;

select setval('auto_tag_instructions_sequence',(select max(instruction_id  ) from auto_tag_instructions ));
select setval('authority_seq',(select max(authority_id    ) from authority ));
select setval('category_relation_sequence',(select max(category_relation_id         ) from category_relation  ));
select setval('dish_sequence',(select max(dish_id         ) from dish  ));
select setval('list_item_sequence',(select max(item_id         ) from list_item  ));
select setval('list_layout_category_sequence',(select max(category_id     ) from list_category ));
select setval('list_layout_sequence',(select max(layout_id       ) from list_layout ));
select setval('meal_plan_sequence',(select max(meal_plan_id    ) from meal_plan ));
select setval('list_sequence',(select max(list_id         ) from list ));
select setval('list_tag_stats_sequence',(select max(list_tag_stat_id             ) from list_tag_stats ));
select setval('meal_plan_slot_sequence',(select max(meal_plan_slot_id            ) from meal_plan_slot ));
select setval('proposal_context_sequence',(select max(proposal_context_id          ) from proposal_context ));
select setval('proposal_context_slot_sequence',(select max(proposal_context_slot_id     ) from proposal_context_slot ));
select setval('shadow_tags_sequence',(select max(shadow_tag_id   ) from shadow_tags ));
select setval('tag_relation_sequence',(select max(tag_relation_id ) from tag_relation ));
select setval('tag_search_group_sequence',(select max(tag_search_group_id          ) from tag_search_group ));
select setval('tag_sequence',(select max(tag_id          ) from tag ));
select setval('target_proposal_dish_sequence',(select max(proposal_dish_id             ) from target_proposal_dish ));
select setval('target_proposal_sequence',(select max(proposal_id     ) from target_proposal ));
select setval('target_proposal_slot_sequence',(select max(slot_id         ) from target_proposal_slot ));
select setval('target_sequence',(select max(target_id       ) from target ));
select setval('user_id_sequence',(select max(user_id         ) from users ));


commit;