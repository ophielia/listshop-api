-- insert from dish source with non null (used = 1)
insert into list_item_details (item_detail_id, item_id, linked_dish_id, used_count)
select nextval('list_item_detail_sequence')                     as item_detail_id,
       item_id,
       concat(unnest(regexp_split_to_array(dish_sources, ';')),0)::bigint as linked_dish_id,
       1 as used_count
from list_item
where dish_sources is not null
  and dish_sources like '%;%';

-- insert from list source with non null (used = 1)
insert into list_item_details (item_detail_id, item_id, linked_list_id, used_count)
select nextval('list_item_detail_sequence')                     as item_detail_id,
       item_id,
       concat(unnest(regexp_split_to_array(list_sources, ';')),0)::bigint as linked_list_id,
       1 as used_count
from list_item
where list_sources is not null
  and list_sources like '%;%';

-- insert from dish source and list source null
insert into list_item_details (item_detail_id, item_id, used_count)
select nextval('list_item_detail_sequence')                     as item_detail_id,
       item_id,
       used_count
from list_item
where dish_sources is  null and list_sources is null;

-- update dish original amounts from dish - for those available
update list_item_details d
set orig_whole_quantity = di.whole_quantity,
    orig_fractional_quantity = di.fractional_quantity,
    orig_quantity = di.quantity,
    unit_id = di.unit_id,
    unit_size = di.unit_size,
    marker = di.marker,
    raw_entry = di.raw_entry
from
    list_item i, dish sh, dish_items di, tag t
where i.item_id = d.item_id
  and sh.dish_id = d.linked_dish_id
  and di.dish_id = sh.dish_id and di.tag_id = i.tag_id
  and t.tag_id = di.tag_id
  and d.linked_dish_id is not null
  and di.raw_entry is not null;

