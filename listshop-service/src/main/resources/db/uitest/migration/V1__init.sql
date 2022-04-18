--
-- PostgreSQL database dump
--

-- Dumped from database version 10.16
-- Dumped by pg_dump version 10.16

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner:
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner:
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET default_tablespace = '';

SET default_with_oids = false;



--CREATE ROLE postgres WITH
--    LOGIN  encrypted password 'postgres'
--    SUPERUSER
--    INHERIT
--    NOCREATEDB
--    NOCREATEROLE
--    NOREPLICATION;
--
-- Name: authority; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.authority
(
    authority_id bigint                NOT NULL,
    name         character varying(50) NOT NULL,
    user_id      bigint
);


ALTER TABLE public.authority
    OWNER TO postgres;

--
-- Name: authority_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.authority_id_seq
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.authority_id_seq
    OWNER TO postgres;

--
-- Name: authority_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.authority_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.authority_seq
    OWNER TO postgres;

--
-- Name: auto_tag_instructions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.auto_tag_instructions
(
    instruction_type character varying(31) NOT NULL,
    instruction_id   bigint                NOT NULL,
    assign_tag_id    bigint,
    is_invert        boolean,
    search_terms     character varying(255),
    invert_filter    character varying(255)
);


ALTER TABLE public.auto_tag_instructions
    OWNER TO postgres;

--
-- Name: auto_tag_instructions_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.auto_tag_instructions_sequence
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.auto_tag_instructions_sequence
    OWNER TO postgres;

--
-- Name: list_stat_configs; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.list_stat_configs
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


ALTER TABLE public.list_stat_configs
    OWNER TO postgres;

--
-- Name: list_tag_stats; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.list_tag_stats
(
    list_tag_stat_id    bigint NOT NULL,
    added_count         integer,
    removed_count       integer,
    tag_id              bigint,
    user_id             bigint,
    added_to_dish       integer DEFAULT 0,
    added_single        bigint  DEFAULT 0,
    added_dish          bigint  DEFAULT 0,
    added_list          bigint  DEFAULT 0,
    added_starterlist   bigint  DEFAULT 0,
    removed_single      bigint  DEFAULT 0,
    removed_dish        bigint  DEFAULT 0,
    removed_list        bigint  DEFAULT 0,
    removed_starterlist bigint  DEFAULT 0
);


ALTER TABLE public.list_tag_stats
    OWNER TO postgres;




--
-- Name: category_relation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.category_relation
(
    category_relation_id bigint NOT NULL,
    child_category_id    bigint,
    parent_category_id   bigint
);


ALTER TABLE public.category_relation
    OWNER TO postgres;

--
-- Name: category_relation_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.category_relation_sequence
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.category_relation_sequence
    OWNER TO postgres;

--
-- Name: category_tags; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.category_tags
(
    category_id bigint NOT NULL,
    tag_id      bigint NOT NULL
);


ALTER TABLE public.category_tags
    OWNER TO postgres;

--
-- Name: dish; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.dish
(
    dish_id         bigint NOT NULL,
    description     character varying(255),
    dish_name       character varying(255),
    user_id         bigint,
    last_added      timestamp with time zone,
    auto_tag_status bigint,
    created_on      timestamp with time zone,
    reference       character varying(255)
);


ALTER TABLE public.dish
    OWNER TO postgres;

--
-- Name: dish_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.dish_sequence
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dish_sequence
    OWNER TO postgres;

--
-- Name: dish_tags; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.dish_tags
(
    dish_id bigint NOT NULL,
    tag_id  bigint NOT NULL
);


ALTER TABLE public.dish_tags
    OWNER TO postgres;


--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence
    OWNER TO postgres;

--
-- Name: list; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.list
(
    list_id         bigint                 NOT NULL,
    created_on      timestamp with time zone,
    user_id         bigint,
    list_types      character varying(255),
    list_layout_id  bigint,
    last_update     timestamp without time zone,
    meal_plan_id    bigint,
    is_starter_list boolean DEFAULT false,
    name            character varying(255) NOT NULL
);


ALTER TABLE public.list
    OWNER TO postgres;

--
-- Name: list_category; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.list_category
(
    category_id   bigint NOT NULL,
    name          character varying(255),
    layout_id     bigint,
    display_order integer,
    is_default    boolean
);


ALTER TABLE public.list_category
    OWNER TO postgres;

--
-- Name: list_item; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.list_item
(
    item_id       bigint NOT NULL,
    added_on      timestamp with time zone,
    crossed_off   timestamp with time zone,
    free_text     character varying(255),
    source        character varying(255),
    list_id       bigint,
    list_category character varying(255),
    tag_id        bigint,
    used_count    integer,
    category_id   bigint,
    dish_sources  character varying(255),
    list_sources  character varying(255),
    removed_on    timestamp with time zone,
    updated_on    timestamp with time zone
);


ALTER TABLE public.list_item
    OWNER TO postgres;

--
-- Name: list_item_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.list_item_sequence
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.list_item_sequence
    OWNER TO postgres;

--
-- Name: list_layout; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.list_layout
(
    layout_id   bigint NOT NULL,
    layout_type character varying(255),
    name        character varying(255)
);


ALTER TABLE public.list_layout
    OWNER TO postgres;

--
-- Name: list_layout_category_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.list_layout_category_sequence
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.list_layout_category_sequence
    OWNER TO postgres;

--
-- Name: list_layout_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.list_layout_sequence
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.list_layout_sequence
    OWNER TO postgres;

--
-- Name: list_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.list_sequence
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.list_sequence
    OWNER TO postgres;

--
-- Name: list_tag_stats_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.list_tag_stats_sequence
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.list_tag_stats_sequence
    OWNER TO postgres;

--
-- Name: meal_plan; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.meal_plan
(
    meal_plan_id   bigint NOT NULL,
    created        timestamp with time zone,
    meal_plan_type character varying(255),
    name           character varying(255),
    user_id        bigint,
    target_id      bigint
);


ALTER TABLE public.meal_plan
    OWNER TO postgres;

--
-- Name: meal_plan_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.meal_plan_sequence
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.meal_plan_sequence
    OWNER TO postgres;

--
-- Name: meal_plan_slot; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.meal_plan_slot
(
    meal_plan_slot_id bigint NOT NULL,
    dish_dish_id      bigint,
    meal_plan_id      bigint NOT NULL
);


ALTER TABLE public.meal_plan_slot
    OWNER TO postgres;

--
-- Name: meal_plan_slot_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.meal_plan_slot_sequence
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.meal_plan_slot_sequence
    OWNER TO postgres;

--
-- Name: proposal; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.proposal
(
    proposal_id    bigint NOT NULL,
    user_id        bigint,
    is_refreshable boolean,
    created        timestamp with time zone
);


ALTER TABLE public.proposal
    OWNER TO postgres;

--
-- Name: proposal_approach; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.proposal_approach
(
    proposal_approach_id bigint NOT NULL,
    proposal_context_id  bigint NOT NULL,
    approach_number      integer,
    instructions         character varying(255)
);


ALTER TABLE public.proposal_approach
    OWNER TO postgres;

--
-- Name: proposal_approach_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.proposal_approach_sequence
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.proposal_approach_sequence
    OWNER TO postgres;

--
-- Name: proposal_context; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.proposal_context
(
    proposal_context_id    bigint NOT NULL,
    proposal_id            bigint,
    current_attempt_index  integer,
    current_approach_type  character varying(255),
    current_approach_index integer,
    meal_plan_id           bigint,
    target_id              bigint,
    target_hash_code       character varying(255),
    proposal_hash_code     character varying(255)
);


ALTER TABLE public.proposal_context
    OWNER TO postgres;

--
-- Name: proposal_context_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.proposal_context_sequence
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.proposal_context_sequence
    OWNER TO postgres;

--
-- Name: proposal_context_slot_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.proposal_context_slot_sequence
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.proposal_context_slot_sequence
    OWNER TO postgres;

--
-- Name: proposal_dish; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.proposal_dish
(
    dish_slot_id    bigint NOT NULL,
    slot_id         bigint NOT NULL,
    dish_id         bigint,
    matched_tag_ids character varying(255)
);


ALTER TABLE public.proposal_dish
    OWNER TO postgres;

--
-- Name: proposal_dish_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.proposal_dish_sequence
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.proposal_dish_sequence
    OWNER TO postgres;

--
-- Name: proposal_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.proposal_sequence
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.proposal_sequence
    OWNER TO postgres;

--
-- Name: proposal_slot; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.proposal_slot
(
    slot_id              bigint NOT NULL,
    slot_number          integer,
    flat_matched_tag_ids character varying(255),
    proposal_id          bigint NOT NULL,
    picked_dish_id       bigint,
    slot_dish_tag_id     bigint
);


ALTER TABLE public.proposal_slot
    OWNER TO postgres;

--
-- Name: proposal_slot_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.proposal_slot_sequence
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.proposal_slot_sequence
    OWNER TO postgres;

--
-- Name: tag_relation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tag_relation
(
    tag_relation_id bigint NOT NULL,
    child_tag_id    bigint,
    parent_tag_id   bigint
);


ALTER TABLE public.tag_relation
    OWNER TO postgres;


--
-- Name: shadow_tags; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.shadow_tags
(
    shadow_tag_id bigint NOT NULL,
    dish_id       bigint,
    tag_id        bigint
);


ALTER TABLE public.shadow_tags
    OWNER TO postgres;

--
-- Name: shadow_tags_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.shadow_tags_sequence
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.shadow_tags_sequence
    OWNER TO postgres;

--
-- Name: tag; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tag
(
    tag_id              bigint                NOT NULL,
    description         character varying(255),
    name                character varying(255),
    tag_type            character varying(255),
    tag_type_default    boolean,
    assign_select       boolean,
    search_select       boolean,
    is_verified         boolean,
    power               double precision,
    to_delete           boolean DEFAULT false NOT NULL,
    replacement_tag_id  bigint,
    created_on          timestamp with time zone,
    updated_on          timestamp with time zone,
    category_updated_on timestamp with time zone,
    removed_on          timestamp with time zone
);


ALTER TABLE public.tag
    OWNER TO postgres;


--
-- Name: tag_relation_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tag_relation_sequence
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tag_relation_sequence
    OWNER TO postgres;

--
-- Name: tag_search_group; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tag_search_group
(
    tag_search_group_id bigint NOT NULL,
    group_id            bigint,
    member_id           bigint
);


ALTER TABLE public.tag_search_group
    OWNER TO postgres;

--
-- Name: tag_search_group_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tag_search_group_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tag_search_group_sequence
    OWNER TO postgres;

--
-- Name: tag_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tag_sequence
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tag_sequence
    OWNER TO postgres;

--
-- Name: target; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.target
(
    target_id      bigint NOT NULL,
    created        timestamp with time zone,
    last_updated   timestamp with time zone,
    last_used      timestamp without time zone,
    target_name    character varying(255),
    target_tag_ids character varying(255),
    user_id        bigint,
    proposal_id    bigint,
    target         character varying(255),
    expires        timestamp with time zone,
    target_type    character varying
);


ALTER TABLE public.target
    OWNER TO postgres;

--
-- Name: target_proposal_dish_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.target_proposal_dish_sequence
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.target_proposal_dish_sequence
    OWNER TO postgres;

--
-- Name: target_proposal_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.target_proposal_sequence
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.target_proposal_sequence
    OWNER TO postgres;

--
-- Name: target_proposal_slot_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.target_proposal_slot_sequence
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.target_proposal_slot_sequence
    OWNER TO postgres;

--
-- Name: target_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.target_sequence
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.target_sequence
    OWNER TO postgres;

--
-- Name: target_slot; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.target_slot
(
    target_slot_id   bigint NOT NULL,
    slot_dish_tag_id bigint,
    slot_order       integer,
    target_id        bigint,
    target_tag_ids   character varying(255),
    target           character varying(255)
);


ALTER TABLE public.target_slot
    OWNER TO postgres;

--
-- Name: target_slot_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.target_slot_sequence
    START WITH 10000
    INCREMENT BY 2
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.target_slot_sequence
    OWNER TO postgres;

--
-- Name: user_device_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.user_device_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_device_sequence
    OWNER TO postgres;

--
-- Name: user_devices; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_devices
(
    user_device_id   bigint NOT NULL,
    user_id          bigint NOT NULL,
    name             character varying(255),
    model            character varying(255),
    os               character varying(255),
    os_version       character varying(255),
    client_type      character varying(15),
    build_number     character varying(255),
    client_device_id character varying(255),
    client_version   character varying(255),
    token            text,
    last_login       timestamp with time zone
);


ALTER TABLE public.user_devices
    OWNER TO postgres;

--
-- Name: user_id_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.user_id_sequence
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_id_sequence
    OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users
(
    user_id                  bigint NOT NULL,
    email                    character varying(255),
    enabled                  boolean,
    last_password_reset_date timestamp without time zone,
    password                 character varying(255),
    username                 character varying(255),
    creation_date            timestamp without time zone,
    last_login               timestamp with time zone
);


ALTER TABLE public.users
    OWNER TO postgres;

-- new table, tokens

CREATE TABLE public.tokens
(
    token_id    bigint NOT NULL,
    created_on  timestamp without time zone,
    token_type  character varying(255) COLLATE pg_catalog."default",
    token_value character varying(255) COLLATE pg_catalog."default",
    user_id     bigint,
    CONSTRAINT tokens_pkey PRIMARY KEY (token_id)
)
    WITH (
        OIDS = FALSE
    );


CREATE SEQUENCE public.token_sequence
    INCREMENT 1
    START 57000
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;



GRANT ALL ON TABLE public.token_sequence TO postgres;
GRANT ALL ON TABLE public.tokens TO postgres;
