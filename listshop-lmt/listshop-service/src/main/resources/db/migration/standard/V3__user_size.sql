alter table dish_items
    add column if not exists user_size boolean default false;

alter table factors
    add column if not exists tag_id bigint;
