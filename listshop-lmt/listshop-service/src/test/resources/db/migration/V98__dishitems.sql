-- new table dish_items

CREATE TABLE public.dish_items
(
    dish_item_id bigint NOT NULL,
    dish_id      bigint NOT NULL,
    tag_id       bigint NOT NULL
);

CREATE SEQUENCE public.dish_item_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;