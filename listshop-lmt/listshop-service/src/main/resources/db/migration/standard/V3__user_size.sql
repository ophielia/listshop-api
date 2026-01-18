alter table dish_items
    add column if not exists user_size boolean default false;
