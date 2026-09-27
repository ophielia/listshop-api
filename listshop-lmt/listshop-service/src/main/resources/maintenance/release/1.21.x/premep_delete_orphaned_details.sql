/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

delete from list_item_details
where item_detail_id in (
    select distinct d.item_detail_id
    from list_item_details d
             left outer join list_item i on i.item_id = d.item_id
    where i is null)
