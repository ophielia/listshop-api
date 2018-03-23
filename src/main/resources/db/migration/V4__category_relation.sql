alter table public.list_item
add column category_id bigint;

ALTER TABLE LIST_CATEGORY
ADD COLUMN DISPLAY_ORDER INT;

CREATE SEQUENCE category_relation_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE category_relation (
    category_relation_id bigint NOT NULL,
    child_category_id bigint,
    parent_category_id bigint
);

ALTER TABLE category_relation
    ADD CONSTRAINT category_relation__list_category_id_child FOREIGN KEY (child_category_id) REFERENCES list_category(category_id);

ALTER TABLE category_relation
    ADD CONSTRAINT category_relation__list_category_id_parent FOREIGN KEY (parent_category_id) REFERENCES list_category(category_id);

ALTER TABLE category_relation
    ADD CONSTRAINT category_relation_pkey PRIMARY KEY (category_relation_id);


update list_category set display_order = category_id;