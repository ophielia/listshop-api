CREATE TABLE list_item_details
(
    item_detail_id       bigint NOT NULL,
    item_id       bigint NOT NULL,
    count int NOT NULL default 1,
    linked_list_id bigint,
    linked_dish_id bigint,
    whole_quantity integer,
    fractional_quantity character varying(56),
    quantity double precision,
    unit_id bigint,
    orig_whole_quantity integer,
    orig_fractional_quantity character varying(56),
    orig_quantity double precision,
    orig_unit_id bigint
);

