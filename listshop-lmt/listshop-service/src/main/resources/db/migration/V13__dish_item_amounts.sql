alter table dish_items add column whole_quantity int;
alter table dish_items add column fractional_quantity varchar(56);
alter table dish_items add column quantity double precision;
alter table dish_items add column unit_id bigint;
alter table dish_items add column marker varchar(256);
alter table dish_items add column unit_size varchar(256);
alter table dish_items add column raw_modifiers text;
alter table dish_items add column modifiers_processed boolean;


CREATE TABLE modifier_mappings
(
    mapping_id bigint                 NOT NULL,
    modifier_type    character varying(50) NOT NULL,
    modifier    character varying(100) NOT NULL,
    mapped_modifier    character varying(100) NOT NULL
);

CREATE SEQUENCE modifier_mapping_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;