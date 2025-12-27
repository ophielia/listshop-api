CREATE TABLE if not exists list_item_details
(
    item_detail_id       bigint NOT NULL,
    item_id       bigint NOT NULL,
    used_count int NOT NULL default 1,
    linked_list_id bigint,
    linked_dish_id bigint,
    whole_quantity integer,
    fractional_quantity character varying(56),
    quantity double precision,
    unit_id bigint,
    marker              varchar(256),
    unit_size           varchar(256),
    raw_entry           text,
    orig_whole_quantity integer,
    orig_fractional_quantity character varying(56),
    orig_quantity double precision,
    orig_unit_id bigint
);

create sequence if not exists list_item_detail_sequence
    start with 1000;

--alter sequence list_item_detail_sequence owner to listshopstarter;

--drop table list_item_details;
--drop sequence list_item_detail_sequence;
