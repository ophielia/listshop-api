
create table foods
(
    food_id     bigint,
    fdc_id      bigint,
    name        text,
    category_id bigint,
    marker      varchar(255)
);

grant all on foods to bank;

create table food_conversions
(
    food_id     bigint,
    fdc_id      bigint,
    amount      double precision,
    unit_name   varchar(128),
    gram_weight double precision,
    unit_id     bigint
);


create table food_categories
(
    category_id   bigint,
    category_code varchar(255),
    name          varchar(512)
);

grant all on food_categories to bank;

create table food_category_mapping
(
    category_id   bigint,
    tag_id       bigint
);

grant all on food_category_mapping to bank;



