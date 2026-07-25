/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */
-- implement cascade on delete for list_item_details
ALTER TABLE list_item_details
    ADD CONSTRAINT fk_list_item_details_list_item
        FOREIGN KEY (item_id)
            REFERENCES list_item (item_id)
            ON DELETE CASCADE;


-- clean up orphans
with to_delete as (select item_detail_id from list_item_details d
                                                  left outer join list_item i on i.item_id = d.item_id
                   where i is null)
delete from list_item_details del
where del.item_detail_id in (select item_detail_id from to_delete);
