ALTER TABLE public.list
    ADD COLUMN list_layout_id bigint  ;


update list set list_layout_id = 5;

