ALTER TABLE public.list_item
ADD COLUMN dish_sources varchar(255);

ALTER TABLE public.list_item
ADD COLUMN list_sources varchar(255);

ALTER TABLE list_item
ADD COLUMN frequent_cross_off boolean;

ALTER TABLE list
ADD COLUMN last_update timestamp;

-- no longer used
ALTER TABLE public.list_item
DROP COLUMN sources varchar(255);
--itemsourcetype
=========================

ALTER TABLE public.list_item
DROP COLUMN dish_sources ;

ALTER TABLE public.list_item
DROP COLUMN list_sources ;

ALTER TABLE public.list_item
DROP COLUMN frequent_cross_off ;

ALTER TABLE list
DROP COLUMN last_update ;

