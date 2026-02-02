-- we don't know how to generate root <with-no-name> (class Root) :(

comment on database postgres is 'default administrative connection database';

grant connect, create, temporary on database testbank to bankuser;

create sequence authority_id_seq
    start with 1000;

alter sequence authority_id_seq owner to postgres;

create sequence authority_seq;

alter sequence authority_seq owner to postgres;

create sequence auto_tag_instructions_sequence
    start with 1000;

alter sequence auto_tag_instructions_sequence owner to postgres;

create sequence campaign_sequence
    start with 1000;

alter sequence campaign_sequence owner to postgres;

create sequence category_relation_sequence
    start with 1000;

alter sequence category_relation_sequence owner to postgres;

create sequence dish_item_sequence
    start with 1000;

alter sequence dish_item_sequence owner to postgres;

create sequence dish_sequence
    start with 1000;

alter sequence dish_sequence owner to postgres;

create sequence domain_unit_sequence
    start with 1000;

alter sequence domain_unit_sequence owner to postgres;

create sequence factor_sequence
    start with 1000;

alter sequence factor_sequence owner to postgres;

create sequence food_category_mapping_seq
    start with 1000;

alter sequence food_category_mapping_seq owner to postgres;

create sequence food_conversion_sequence
    start with 1000;

alter sequence food_conversion_sequence owner to postgres;

create sequence hibernate_sequence;

alter sequence hibernate_sequence owner to postgres;

create sequence list_item_detail_sequence
    start with 1000;

alter sequence list_item_detail_sequence owner to postgres;

create sequence list_item_sequence
    start with 1000;

alter sequence list_item_sequence owner to postgres;

create sequence list_layout_category_sequence
    start with 1000;

alter sequence list_layout_category_sequence owner to postgres;

create sequence list_layout_sequence
    start with 1000;

alter sequence list_layout_sequence owner to postgres;

create sequence list_sequence
    start with 1000;

alter sequence list_sequence owner to postgres;

create sequence list_tag_stats_sequence
    start with 1000;

alter sequence list_tag_stats_sequence owner to postgres;

create sequence meal_plan_sequence
    start with 1000;

alter sequence meal_plan_sequence owner to postgres;

create sequence meal_plan_slot_sequence
    start with 1000;

alter sequence meal_plan_slot_sequence owner to postgres;

create sequence modifier_mapping_sequence
    start with 1000;

alter sequence modifier_mapping_sequence owner to postgres;

create sequence proposal_approach_sequence
    start with 1000;

alter sequence proposal_approach_sequence owner to postgres;

create sequence proposal_context_sequence
    start with 1000;

alter sequence proposal_context_sequence owner to postgres;

create sequence proposal_context_slot_sequence
    start with 1000;

alter sequence proposal_context_slot_sequence owner to postgres;

create sequence proposal_dish_sequence
    start with 1000;

alter sequence proposal_dish_sequence owner to postgres;

create sequence proposal_sequence
    start with 1000;

alter sequence proposal_sequence owner to postgres;

create sequence proposal_slot_sequence
    start with 1000;

alter sequence proposal_slot_sequence owner to postgres;

create sequence shadow_tags_sequence
    start with 1000;

alter sequence shadow_tags_sequence owner to postgres;

create sequence tag_relation_sequence
    start with 1000;

alter sequence tag_relation_sequence owner to postgres;

create sequence tag_search_group_sequence;

alter sequence tag_search_group_sequence owner to postgres;

create sequence tag_sequence
    start with 1000;

alter sequence tag_sequence owner to postgres;

create sequence target_proposal_dish_sequence
    start with 1000;

alter sequence target_proposal_dish_sequence owner to postgres;

create sequence target_proposal_sequence
    start with 1000;

alter sequence target_proposal_sequence owner to postgres;

create sequence target_proposal_slot_sequence
    start with 1000;

alter sequence target_proposal_slot_sequence owner to postgres;

create sequence target_sequence
    start with 1000;

alter sequence target_sequence owner to postgres;

create sequence target_slot_sequence
    start with 1000
    increment by 2;

alter sequence target_slot_sequence owner to postgres;

create sequence token_sequence
    start with 57000;

alter sequence token_sequence owner to postgres;

create sequence unit_sequence
    start with 1000;

alter sequence unit_sequence owner to postgres;

create sequence user_device_sequence;

alter sequence user_device_sequence owner to postgres;

create sequence user_id_sequence
    start with 1000;

alter sequence user_id_sequence owner to postgres;

create sequence user_properties_id_seq
    start with 10000;

alter sequence user_properties_id_seq owner to postgres;

create table users
(
    user_id                  bigint not null
        primary key,
    email                    varchar(255),
    enabled                  boolean,
    last_password_reset_date timestamp,
    password                 varchar(255),
    username                 varchar(255),
    creation_date            timestamp,
    last_login               timestamp with time zone
);

alter table users
    owner to postgres;

create table dish
(
    dish_id         bigint not null
        primary key,
    description     varchar(255),
    dish_name       varchar(255),
    user_id         bigint
        constraint fk_dish__user_id
            references users
            on delete cascade,
    last_added      timestamp with time zone,
    auto_tag_status bigint,
    created_on      timestamp with time zone,
    reference       varchar(255)
);

alter table dish
    owner to postgres;

create table list
(
    list_id         bigint       not null
        primary key,
    created_on      timestamp with time zone,
    user_id         bigint
        constraint fk_list__user_id
            references users
            on delete cascade,
    list_types      varchar(255),
    list_layout_id  bigint,
    last_update     timestamp,
    meal_plan_id    bigint,
    is_starter_list boolean default false,
    name            varchar(255) not null
);

alter table list
    owner to postgres;

create table meal_plan
(
    meal_plan_id   bigint not null
        primary key,
    created        timestamp with time zone,
    meal_plan_type varchar(255),
    name           varchar(255),
    user_id        bigint
        constraint fk_meal_plan__user_id
            references users
            on delete cascade,
    target_id      bigint
);

alter table meal_plan
    owner to postgres;

create table authority
(
    authority_id bigint      not null
        primary key,
    name         varchar(50) not null,
    user_id      bigint
        constraint fk_authority__user_id
            references users
            on delete cascade
);

alter table authority
    owner to postgres;

create table auto_tag_instructions
(
    instruction_type varchar(31) not null,
    instruction_id   bigint      not null
        primary key,
    assign_tag_id    bigint,
    is_invert        boolean,
    search_terms     varchar(255),
    invert_filter    varchar(255)
);

alter table auto_tag_instructions
    owner to postgres;

create table list_stat_configs
(
    added_dish_factor          integer,
    added_single_factor        integer,
    added_list_factor          integer,
    added_starterlist_factor   integer,
    removed_dish_factor        integer,
    removed_single_factor      integer,
    removed_list_factor        integer,
    removed_starterlist_factor integer,
    frequent_threshold         double precision
);

alter table list_stat_configs
    owner to postgres;

create table list_tag_stats
(
    list_tag_stat_id    bigint not null
        primary key,
    added_count         integer,
    removed_count       integer,
    tag_id              bigint,
    user_id             bigint
        constraint fk_stats__user_id
            references users
            on delete cascade,
    added_to_dish       integer default 0,
    added_single        bigint  default 0,
    added_dish          bigint  default 0,
    added_list          bigint  default 0,
    added_starterlist   bigint  default 0,
    removed_single      bigint  default 0,
    removed_dish        bigint  default 0,
    removed_list        bigint  default 0,
    removed_starterlist bigint  default 0
);

alter table list_tag_stats
    owner to postgres;

create table campaigns
(
    campaign_id bigint not null
        constraint campaign_pkey
            primary key,
    created_on  timestamp,
    email       varchar(255),
    campaign    varchar(255),
    user_id     bigint
);

alter table campaigns
    owner to postgres;

create table dish_items
(
    dish_item_id        bigint not null,
    dish_id             bigint not null,
    tag_id              bigint not null,
    whole_quantity      integer,
    fractional_quantity varchar(56),
    quantity            double precision,
    unit_id             bigint,
    marker              varchar(256),
    unit_size           varchar(256),
    raw_modifiers       varchar(256),
    raw_entry           text,
    modifiers_processed boolean,
    user_size           boolean default false
);

alter table dish_items
    owner to postgres;

create table domain_unit
(
    domain_unit_id bigint      not null,
    domain_type    varchar(50) not null,
    unit_id        bigint
);

alter table domain_unit
    owner to postgres;

create table flyway_schema_history
(
    installed_rank integer                 not null
        constraint flyway_schema_history_pk
            primary key,
    version        varchar(50),
    description    varchar(200)            not null,
    type           varchar(20)             not null,
    script         varchar(1000)           not null,
    checksum       integer,
    installed_by   varchar(100)            not null,
    installed_on   timestamp default now() not null,
    execution_time integer                 not null,
    success        boolean                 not null
);

alter table flyway_schema_history
    owner to postgres;

create index flyway_schema_history_s_idx
    on flyway_schema_history (success);

create table food_categories
(
    category_id   bigint,
    category_code varchar(255),
    name          varchar(512)
);

alter table food_categories
    owner to postgres;

create table food_category_mapping
(
    food_category_mapping_id bigint,
    category_id              bigint,
    tag_id                   bigint
);

alter table food_category_mapping
    owner to postgres;

create table food_conversions
(
    conversion_id      bigint,
    food_id            bigint,
    fdc_id             bigint,
    amount             double precision,
    unit_name          varchar(128),
    gram_weight        double precision,
    unit_id            bigint,
    food_conversion_id bigint,
    integral           varchar(256),
    marker             varchar(256),
    sub_amount         varchar(256),
    info               varchar(256),
    unit_size          varchar(256),
    unit_default       boolean
);

alter table food_conversions
    owner to postgres;

create table foods
(
    food_id       bigint,
    fdc_id        bigint,
    name          text,
    category_id   bigint,
    marker        varchar(255),
    has_factor    boolean,
    conversion_id bigint,
    original_name varchar(256),
    integral      varchar(256)
);

alter table foods
    owner to postgres;

create table list_item_details
(
    item_detail_id           bigint            not null,
    item_id                  bigint            not null,
    used_count               integer default 1 not null,
    linked_list_id           bigint,
    linked_dish_id           bigint,
    whole_quantity           integer,
    fractional_quantity      varchar(56),
    quantity                 double precision,
    unit_id                  bigint,
    marker                   varchar(256),
    unit_size                varchar(256),
    raw_entry                text,
    orig_whole_quantity      integer,
    orig_fractional_quantity varchar(56),
    orig_quantity            double precision,
    orig_unit_id             bigint
);

alter table list_item_details
    owner to postgres;

create table list_layout
(
    layout_id  bigint not null
        primary key,
    name       varchar(255),
    user_id    bigint,
    is_default boolean
);

alter table list_layout
    owner to postgres;

create table list_category
(
    category_id   bigint not null
        primary key,
    name          varchar(255),
    layout_id     bigint
        constraint fkrhcs3i2p15y79hn00y5ic41gn
            references list_layout,
    display_order integer,
    is_default    boolean
);

alter table list_category
    owner to postgres;

create table meal_plan_slot
(
    meal_plan_slot_id bigint not null
        primary key,
    dish_dish_id      bigint
        constraint fkdit15dhtc9j583c1pp21c8ss0
            references dish,
    meal_plan_id      bigint not null
        constraint fk_meal_plan__meal_plan_slot
            references meal_plan
            on delete cascade
);

alter table meal_plan_slot
    owner to postgres;

create table modifier_mappings
(
    mapping_id      bigint       not null,
    modifier_type   varchar(50)  not null,
    modifier        varchar(100) not null,
    mapped_modifier varchar(100) not null,
    reference_id    bigint
);

alter table modifier_mappings
    owner to postgres;

create table proposal
(
    proposal_id    bigint not null
        primary key,
    user_id        bigint
        constraint fk_proposal__user_id
            references users
            on delete cascade,
    is_refreshable boolean,
    created        timestamp with time zone
);

alter table proposal
    owner to postgres;

create table proposal_context
(
    proposal_context_id    bigint not null
        primary key,
    proposal_id            bigint
        constraint fk_proposal_context__proposal
            references proposal
            on delete cascade,
    current_attempt_index  integer,
    current_approach_type  varchar(255),
    current_approach_index integer,
    meal_plan_id           bigint,
    target_id              bigint,
    target_hash_code       varchar(255),
    proposal_hash_code     varchar(255)
);

alter table proposal_context
    owner to postgres;

create table proposal_approach
(
    proposal_approach_id bigint not null,
    proposal_context_id  bigint not null
        constraint fk_proposal_approach__proposal_context
            references proposal_context
            on delete cascade,
    approach_number      integer,
    instructions         varchar(255)
);

alter table proposal_approach
    owner to postgres;

create table proposal_slot
(
    slot_id              bigint not null
        primary key,
    slot_number          integer,
    flat_matched_tag_ids varchar(255),
    proposal_id          bigint not null
        constraint fk_proposal_slot__proposal
            references proposal
            on delete cascade,
    picked_dish_id       bigint,
    slot_dish_tag_id     bigint
);

alter table proposal_slot
    owner to postgres;

create table proposal_dish
(
    dish_slot_id    bigint not null,
    slot_id         bigint not null
        constraint fk_proposal_dish__proposal_slot
            references proposal_slot
            on delete cascade,
    dish_id         bigint,
    matched_tag_ids varchar(255)
);

alter table proposal_dish
    owner to postgres;

create table q
(
    copy_single_dish integer
);

alter table q
    owner to postgres;

create table shadow_tags
(
    shadow_tag_id bigint not null
        primary key,
    dish_id       bigint,
    tag_id        bigint
);

alter table shadow_tags
    owner to postgres;

create table tag
(
    tag_id              bigint                not null
        primary key,
    description         varchar(255),
    name                varchar(255),
    tag_type            varchar(255),
    tag_type_default    boolean,
    is_verified         boolean,
    power               double precision,
    to_delete           boolean default false not null,
    replacement_tag_id  bigint,
    created_on          timestamp with time zone,
    updated_on          timestamp with time zone,
    category_updated_on timestamp with time zone,
    removed_on          timestamp with time zone,
    is_group            boolean default false not null,
    user_id             bigint,
    internal_status     bigint  default 1     not null,
    is_liquid           boolean,
    conversion_id       bigint,
    marker              varchar(256)
);

alter table tag
    owner to postgres;

create table category_tags
(
    category_id bigint not null
        constraint fkns9s1sef980caqqamoee8srdw
            references list_category,
    tag_id      bigint not null
        constraint fkclr8vrg8b1cwgwjsgcd5jtj6a
            references tag
);

alter table category_tags
    owner to postgres;

create table dish_tags
(
    dish_id bigint not null
        constraint fk_dish__dish_tags
            references dish
            on delete cascade,
    tag_id  bigint not null
        constraint fkpy8j9ypbt3d59bjs0hgl3wcct
            references tag
);

alter table dish_tags
    owner to postgres;

create table list_item
(
    item_id       bigint not null
        primary key,
    added_on      timestamp with time zone,
    crossed_off   timestamp with time zone,
    free_text     varchar(255),
    source        varchar(255),
    list_id       bigint
        constraint fk_list__list_id
            references list
            on delete cascade,
    list_category varchar(255),
    tag_id        bigint
        constraint fklcvoij9ynqfllhxgn9v6qpsh8
            references tag,
    used_count    integer,
    category_id   bigint,
    dish_sources  varchar(255),
    list_sources  varchar(255),
    removed_on    timestamp with time zone,
    updated_on    timestamp with time zone
);

alter table list_item
    owner to postgres;

create table tag_relation
(
    tag_relation_id bigint not null
        primary key,
    child_tag_id    bigint
        constraint fk6x8vvlp985udfs7g15uuxj42c
            references tag,
    parent_tag_id   bigint
        constraint fk3vyajpbcb8wl8380yntahtgtf
            references tag
);

alter table tag_relation
    owner to postgres;

create table target
(
    target_id      bigint not null
        primary key,
    created        timestamp with time zone,
    last_updated   timestamp with time zone,
    last_used      timestamp,
    target_name    varchar(255),
    target_tag_ids varchar(255),
    user_id        bigint
        constraint fk_target__user_id
            references users
            on delete cascade,
    proposal_id    bigint,
    target         varchar(255),
    expires        timestamp with time zone,
    target_type    varchar
);

alter table target
    owner to postgres;

create table target_slot
(
    target_slot_id   bigint not null
        primary key,
    slot_dish_tag_id bigint,
    slot_order       integer,
    target_id        bigint
        constraint fk_target__target_slot
            references target
            on delete cascade,
    target_tag_ids   varchar(255),
    target           varchar(255)
);

alter table target_slot
    owner to postgres;

create table tokens
(
    token_id    bigint not null
        primary key,
    created_on  timestamp,
    token_type  varchar(255),
    token_value varchar(255),
    user_id     bigint
);

alter table tokens
    owner to postgres;

create table units
(
    unit_id            bigint                not null
        constraint pk_units
            primary key,
    type               varchar(255),
    subtype            varchar(255),
    name               varchar(255),
    is_liquid          boolean default false not null,
    is_list_unit       boolean default false not null,
    is_dish_unit       boolean default false not null,
    is_weight          boolean default false not null,
    is_volume          boolean default false not null,
    is_tag_specific    boolean default false,
    excluded_domains   varchar(256),
    one_way_conversion boolean default false
);

alter table units
    owner to postgres;

create table factors
(
    factor_id     bigint not null
        constraint pk_factors
            primary key,
    factor        double precision,
    to_unit       bigint
        constraint fk_factors_on_to_unit
            references units,
    from_unit     bigint
        constraint fk_factors_on_from_unit
            references units,
    conversion_id bigint,
    reference_id  bigint,
    marker        varchar(256),
    unit_size     varchar(256),
    unit_default  boolean,
    tag_id        bigint
);

alter table factors
    owner to postgres;

create table user_devices
(
    user_device_id   bigint not null,
    user_id          bigint not null
        constraint fk_user_devices__user_id
            references users
            on delete cascade,
    name             varchar(255),
    model            varchar(255),
    os               varchar(255),
    os_version       varchar(255),
    client_type      varchar(15),
    build_number     varchar(255),
    client_device_id varchar(255),
    client_version   varchar(255),
    token            text,
    last_login       timestamp with time zone
);

alter table user_devices
    owner to postgres;

create table user_properties
(
    user_property_id bigint       not null,
    user_id          bigint,
    property_key     varchar(150) not null,
    property_value   varchar(150) not null,
    is_system        boolean
);

alter table user_properties
    owner to postgres;

create view admin_user_details
            (user_id, email, user_name, creation_date, last_login, list_count, meal_plan_count, dish_count) as
SELECT u.user_id,
       u.email,
       u.username                     AS user_name,
       u.creation_date,
       u.last_login,
       count(DISTINCT l.list_id)      AS list_count,
       count(DISTINCT m.meal_plan_id) AS meal_plan_count,
       count(DISTINCT d.dish_id)      AS dish_count
FROM users u
         LEFT JOIN list l ON u.user_id = l.user_id
         LEFT JOIN meal_plan m ON u.user_id = m.user_id
         LEFT JOIN dish d ON u.user_id = d.user_id
GROUP BY u.user_id, u.email, u.username, u.creation_date, u.last_login;

alter table admin_user_details
    owner to postgres;

create view calculated_stats(tag_id, user_id, frequent_threshold, factored_frequency) as
SELECT list_tag_stats.tag_id,
       list_tag_stats.user_id,
       c.frequent_threshold,
       (list_tag_stats.removed_single * c.removed_single_factor + list_tag_stats.removed_dish * c.removed_dish_factor +
        list_tag_stats.removed_list * c.removed_list_factor +
        list_tag_stats.removed_starterlist * c.removed_starterlist_factor)::numeric /
       (list_tag_stats.added_single * c.added_single_factor + list_tag_stats.added_dish * c.added_dish_factor +
        list_tag_stats.added_list * c.added_list_factor +
        list_tag_stats.added_starterlist * c.added_starterlist_factor)::numeric AS factored_frequency
FROM list_tag_stats,
     list_stat_configs c
WHERE (list_tag_stats.added_single * c.added_single_factor + list_tag_stats.added_dish * c.added_dish_factor +
       list_tag_stats.added_list * c.added_list_factor +
       list_tag_stats.added_starterlist * c.added_starterlist_factor) > 0;

alter table calculated_stats
    owner to postgres;

create function copy_single_dish(integer, integer) returns integer
    language plpgsql
as
$$
DECLARE
    pDishId ALIAS for $1;
    pNewUser
        ALIAS for $2;
    pDish
        record;
    nDish
        int;
BEGIN
    FOR pDish IN
        select *
        from Dish o
        where o.dish_id = pDishId
        LOOP
            insert
            into dish (dish_id, description, dish_name, user_id, last_added)
            select nextval('dish_sequence'), description, dish_name, pNewUser, last_added
            from dish
            where dish_id = pDish.dish_id returning dish_id
                into nDish;
            RAISE
                NOTICE 'dish created(new:%, old:%)',nDish,pDish.dish_id;
            insert into dish_items (dish_item_id, dish_id, tag_id)
            select nextval('dish_item_sequence'), nDish, tag_id
            from dish_tags
            where dish_id = pDish.dish_id;
        END LOOP;
    return 1;
END;
$$;

alter function copy_single_dish(integer, integer) owner to postgres;

