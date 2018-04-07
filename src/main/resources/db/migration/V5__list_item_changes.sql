ALTER TABLE public.list_item
ADD COLUMN dish_sources varchar(255);

ALTER TABLE public.list_item
ADD COLUMN list_sources varchar(255);


ALTER TABLE list_item
ADD COLUMN frequent_cross_off boolean default false;



ALTER TABLE list
ADD COLUMN last_update timestamp;