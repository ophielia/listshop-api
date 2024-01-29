alter table factors add column tag_id BIGINT;

create table foods
(
    food_id     bigint,
    fdc_id      bigint,
    name        text,
    category_id bigint,
    marker      varchar(255)
);

alter table foods
    owner to postgres;

create table food_conversions
(
    food_id     bigint,
    fdc_id      bigint,
    amount      double precision,
    unit_name   varchar(128),
    gram_weight double precision,
    unit_id     bigint
);

alter table food_conversions
    owner to postgres;

create table food_categories
(
    category_id   bigint,
    category_code varchar(255),
    name          varchar(512)
);

alter table food_categories
    owner to postgres;

alter table tag add column internal_status bigint;


-- alter table factors drop column tag_id