--
-- PostgreSQL database dump
--

-- Dumped from database version 10.16
-- Dumped by pg_dump version 10.16

SET
statement_timeout = 0;
SET
lock_timeout = 0;
SET
idle_in_transaction_session_timeout = 0;
SET
client_encoding = 'UTF8';
SET
standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET
check_function_bodies = false;
SET
xmloption = content;
SET
client_min_messages = warning;
SET
row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner:
--

CREATE
EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner:
--

COMMENT
ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET
default_tablespace = '';

SET
default_with_oids = false;

create sequence public.authority_id_seq
    start with 1023;

alter sequence public.authority_id_seq owner to postgres;

create sequence public.authority_seq
    start with 6;

alter sequence public.authority_seq owner to postgres;

create sequence public.auto_tag_instructions_sequence
    start with 1004;

alter sequence public.auto_tag_instructions_sequence owner to postgres;

create sequence public.category_relation_sequence
    start with 50059;

alter sequence public.category_relation_sequence owner to postgres;

create sequence public.dish_sequence
    start with 58033;

alter sequence public.dish_sequence owner to postgres;

create sequence public.hibernate_sequence
    start with 6;

alter sequence public.hibernate_sequence owner to postgres;

create sequence public.list_item_sequence
    start with 85210;

alter sequence public.list_item_sequence owner to postgres;

create sequence public.list_layout_category_sequence
    start with 52021;

alter sequence public.list_layout_category_sequence owner to postgres;

create sequence public.list_layout_sequence
    start with 14;

alter sequence public.list_layout_sequence owner to postgres;

create sequence public.list_sequence
    start with 50991;

alter sequence public.list_sequence owner to postgres;

create sequence public.list_tag_stats_sequence
    start with 54750;

alter sequence public.list_tag_stats_sequence owner to postgres;

create sequence public.meal_plan_sequence
    start with 50690;

alter sequence public.meal_plan_sequence owner to postgres;

create sequence public.meal_plan_slot_sequence
    start with 53074;

alter sequence public.meal_plan_slot_sequence owner to postgres;

create sequence public.proposal_approach_sequence
    start with 1004;

alter sequence public.proposal_approach_sequence owner to postgres;

create sequence public.proposal_context_sequence
    start with 50008;

alter sequence public.proposal_context_sequence owner to postgres;

create sequence public.proposal_context_slot_sequence
    start with 50049;

alter sequence public.proposal_context_slot_sequence owner to postgres;

create sequence public.proposal_dish_sequence
    start with 1040;

alter sequence public.proposal_dish_sequence owner to postgres;

create sequence public.proposal_sequence
    start with 1004;

alter sequence public.proposal_sequence owner to postgres;

create sequence public.proposal_slot_sequence
    start with 1007;

alter sequence public.proposal_slot_sequence owner to postgres;

create sequence public.shadow_tags_sequence
    start with 52512;

alter sequence public.shadow_tags_sequence owner to postgres;

create sequence public.tag_relation_sequence
    start with 51378;

alter sequence public.tag_relation_sequence owner to postgres;

create sequence public.tag_search_group_sequence
    start with 279;

alter sequence public.tag_search_group_sequence owner to postgres;

create sequence public.tag_sequence
    start with 51601;

alter sequence public.tag_sequence owner to postgres;

create sequence public.target_proposal_dish_sequence
    start with 52100;

alter sequence public.target_proposal_dish_sequence owner to postgres;

create sequence public.target_proposal_sequence
    start with 50004;

alter sequence public.target_proposal_sequence owner to postgres;

create sequence public.target_proposal_slot_sequence
    start with 50018;

alter sequence public.target_proposal_slot_sequence owner to postgres;

create sequence public.target_sequence
    start with 50006;

alter sequence public.target_sequence owner to postgres;

create sequence public.target_slot_sequence
    start with 1024
    increment by 2;

alter sequence public.target_slot_sequence owner to postgres;

create sequence public.token_sequence
    start with 57008;

alter sequence public.token_sequence owner to postgres;

create sequence public.user_device_sequence
    start with 741;

alter sequence public.user_device_sequence owner to postgres;

create sequence public.user_id_sequence
    start with 46;

alter sequence public.user_id_sequence owner to postgres;

create sequence public.user_properties_id_seq
    start with 10000;

alter sequence public.user_properties_id_seq owner to postgres;

create table if not exists public.dish
(
    dish_id
    bigint
    not
    null,
    description
    varchar
(
    255
),
    dish_name varchar
(
    255
),
    user_id bigint,
    last_added timestamp with time zone,
    auto_tag_status bigint,
    created_on timestamp with time zone,
                             reference varchar (255)
    );

alter table public.dish
    owner to postgres;

alter table public.dish
    add primary key (dish_id);

create table if not exists public.list
(
    list_id
    bigint
    not
    null,
    created_on
    timestamp
    with
    time
    zone,
    user_id
    bigint,
    list_types
    varchar
(
    255
),
    list_layout_id bigint,
    last_update timestamp,
    meal_plan_id bigint,
    is_starter_list boolean default false,
    name varchar
(
    255
) not null
    );

alter table public.list
    owner to postgres;

alter table public.list
    add primary key (list_id);

create table if not exists public.meal_plan
(
    meal_plan_id
    bigint
    not
    null,
    created
    timestamp
    with
    time
    zone,
    meal_plan_type
    varchar
(
    255
),
    name varchar
(
    255
),
    user_id bigint,
    target_id bigint
    );

alter table public.meal_plan
    owner to postgres;

alter table public.meal_plan
    add primary key (meal_plan_id);

create table if not exists public.users
(
    user_id
    bigint
    not
    null,
    email
    varchar
(
    255
),
    enabled boolean,
    last_password_reset_date timestamp,
    password varchar
(
    255
),
    username varchar
(
    255
),
    creation_date timestamp,
    last_login timestamp with time zone
                             );

alter table public.users
    owner to postgres;

alter table public.users
    add primary key (user_id);


create table if not exists public.authority
(
    authority_id
    bigint
    not
    null,
    name
    varchar
(
    50
) not null,
    user_id bigint
    );

alter table public.authority
    owner to postgres;

alter table public.authority
    add primary key (authority_id);

create table if not exists public.auto_tag_instructions
(
    instruction_type varchar
(
    31
) not null,
    instruction_id bigint not null,
    assign_tag_id bigint,
    is_invert boolean,
    search_terms varchar
(
    255
),
    invert_filter varchar
(
    255
)
    );

alter table public.auto_tag_instructions
    owner to postgres;

alter table public.auto_tag_instructions
    add primary key (instruction_id);

create table if not exists public.list_stat_configs
(
    added_dish_factor
    integer,
    added_single_factor
    integer,
    added_list_factor
    integer,
    added_starterlist_factor
    integer,
    removed_dish_factor
    integer,
    removed_single_factor
    integer,
    removed_list_factor
    integer,
    removed_starterlist_factor
    integer,
    frequent_threshold
    double
    precision
);

alter table public.list_stat_configs
    owner to postgres;

create table if not exists public.list_tag_stats
(
    list_tag_stat_id
    bigint
    not
    null,
    added_count
    integer,
    removed_count
    integer,
    tag_id
    bigint,
    user_id
    bigint,
    added_to_dish
    integer
    default
    0,
    added_single
    bigint
    default
    0,
    added_dish
    bigint
    default
    0,
    added_list
    bigint
    default
    0,
    added_starterlist
    bigint
    default
    0,
    removed_single
    bigint
    default
    0,
    removed_dish
    bigint
    default
    0,
    removed_list
    bigint
    default
    0,
    removed_starterlist
    bigint
    default
    0
);

alter table public.list_tag_stats
    owner to postgres;

alter table public.list_tag_stats
    add primary key (list_tag_stat_id);


create table if not exists public.category_tags
(
    category_id
    bigint
    not
    null,
    tag_id
    bigint
    not
    null
);

alter table public.category_tags
    owner to postgres;

create table if not exists public.dish_tags
(
    dish_id
    bigint
    not
    null,
    tag_id
    bigint
    not
    null
);

alter table public.dish_tags
    owner to postgres;



create table if not exists public.list_category
(
    category_id
    bigint
    not
    null,
    name
    varchar
(
    255
),
    layout_id bigint,
    display_order integer,
    is_default boolean
    );

alter table public.list_category
    owner to postgres;

alter table public.list_category
    add primary key (category_id);


create table if not exists public.list_item
(
    item_id
    bigint
    not
    null,
    added_on
    timestamp
    with
    time
    zone,
    crossed_off
    timestamp
    with
    time
    zone,
    free_text
    varchar
(
    255
),
    source varchar
(
    255
),
    list_id bigint,
    list_category varchar
(
    255
),
    tag_id bigint,
    used_count integer,
    category_id bigint,
    dish_sources varchar
(
    255
),
    list_sources varchar
(
    255
),
    removed_on timestamp with time zone,
    updated_on timestamp with time zone
                             );

alter table public.list_item
    owner to postgres;

alter table public.list_item
    add primary key (item_id);



create table if not exists public.list_layout
(
    layout_id
    bigint
    not
    null,
    name
    varchar
(
    255
),
    user_id bigint,
    is_default boolean
    );

alter table public.list_layout
    owner to postgres;

alter table public.list_layout
    add primary key (layout_id);


create table if not exists public.meal_plan_slot
(
    meal_plan_slot_id
    bigint
    not
    null,
    dish_dish_id
    bigint,
    meal_plan_id
    bigint
    not
    null
);

alter table public.meal_plan_slot
    owner to postgres;

alter table public.meal_plan_slot
    add primary key (meal_plan_slot_id);

create table if not exists public.proposal
(
    proposal_id
    bigint
    not
    null,
    user_id
    bigint,
    is_refreshable
    boolean,
    created
    timestamp
    with
    time
    zone
);

alter table public.proposal
    owner to postgres;

alter table public.proposal
    add primary key (proposal_id);


create table if not exists public.proposal_approach
(
    proposal_approach_id
    bigint
    not
    null,
    proposal_context_id
    bigint
    not
    null,
    approach_number
    integer,
    instructions
    varchar
(
    255
)
    );

alter table public.proposal_approach
    owner to postgres;

create table if not exists public.proposal_context
(
    proposal_context_id
    bigint
    not
    null,
    proposal_id
    bigint,
    current_attempt_index
    integer,
    current_approach_type
    varchar
(
    255
),
    current_approach_index integer,
    meal_plan_id bigint,
    target_id bigint,
    target_hash_code varchar
(
    255
),
    proposal_hash_code varchar
(
    255
)
    );

alter table public.proposal_context
    owner to postgres;

alter table public.proposal_context
    add primary key (proposal_context_id);


create table if not exists public.proposal_dish
(
    dish_slot_id
    bigint
    not
    null,
    slot_id
    bigint
    not
    null,
    dish_id
    bigint,
    matched_tag_ids
    varchar
(
    255
)
    );

alter table public.proposal_dish
    owner to postgres;

create table if not exists public.proposal_slot
(
    slot_id
    bigint
    not
    null,
    slot_number
    integer,
    flat_matched_tag_ids
    varchar
(
    255
),
    proposal_id bigint not null,
    picked_dish_id bigint,
    slot_dish_tag_id bigint
    );

alter table public.proposal_slot
    owner to postgres;

alter table public.proposal_slot
    add primary key (slot_id);


create table if not exists public.q
(
    copy_single_dish integer
);

alter table public.q
    owner to postgres;

create table if not exists public.shadow_tags
(
    shadow_tag_id
    bigint
    not
    null,
    dish_id
    bigint,
    tag_id
    bigint
);

alter table public.shadow_tags
    owner to postgres;

alter table public.shadow_tags
    add primary key (shadow_tag_id);

create table if not exists public.tag
(
    tag_id
    bigint
    not
    null,
    description
    varchar
(
    255
),
    name varchar
(
    255
),
    tag_type varchar
(
    255
),
    tag_type_default boolean,
    is_verified boolean,
    power double precision,
    to_delete boolean default false not null,
    replacement_tag_id bigint,
    created_on timestamp with time zone,
    updated_on timestamp with time zone,
    category_updated_on timestamp with time zone,
    removed_on timestamp with time zone,
                             is_group boolean default false not null,
                             user_id bigint
                             );

alter table public.tag
    owner to postgres;

alter table public.tag
    add primary key (tag_id);


create table if not exists public.tag_relation
(
    tag_relation_id
    bigint
    not
    null,
    child_tag_id
    bigint,
    parent_tag_id
    bigint
);

alter table public.tag_relation
    owner to postgres;

alter table public.tag_relation
    add primary key (tag_relation_id);

create table if not exists public.target
(
    target_id
    bigint
    not
    null,
    created
    timestamp
    with
    time
    zone,
    last_updated
    timestamp
    with
    time
    zone,
    last_used
    timestamp,
    target_name
    varchar
(
    255
),
    target_tag_ids varchar
(
    255
),
    user_id bigint,
    proposal_id bigint,
    target varchar
(
    255
),
    expires timestamp with time zone,
                          target_type varchar
                          );

alter table public.target
    owner to postgres;

alter table public.target
    add primary key (target_id);


create table if not exists public.target_slot
(
    target_slot_id
    bigint
    not
    null,
    slot_dish_tag_id
    bigint,
    slot_order
    integer,
    target_id
    bigint,
    target_tag_ids
    varchar
(
    255
),
    target varchar
(
    255
)
    );

alter table public.target_slot
    owner to postgres;

alter table public.target_slot
    add primary key (target_slot_id);

create table if not exists public.tokens
(
    token_id
    bigint
    not
    null,
    created_on
    timestamp,
    token_type
    varchar
(
    255
),
    token_value varchar
(
    255
),
    user_id bigint
    );

alter table public.tokens
    owner to postgres;

alter table public.tokens
    add primary key (token_id);

create table if not exists public.user_devices
(
    user_device_id
    bigint
    not
    null,
    user_id
    bigint
    not
    null,
    name
    varchar
(
    255
),
    model varchar
(
    255
),
    os varchar
(
    255
),
    os_version varchar
(
    255
),
    client_type varchar
(
    15
),
    build_number varchar
(
    255
),
    client_device_id varchar
(
    255
),
    client_version varchar
(
    255
),
    token text,
    last_login timestamp with time zone
                             );

alter table public.user_devices
    owner to postgres;

create table if not exists public.user_properties
(
    user_property_id
    bigint
    not
    null,
    user_id
    bigint,
    property_key
    varchar
(
    150
) not null,
    property_value varchar
(
    150
) not null
    );

alter table public.user_properties
    owner to postgres;

CREATE TABLE if not exists public.campaigns
(
    campaign_id
    bigint
    NOT
    NULL,
    created_on
    timestamp
    without
    time
    zone,
    email
    character
    varying
(
    255
) COLLATE pg_catalog."default",
    campaign character varying
(
    255
) COLLATE pg_catalog."default",
    user_id bigint,
    CONSTRAINT campaign_pkey PRIMARY KEY
(
    campaign_id
)
    )
    WITH (
        OIDS = FALSE
        );


CREATE SEQUENCE if not exists public.campaign_sequence
    INCREMENT 1
    START 1000
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

alter table public.campaigns
    owner to postgres;