alter table factors add column tag_id BIGINT;

create table foods
(
    food_id     bigint,
    fdc_id      bigint,
    name        text,
    category_id bigint,
    marker      varchar(255)
);

alter table foods    owner to postgres;
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

alter table food_conversions  owner to postgres;
grant all on food_conversions to bank;

create table food_categories
(
    category_id   bigint,
    category_code varchar(255),
    name          varchar(512)
);

alter table food_categories    owner to postgres;
grant all on food_categories to bank;

create table food_tag_mapping
(
    category_id   bigint,
    tag_id       bigint
);

alter table food_tag_mapping    owner to postgres;
grant all on food_tag_mapping to bank;

alter table tag add column internal_status bigint default 1;
update tag set internal_status = 1;
alter table tag alter column internal_status set not null;


