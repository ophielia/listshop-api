-- fix ownership previously created tables
ALTER TABLE factors OWNER TO postgres;
grant all on factors to bankuser;

ALTER TABLE dish_items OWNER TO postgres;
grant all on dish_items to bankuser;

ALTER TABLE units OWNER TO postgres;
grant all on units to bankuser;

create table foods
(
    food_id     bigint,
    fdc_id      bigint,
    name        text,
    category_id bigint,
    marker      varchar(255),
        has_factor bool
);
ALTER TABLE foods OWNER TO postgres;
grant all on foods to bankuser;

create table food_conversions
(
    conversion_id bigint,
    food_id     bigint,
    fdc_id      bigint,
    amount      double precision,
    unit_name   varchar(128),
    gram_weight double precision,
    unit_id     bigint
);

ALTER TABLE food_conversions OWNER TO postgres;
grant all on food_conversions to bankuser;

create table food_categories
(
    category_id   bigint,
    category_code varchar(255),
    name          varchar(512)
);

ALTER TABLE food_categories OWNER TO postgres;
grant all on food_categories to bankuser;

create table food_category_mapping
(
    food_category_mapping_id bigint,
    category_id   bigint,
    tag_id       bigint
);

ALTER TABLE food_category_mapping OWNER TO postgres;
grant all on food_category_mapping to bankuser;

CREATE SEQUENCE public.food_category_mapping_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;






