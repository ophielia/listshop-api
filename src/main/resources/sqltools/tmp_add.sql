ALTER TABLE public.list_item
ADD COLUMN category_id bigint;

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

INSERT INTO category_relation (category_relation_id, child_category_id, parent_category_id)
 VALUES (500, 1, 2);









=========================

ALTER TABLE PUBLIC.LIST_ITEM
DROP COLUMN CATEGORY_ID;

ALTER TABLE LIST_CATEGORY
ADD COLUMN DISPLAY_ORDER INT;

DROP SEQUENCE CATEGORY_RELATION_SEQUENCE;

DROP TABLE CATEGORY_RELATION;