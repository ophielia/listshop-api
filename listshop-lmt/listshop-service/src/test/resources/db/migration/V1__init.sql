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



do
$body$
    declare
        num_users integer;
    begin
        SELECT count(*)
        into num_users
        FROM pg_user
        WHERE usename = 'bank';

        IF num_users = 0 THEN
            create role bank password 'dummypassword';
        END IF;
    end
$body$
;



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
-- Name: calculated_stats; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.calculated_stats AS
SELECT list_tag_stats.tag_id,
       list_tag_stats.user_id,
       c.frequent_threshold,
       ((((((list_tag_stats.removed_single * c.removed_single_factor) +
            (list_tag_stats.removed_dish * c.removed_dish_factor)) +
           (list_tag_stats.removed_list * c.removed_list_factor)) +
          (list_tag_stats.removed_starterlist * c.removed_starterlist_factor)))::numeric /
        (((((list_tag_stats.added_single * c.added_single_factor) + (list_tag_stats.added_dish * c.added_dish_factor)) +
           (list_tag_stats.added_list * c.added_list_factor)) +
          (list_tag_stats.added_starterlist * c.added_starterlist_factor)))::numeric) AS factored_frequency
FROM public.list_tag_stats,
     public.list_stat_configs c
WHERE (((((list_tag_stats.added_single * c.added_single_factor) + (list_tag_stats.added_dish * c.added_dish_factor)) +
         (list_tag_stats.added_list * c.added_list_factor)) +
        (list_tag_stats.added_starterlist * c.added_starterlist_factor)) > 0);


ALTER TABLE public.calculated_stats
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
-- Name: tag_extended; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.tag_extended AS
WITH parent_ids AS (
    SELECT DISTINCT parent.parent_tag_id
    FROM public.tag_relation parent
    WHERE (parent.parent_tag_id IS NOT NULL)
)
SELECT t.tag_id,
       t.assign_select,
       t.category_updated_on,
       t.created_on,
       t.description,
       t.is_verified,
       t.name,
       t.power,
       t.removed_on,
       t.replacement_tag_id,
       t.search_select,
       t.tag_type,
       t.tag_type_default,
       t.to_delete,
       t.updated_on,
       r.parent_tag_id,
       (ip.parent_tag_id IS NOT NULL) AS is_parent
FROM ((public.tag t
    JOIN public.tag_relation r ON ((t.tag_id = r.child_tag_id)))
         LEFT JOIN parent_ids ip ON ((t.tag_id = ip.parent_tag_id)));


ALTER TABLE public.tag_extended
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

--
-- Name: authority authority_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.authority
    ADD CONSTRAINT authority_pkey PRIMARY KEY (authority_id);


--
-- Name: auto_tag_instructions auto_tag_instructions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.auto_tag_instructions
    ADD CONSTRAINT auto_tag_instructions_pkey PRIMARY KEY (instruction_id);


--
-- Name: category_relation category_relation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category_relation
    ADD CONSTRAINT category_relation_pkey PRIMARY KEY (category_relation_id);


--
-- Name: dish dish_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dish
    ADD CONSTRAINT dish_pkey PRIMARY KEY (dish_id);



--
-- Name: list_category list_category_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.list_category
    ADD CONSTRAINT list_category_pkey PRIMARY KEY (category_id);


--
-- Name: list_item list_item_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.list_item
    ADD CONSTRAINT list_item_pkey PRIMARY KEY (item_id);


--
-- Name: list_layout list_layout_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.list_layout
    ADD CONSTRAINT list_layout_pkey PRIMARY KEY (layout_id);


--
-- Name: list list_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.list
    ADD CONSTRAINT list_pkey PRIMARY KEY (list_id);


--
-- Name: list_tag_stats list_tag_stats_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.list_tag_stats
    ADD CONSTRAINT list_tag_stats_pkey PRIMARY KEY (list_tag_stat_id);


--
-- Name: meal_plan meal_plan_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.meal_plan
    ADD CONSTRAINT meal_plan_pkey PRIMARY KEY (meal_plan_id);


--
-- Name: meal_plan_slot meal_plan_slot_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.meal_plan_slot
    ADD CONSTRAINT meal_plan_slot_pkey PRIMARY KEY (meal_plan_slot_id);


--
-- Name: proposal_context proposal_context_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.proposal_context
    ADD CONSTRAINT proposal_context_pkey PRIMARY KEY (proposal_context_id);


--
-- Name: shadow_tags shadow_tags_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shadow_tags
    ADD CONSTRAINT shadow_tags_pkey PRIMARY KEY (shadow_tag_id);


--
-- Name: tag tag_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tag
    ADD CONSTRAINT tag_pkey PRIMARY KEY (tag_id);


--
-- Name: tag_relation tag_relation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tag_relation
    ADD CONSTRAINT tag_relation_pkey PRIMARY KEY (tag_relation_id);


--
-- Name: tag_search_group tag_search_group_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tag_search_group
    ADD CONSTRAINT tag_search_group_pkey PRIMARY KEY (tag_search_group_id);


--
-- Name: target target_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.target
    ADD CONSTRAINT target_pkey PRIMARY KEY (target_id);


--
-- Name: target_slot target_slot_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.target_slot
    ADD CONSTRAINT target_slot_pkey PRIMARY KEY (target_slot_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);



--
-- Name: category_relation category_relation__list_category_id_child; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category_relation
    ADD CONSTRAINT category_relation__list_category_id_child FOREIGN KEY (child_category_id) REFERENCES public.list_category (category_id);


--
-- Name: category_relation category_relation__list_category_id_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category_relation
    ADD CONSTRAINT category_relation__list_category_id_parent FOREIGN KEY (parent_category_id) REFERENCES public.list_category (category_id);


--
-- Name: list_item fk1ddq3ct1ulogjn5ijs8ert7hw; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.list_item
    ADD CONSTRAINT fk1ddq3ct1ulogjn5ijs8ert7hw FOREIGN KEY (list_id) REFERENCES public.list (list_id);


--
-- Name: tag_relation fk3vyajpbcb8wl8380yntahtgtf; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tag_relation
    ADD CONSTRAINT fk3vyajpbcb8wl8380yntahtgtf FOREIGN KEY (parent_tag_id) REFERENCES public.tag (tag_id);


--
-- Name: dish fk4cvbymf9m9quckcouehn0p414; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dish
    ADD CONSTRAINT fk4cvbymf9m9quckcouehn0p414 FOREIGN KEY (user_id) REFERENCES public.users (user_id);


--
-- Name: tag_relation fk6x8vvlp985udfs7g15uuxj42c; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tag_relation
    ADD CONSTRAINT fk6x8vvlp985udfs7g15uuxj42c FOREIGN KEY (child_tag_id) REFERENCES public.tag (tag_id);


--
-- Name: dish_tags fkbh371e2vv53a3arqea0hf3jkl; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dish_tags
    ADD CONSTRAINT fkbh371e2vv53a3arqea0hf3jkl FOREIGN KEY (dish_id) REFERENCES public.dish (dish_id);


--
-- Name: category_tags fkclr8vrg8b1cwgwjsgcd5jtj6a; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category_tags
    ADD CONSTRAINT fkclr8vrg8b1cwgwjsgcd5jtj6a FOREIGN KEY (tag_id) REFERENCES public.tag (tag_id);


--
-- Name: meal_plan_slot fkdit15dhtc9j583c1pp21c8ss0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.meal_plan_slot
    ADD CONSTRAINT fkdit15dhtc9j583c1pp21c8ss0 FOREIGN KEY (dish_dish_id) REFERENCES public.dish (dish_id);


--
-- Name: meal_plan_slot fkhhja2slk7gr34nhgcnlyw21ge; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.meal_plan_slot
    ADD CONSTRAINT fkhhja2slk7gr34nhgcnlyw21ge FOREIGN KEY (meal_plan_id) REFERENCES public.meal_plan (meal_plan_id);


--
-- Name: authority fkka37hl6mopj61rfbe97si18p8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.authority
    ADD CONSTRAINT fkka37hl6mopj61rfbe97si18p8 FOREIGN KEY (user_id) REFERENCES public.users (user_id);


--
-- Name: list_item fklcvoij9ynqfllhxgn9v6qpsh8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.list_item
    ADD CONSTRAINT fklcvoij9ynqfllhxgn9v6qpsh8 FOREIGN KEY (tag_id) REFERENCES public.tag (tag_id);


--
-- Name: category_tags fkns9s1sef980caqqamoee8srdw; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category_tags
    ADD CONSTRAINT fkns9s1sef980caqqamoee8srdw FOREIGN KEY (category_id) REFERENCES public.list_category (category_id);


--
-- Name: dish_tags fkpy8j9ypbt3d59bjs0hgl3wcct; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dish_tags
    ADD CONSTRAINT fkpy8j9ypbt3d59bjs0hgl3wcct FOREIGN KEY (tag_id) REFERENCES public.tag (tag_id);


--
-- Name: list_category fkrhcs3i2p15y79hn00y5ic41gn; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.list_category
    ADD CONSTRAINT fkrhcs3i2p15y79hn00y5ic41gn FOREIGN KEY (layout_id) REFERENCES public.list_layout (layout_id);


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: margaretmartin
--

GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO bankuser;


--
-- Name: TABLE authority; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.authority TO bankuser;


--
-- Name: TABLE auto_tag_instructions; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.auto_tag_instructions TO bankuser;


--
-- Name: TABLE list_stat_configs; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.list_stat_configs TO bankuser;


--
-- Name: TABLE list_tag_stats; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.list_tag_stats TO bankuser;


--
-- Name: TABLE calculated_stats; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.calculated_stats TO bankuser;


--
-- Name: TABLE category_relation; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.category_relation TO bankuser;


--
-- Name: TABLE category_tags; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.category_tags TO bankuser;


--
-- Name: TABLE dish; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.dish TO bankuser;


--
-- Name: TABLE dish_tags; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.dish_tags TO bankuser;



--
-- Name: TABLE list; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.list TO bankuser;


--
-- Name: TABLE list_category; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.list_category TO bankuser;


--
-- Name: TABLE list_item; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.list_item TO bankuser;


--
-- Name: TABLE list_layout; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.list_layout TO bankuser;


--
-- Name: TABLE meal_plan; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.meal_plan TO bankuser;


--
-- Name: TABLE meal_plan_slot; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.meal_plan_slot TO bankuser;


--
-- Name: TABLE proposal; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.proposal TO bankuser;


--
-- Name: TABLE proposal_approach; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.proposal_approach TO bankuser;


--
-- Name: TABLE proposal_context; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.proposal_context TO bankuser;


--
-- Name: TABLE proposal_dish; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.proposal_dish TO bankuser;


--
-- Name: TABLE proposal_slot; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.proposal_slot TO bankuser;


--
-- Name: TABLE tag_relation; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.tag_relation TO bankuser;


--
-- Name: TABLE shadow_tags; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.shadow_tags TO bankuser;


--
-- Name: TABLE tag; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.tag TO bankuser;


--
-- Name: TABLE tag_extended; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.tag_extended TO bankuser;


--
-- Name: TABLE tag_search_group; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.tag_search_group TO bankuser;


--
-- Name: TABLE target; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.target TO bankuser;


--
-- Name: TABLE target_slot; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.target_slot TO bankuser;


--
-- Name: TABLE user_devices; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.user_devices TO bankuser;


--
-- Name: TABLE users; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.users TO bankuser;


--
-- PostgreSQL database dump complete
--
