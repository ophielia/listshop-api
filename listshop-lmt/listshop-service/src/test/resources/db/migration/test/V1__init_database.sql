--
-- PostgreSQL database dump
--

-- Dumped from database version 14.11 (Homebrew)
-- Dumped by pg_dump version 14.11 (Homebrew)

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
-- Name: dishdevtesttest; Type: DATABASE; Schema: -; Owner: margaretmartin
--


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
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;




--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- Name: copy_single_dish(integer, integer); Type: FUNCTION; Schema: public; Owner: bank
--

CREATE FUNCTION public.copy_single_dish(integer, integer) RETURNS integer
    LANGUAGE plpgsql
    AS $_$
DECLARE
    pDishId ALIAS for $1;
    pNewUser ALIAS for $2;
    pDish record;
    nDish int;
BEGIN
    FOR pDish IN select *
                 from Dish o
                 where o.dish_id = pDishId
        LOOP
            insert into dish (dish_id, description, dish_name, user_id, last_added)
            select nextval('dish_sequence'), description, dish_name, pNewUser, last_added
            from dish
            where dish_id = pDish.dish_id
            returning dish_id into nDish;
            RAISE NOTICE 'dish created(new:%, old:%)',nDish,pDish.dish_id;
            insert into dish_tags (dish_id, tag_id)
            select nDish, tag_id
            from dish_tags
            where dish_id = pDish.dish_id;
        END LOOP;
    return 1;
END;
$_$;


ALTER FUNCTION public.copy_single_dish(integer, integer) OWNER TO postgres;

SET default_tablespace = '';

--
-- Name: dish; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.dish (
    dish_id bigint NOT NULL,
    description character varying(255),
    dish_name character varying(255),
    user_id bigint,
    last_added timestamp with time zone,
    auto_tag_status bigint,
    created_on timestamp with time zone,
    reference character varying(255)
);


ALTER TABLE public.dish OWNER TO postgres;

--
-- Name: list; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.list (
    list_id bigint NOT NULL,
    created_on timestamp with time zone,
    user_id bigint,
    list_types character varying(255),
    list_layout_id bigint,
    last_update timestamp without time zone,
    meal_plan_id bigint,
    is_starter_list boolean DEFAULT false,
    name character varying(255) NOT NULL
);


ALTER TABLE public.list OWNER TO postgres;

--
-- Name: meal_plan; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.meal_plan (
    meal_plan_id bigint NOT NULL,
    created timestamp with time zone,
    meal_plan_type character varying(255),
    name character varying(255),
    user_id bigint,
    target_id bigint
);


ALTER TABLE public.meal_plan OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.users (
    user_id bigint NOT NULL,
    email character varying(255),
    enabled boolean,
    last_password_reset_date timestamp without time zone,
    password character varying(255),
    username character varying(255),
    creation_date timestamp without time zone,
    last_login timestamp with time zone
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: admin_user_details; Type: VIEW; Schema: public; Owner: bank
--

CREATE VIEW public.admin_user_details AS
 SELECT u.user_id,
    u.email,
    u.username AS user_name,
    u.creation_date,
    u.last_login,
    count(DISTINCT l.list_id) AS list_count,
    count(DISTINCT m.meal_plan_id) AS meal_plan_count,
    count(DISTINCT d.dish_id) AS dish_count
   FROM (((public.users u
     LEFT JOIN public.list l ON ((u.user_id = l.user_id)))
     LEFT JOIN public.meal_plan m ON ((u.user_id = m.user_id)))
     LEFT JOIN public.dish d ON ((u.user_id = d.user_id)))
  GROUP BY u.user_id, u.email, u.username, u.creation_date, u.last_login;


ALTER TABLE public.admin_user_details OWNER TO postgres;

--
-- Name: authority; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.authority (
    authority_id bigint NOT NULL,
    name character varying(50) NOT NULL,
    user_id bigint
);


ALTER TABLE public.authority OWNER TO postgres;

--
-- Name: authority_id_seq; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.authority_id_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.authority_id_seq OWNER TO postgres;

--
-- Name: authority_seq; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.authority_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.authority_seq OWNER TO postgres;

--
-- Name: auto_tag_instructions; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.auto_tag_instructions (
    instruction_type character varying(31) NOT NULL,
    instruction_id bigint NOT NULL,
    assign_tag_id bigint,
    is_invert boolean,
    search_terms character varying(255),
    invert_filter character varying(255)
);


ALTER TABLE public.auto_tag_instructions OWNER TO postgres;

--
-- Name: auto_tag_instructions_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.auto_tag_instructions_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.auto_tag_instructions_sequence OWNER TO postgres;

--
-- Name: list_stat_configs; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.list_stat_configs (
    added_dish_factor integer,
    added_single_factor integer,
    added_list_factor integer,
    added_starterlist_factor integer,
    removed_dish_factor integer,
    removed_single_factor integer,
    removed_list_factor integer,
    removed_starterlist_factor integer,
    frequent_threshold double precision
);


ALTER TABLE public.list_stat_configs OWNER TO postgres;

--
-- Name: list_tag_stats; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.list_tag_stats (
    list_tag_stat_id bigint NOT NULL,
    added_count integer,
    removed_count integer,
    tag_id bigint,
    user_id bigint,
    added_to_dish integer DEFAULT 0,
    added_single bigint DEFAULT 0,
    added_dish bigint DEFAULT 0,
    added_list bigint DEFAULT 0,
    added_starterlist bigint DEFAULT 0,
    removed_single bigint DEFAULT 0,
    removed_dish bigint DEFAULT 0,
    removed_list bigint DEFAULT 0,
    removed_starterlist bigint DEFAULT 0
);


ALTER TABLE public.list_tag_stats OWNER TO postgres;

--
-- Name: calculated_stats; Type: VIEW; Schema: public; Owner: bank
--

CREATE VIEW public.calculated_stats AS
 SELECT list_tag_stats.tag_id,
    list_tag_stats.user_id,
    c.frequent_threshold,
    ((((((list_tag_stats.removed_single * c.removed_single_factor) + (list_tag_stats.removed_dish * c.removed_dish_factor)) + (list_tag_stats.removed_list * c.removed_list_factor)) + (list_tag_stats.removed_starterlist * c.removed_starterlist_factor)))::numeric / (((((list_tag_stats.added_single * c.added_single_factor) + (list_tag_stats.added_dish * c.added_dish_factor)) + (list_tag_stats.added_list * c.added_list_factor)) + (list_tag_stats.added_starterlist * c.added_starterlist_factor)))::numeric) AS factored_frequency
   FROM public.list_tag_stats,
    public.list_stat_configs c
  WHERE (((((list_tag_stats.added_single * c.added_single_factor) + (list_tag_stats.added_dish * c.added_dish_factor)) + (list_tag_stats.added_list * c.added_list_factor)) + (list_tag_stats.added_starterlist * c.added_starterlist_factor)) > 0);


ALTER TABLE public.calculated_stats OWNER TO postgres;

--
-- Name: campaign_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.campaign_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.campaign_sequence OWNER TO postgres;

--
-- Name: campaigns; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.campaigns (
    campaign_id bigint NOT NULL,
    created_on timestamp without time zone,
    email character varying(255),
    campaign character varying(255),
    user_id bigint
);


ALTER TABLE public.campaigns OWNER TO postgres;

--
-- Name: category_relation_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.category_relation_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.category_relation_sequence OWNER TO postgres;

--
-- Name: category_tags; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.category_tags (
    category_id bigint NOT NULL,
    tag_id bigint NOT NULL
);


ALTER TABLE public.category_tags OWNER TO postgres;

--
-- Name: dish_item_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.dish_item_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dish_item_sequence OWNER TO postgres;

--
-- Name: dish_items; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.dish_items (
    dish_item_id bigint NOT NULL,
    dish_id bigint NOT NULL,
    tag_id bigint NOT NULL,
    whole_quantity integer,
    fractional_quantity character varying(56),
    quantity double precision,
    unit_id bigint,
    marker character varying(256),
    unit_size character varying(256),
    raw_modifiers character varying(256),
    raw_entry text,
    modifiers_processed boolean
);


ALTER TABLE public.dish_items OWNER TO postgres;

--
-- Name: dish_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.dish_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dish_sequence OWNER TO postgres;

--
-- Name: dish_tags; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.dish_tags (
    dish_id bigint NOT NULL,
    tag_id bigint NOT NULL
);


ALTER TABLE public.dish_tags OWNER TO postgres;

--
-- Name: domain_unit; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.domain_unit (
    domain_unit_id bigint NOT NULL,
    domain_type character varying(50) NOT NULL,
    unit_id bigint
);


ALTER TABLE public.domain_unit OWNER TO postgres;

--
-- Name: domain_unit_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.domain_unit_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.domain_unit_sequence OWNER TO postgres;

--
-- Name: factor_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.factor_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.factor_sequence OWNER TO postgres;

--
-- Name: factors; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.factors (
    factor_id bigint NOT NULL,
    factor double precision,
    to_unit bigint,
    from_unit bigint,
    conversion_id bigint,
    reference_id bigint,
    marker character varying(256),
    unit_size character varying(256),
    unit_default boolean
);


ALTER TABLE public.factors OWNER TO postgres;


--
-- Name: food_categories; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.food_categories (
    category_id bigint,
    category_code character varying(255),
    name character varying(512)
);


ALTER TABLE public.food_categories OWNER TO postgres;

--
-- Name: food_category_mapping; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.food_category_mapping (
    food_category_mapping_id bigint,
    category_id bigint,
    tag_id bigint
);


ALTER TABLE public.food_category_mapping OWNER TO postgres;

--
-- Name: food_category_mapping_seq; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.food_category_mapping_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.food_category_mapping_seq OWNER TO postgres;

--
-- Name: food_conversions; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.food_conversions (
    conversion_id bigint,
    food_id bigint,
    fdc_id bigint,
    amount double precision,
    unit_name character varying(128),
    gram_weight double precision,
    unit_id bigint,
    food_conversion_id bigint,
    integral character varying(256),
    marker character varying(256),
    sub_amount character varying(256),
    info character varying(256),
    unit_size character varying(256),
    unit_default boolean
);


ALTER TABLE public.food_conversions OWNER TO postgres;

--
-- Name: foods; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.foods (
    food_id bigint,
    fdc_id bigint,
    name text,
    category_id bigint,
    marker character varying(255),
    has_factor boolean,
    conversion_id bigint,
    original_name character varying(256),
    integral character varying(256)
);


ALTER TABLE public.foods OWNER TO postgres;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO postgres;

--
-- Name: list_category; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.list_category (
    category_id bigint NOT NULL,
    name character varying(255),
    layout_id bigint,
    display_order integer,
    is_default boolean
);


ALTER TABLE public.list_category OWNER TO postgres;

--
-- Name: list_item; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.list_item (
    item_id bigint NOT NULL,
    added_on timestamp with time zone,
    crossed_off timestamp with time zone,
    free_text character varying(255),
    source character varying(255),
    list_id bigint,
    list_category character varying(255),
    tag_id bigint,
    used_count integer,
    category_id bigint,
    dish_sources character varying(255),
    list_sources character varying(255),
    removed_on timestamp with time zone,
    updated_on timestamp with time zone
);


ALTER TABLE public.list_item OWNER TO postgres;

--
-- Name: list_item_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.list_item_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.list_item_sequence OWNER TO postgres;

--
-- Name: list_layout; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.list_layout (
    layout_id bigint NOT NULL,
    name character varying(255),
    user_id bigint,
    is_default boolean
);


ALTER TABLE public.list_layout OWNER TO postgres;

--
-- Name: list_layout_category_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.list_layout_category_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.list_layout_category_sequence OWNER TO postgres;

--
-- Name: list_layout_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.list_layout_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.list_layout_sequence OWNER TO postgres;

--
-- Name: list_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.list_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.list_sequence OWNER TO postgres;

--
-- Name: list_tag_stats_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.list_tag_stats_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.list_tag_stats_sequence OWNER TO postgres;

--
-- Name: meal_plan_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.meal_plan_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.meal_plan_sequence OWNER TO postgres;

--
-- Name: meal_plan_slot; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.meal_plan_slot (
    meal_plan_slot_id bigint NOT NULL,
    dish_dish_id bigint,
    meal_plan_id bigint NOT NULL
);


ALTER TABLE public.meal_plan_slot OWNER TO postgres;

--
-- Name: meal_plan_slot_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.meal_plan_slot_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.meal_plan_slot_sequence OWNER TO postgres;

--
-- Name: modifier_mapping_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.modifier_mapping_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.modifier_mapping_sequence OWNER TO postgres;

--
-- Name: modifier_mappings; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.modifier_mappings (
    mapping_id bigint NOT NULL,
    modifier_type character varying(50) NOT NULL,
    modifier character varying(100) NOT NULL,
    mapped_modifier character varying(100) NOT NULL,
    reference_id bigint
);


ALTER TABLE public.modifier_mappings OWNER TO postgres;

--
-- Name: proposal; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.proposal (
    proposal_id bigint NOT NULL,
    user_id bigint,
    is_refreshable boolean,
    created timestamp with time zone
);


ALTER TABLE public.proposal OWNER TO postgres;

--
-- Name: proposal_approach; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.proposal_approach (
    proposal_approach_id bigint NOT NULL,
    proposal_context_id bigint NOT NULL,
    approach_number integer,
    instructions character varying(255)
);


ALTER TABLE public.proposal_approach OWNER TO postgres;

--
-- Name: proposal_approach_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.proposal_approach_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.proposal_approach_sequence OWNER TO postgres;

--
-- Name: proposal_context; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.proposal_context (
    proposal_context_id bigint NOT NULL,
    proposal_id bigint,
    current_attempt_index integer,
    current_approach_type character varying(255),
    current_approach_index integer,
    meal_plan_id bigint,
    target_id bigint,
    target_hash_code character varying(255),
    proposal_hash_code character varying(255)
);


ALTER TABLE public.proposal_context OWNER TO postgres;

--
-- Name: proposal_context_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.proposal_context_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.proposal_context_sequence OWNER TO postgres;

--
-- Name: proposal_context_slot_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.proposal_context_slot_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.proposal_context_slot_sequence OWNER TO postgres;

--
-- Name: proposal_dish; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.proposal_dish (
    dish_slot_id bigint NOT NULL,
    slot_id bigint NOT NULL,
    dish_id bigint,
    matched_tag_ids character varying(255)
);


ALTER TABLE public.proposal_dish OWNER TO postgres;

--
-- Name: proposal_dish_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.proposal_dish_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.proposal_dish_sequence OWNER TO postgres;

--
-- Name: proposal_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.proposal_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.proposal_sequence OWNER TO postgres;

--
-- Name: proposal_slot; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.proposal_slot (
    slot_id bigint NOT NULL,
    slot_number integer,
    flat_matched_tag_ids character varying(255),
    proposal_id bigint NOT NULL,
    picked_dish_id bigint,
    slot_dish_tag_id bigint
);


ALTER TABLE public.proposal_slot OWNER TO postgres;

--
-- Name: proposal_slot_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.proposal_slot_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.proposal_slot_sequence OWNER TO postgres;

--
-- Name: q; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.q (
    copy_single_dish integer
);


ALTER TABLE public.q OWNER TO postgres;

--
-- Name: shadow_tags; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.shadow_tags (
    shadow_tag_id bigint NOT NULL,
    dish_id bigint,
    tag_id bigint
);


ALTER TABLE public.shadow_tags OWNER TO postgres;

--
-- Name: shadow_tags_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.shadow_tags_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.shadow_tags_sequence OWNER TO postgres;

--
-- Name: tag; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.tag (
    tag_id bigint NOT NULL,
    description character varying(255),
    name character varying(255),
    tag_type character varying(255),
    tag_type_default boolean,
    is_verified boolean,
    power double precision,
    to_delete boolean DEFAULT false NOT NULL,
    replacement_tag_id bigint,
    created_on timestamp with time zone,
    updated_on timestamp with time zone,
    category_updated_on timestamp with time zone,
    removed_on timestamp with time zone,
    is_group boolean DEFAULT false NOT NULL,
    user_id bigint,
    internal_status bigint DEFAULT 1 NOT NULL,
    is_liquid boolean,
    conversion_id bigint,
    marker character varying(256)
);


ALTER TABLE public.tag OWNER TO postgres;

--
-- Name: tag_relation; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.tag_relation (
    tag_relation_id bigint NOT NULL,
    child_tag_id bigint,
    parent_tag_id bigint
);


ALTER TABLE public.tag_relation OWNER TO postgres;

--
-- Name: tag_relation_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.tag_relation_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tag_relation_sequence OWNER TO postgres;

--
-- Name: tag_search_group_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.tag_search_group_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tag_search_group_sequence OWNER TO postgres;

--
-- Name: tag_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.tag_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tag_sequence OWNER TO postgres;

--
-- Name: target; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.target (
    target_id bigint NOT NULL,
    created timestamp with time zone,
    last_updated timestamp with time zone,
    last_used timestamp without time zone,
    target_name character varying(255),
    target_tag_ids character varying(255),
    user_id bigint,
    proposal_id bigint,
    target character varying(255),
    expires timestamp with time zone,
    target_type character varying
);


ALTER TABLE public.target OWNER TO postgres;

--
-- Name: target_proposal_dish_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.target_proposal_dish_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.target_proposal_dish_sequence OWNER TO postgres;

--
-- Name: target_proposal_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.target_proposal_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.target_proposal_sequence OWNER TO postgres;

--
-- Name: target_proposal_slot_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.target_proposal_slot_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.target_proposal_slot_sequence OWNER TO postgres;

--
-- Name: target_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.target_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.target_sequence OWNER TO postgres;

--
-- Name: target_slot; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.target_slot (
    target_slot_id bigint NOT NULL,
    slot_dish_tag_id bigint,
    slot_order integer,
    target_id bigint,
    target_tag_ids character varying(255),
    target character varying(255)
);


ALTER TABLE public.target_slot OWNER TO postgres;

--
-- Name: target_slot_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.target_slot_sequence
    START WITH 1000
    INCREMENT BY 2
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.target_slot_sequence OWNER TO postgres;

--
-- Name: token_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.token_sequence
    START WITH 57000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.token_sequence OWNER TO postgres;

--
-- Name: tokens; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.tokens (
    token_id bigint NOT NULL,
    created_on timestamp without time zone,
    token_type character varying(255),
    token_value character varying(255),
    user_id bigint
);


ALTER TABLE public.tokens OWNER TO postgres;

--
-- Name: unit_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.unit_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.unit_sequence OWNER TO postgres;

--
-- Name: units; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.units (
    unit_id bigint NOT NULL,
    type character varying(255),
    subtype character varying(255),
    name character varying(255),
    is_liquid boolean DEFAULT false NOT NULL,
    is_list_unit boolean DEFAULT false NOT NULL,
    is_dish_unit boolean DEFAULT false NOT NULL,
    is_weight boolean DEFAULT false NOT NULL,
    is_volume boolean DEFAULT false NOT NULL,
    is_tag_specific boolean DEFAULT false,
    excluded_domains character varying(256),
    one_way_conversion boolean DEFAULT false
);


ALTER TABLE public.units OWNER TO postgres;

--
-- Name: user_device_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.user_device_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_device_sequence OWNER TO postgres;

--
-- Name: user_devices; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.user_devices (
    user_device_id bigint NOT NULL,
    user_id bigint NOT NULL,
    name character varying(255),
    model character varying(255),
    os character varying(255),
    os_version character varying(255),
    client_type character varying(15),
    build_number character varying(255),
    client_device_id character varying(255),
    client_version character varying(255),
    token text,
    last_login timestamp with time zone
);


ALTER TABLE public.user_devices OWNER TO postgres;

--
-- Name: user_id_sequence; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.user_id_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_id_sequence OWNER TO postgres;

--
-- Name: user_properties; Type: TABLE; Schema: public; Owner: bank
--

CREATE TABLE public.user_properties (
    user_property_id bigint NOT NULL,
    user_id bigint,
    property_key character varying(150) NOT NULL,
    property_value character varying(150) NOT NULL,
    is_system boolean
);


ALTER TABLE public.user_properties OWNER TO postgres;

--
-- Name: user_properties_id_seq; Type: SEQUENCE; Schema: public; Owner: bank
--

CREATE SEQUENCE public.user_properties_id_seq
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_properties_id_seq OWNER TO postgres;

--
--
-- Name: authority_id_seq; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.authority_id_seq', 1057, true);


--
-- Name: authority_seq; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.authority_seq', 5, true);


--
-- Name: auto_tag_instructions_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.auto_tag_instructions_sequence', 1003, true);


--
-- Name: campaign_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.campaign_sequence', 1000, false);


--
-- Name: category_relation_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.category_relation_sequence', 50058, true);


--
-- Name: dish_item_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.dish_item_sequence', 28625, true);


--
-- Name: dish_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.dish_sequence', 57699, true);


--
-- Name: domain_unit_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.domain_unit_sequence', 1033, true);


--
-- Name: factor_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.factor_sequence', 1028, true);


--
-- Name: food_category_mapping_seq; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.food_category_mapping_seq', 1000, false);


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.hibernate_sequence', 11, true);


--
-- Name: list_item_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.list_item_sequence', 92413, true);


--
-- Name: list_layout_category_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.list_layout_category_sequence', 52024, true);


--
-- Name: list_layout_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.list_layout_sequence', 15, true);


--
-- Name: list_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.list_sequence', 51066, true);


--
-- Name: list_tag_stats_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.list_tag_stats_sequence', 54470, true);


--
-- Name: meal_plan_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.meal_plan_sequence', 50708, true);


--
-- Name: meal_plan_slot_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.meal_plan_slot_sequence', 53826, true);


--
-- Name: modifier_mapping_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.modifier_mapping_sequence', 1000, false);


--
-- Name: proposal_approach_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.proposal_approach_sequence', 1003, true);


--
-- Name: proposal_context_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.proposal_context_sequence', 50007, true);


--
-- Name: proposal_context_slot_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.proposal_context_slot_sequence', 50048, true);


--
-- Name: proposal_dish_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.proposal_dish_sequence', 1039, true);


--
-- Name: proposal_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.proposal_sequence', 1003, true);


--
-- Name: proposal_slot_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.proposal_slot_sequence', 1006, true);


--
-- Name: shadow_tags_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.shadow_tags_sequence', 52564, true);


--
-- Name: tag_relation_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.tag_relation_sequence', 51721, true);


--
-- Name: tag_search_group_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.tag_search_group_sequence', 278, true);


--
-- Name: tag_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.tag_sequence', 51872, true);


--
-- Name: target_proposal_dish_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.target_proposal_dish_sequence', 52099, true);


--
-- Name: target_proposal_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.target_proposal_sequence', 50003, true);


--
-- Name: target_proposal_slot_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.target_proposal_slot_sequence', 50017, true);


--
-- Name: target_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.target_sequence', 50005, true);


--
-- Name: target_slot_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.target_slot_sequence', 1022, true);


--
-- Name: token_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.token_sequence', 57011, true);


--
-- Name: unit_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.unit_sequence', 1000, false);


--
-- Name: user_device_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.user_device_sequence', 917, true);


--
-- Name: user_id_sequence; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.user_id_sequence', 69, true);


--
-- Name: user_properties_id_seq; Type: SEQUENCE SET; Schema: public; Owner: bank
--

SELECT pg_catalog.setval('public.user_properties_id_seq', 10000, false);


--
-- Name: authority authority_pkey; Type: CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.authority
    ADD CONSTRAINT authority_pkey PRIMARY KEY (authority_id);


--
-- Name: auto_tag_instructions auto_tag_instructions_pkey; Type: CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.auto_tag_instructions
    ADD CONSTRAINT auto_tag_instructions_pkey PRIMARY KEY (instruction_id);


--
-- Name: campaigns campaign_pkey; Type: CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.campaigns
    ADD CONSTRAINT campaign_pkey PRIMARY KEY (campaign_id);


--
-- Name: dish dish_pkey; Type: CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.dish
    ADD CONSTRAINT dish_pkey PRIMARY KEY (dish_id);



--
-- Name: list_category list_category_pkey; Type: CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.list_category
    ADD CONSTRAINT list_category_pkey PRIMARY KEY (category_id);


--
-- Name: list_item list_item_pkey; Type: CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.list_item
    ADD CONSTRAINT list_item_pkey PRIMARY KEY (item_id);


--
-- Name: list_layout list_layout_pkey; Type: CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.list_layout
    ADD CONSTRAINT list_layout_pkey PRIMARY KEY (layout_id);


--
-- Name: list list_pkey; Type: CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.list
    ADD CONSTRAINT list_pkey PRIMARY KEY (list_id);


--
-- Name: list_tag_stats list_tag_stats_pkey; Type: CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.list_tag_stats
    ADD CONSTRAINT list_tag_stats_pkey PRIMARY KEY (list_tag_stat_id);


--
-- Name: meal_plan meal_plan_pkey; Type: CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.meal_plan
    ADD CONSTRAINT meal_plan_pkey PRIMARY KEY (meal_plan_id);


--
-- Name: meal_plan_slot meal_plan_slot_pkey; Type: CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.meal_plan_slot
    ADD CONSTRAINT meal_plan_slot_pkey PRIMARY KEY (meal_plan_slot_id);


--
-- Name: factors pk_factors; Type: CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.factors
    ADD CONSTRAINT pk_factors PRIMARY KEY (factor_id);


--
-- Name: units pk_units; Type: CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.units
    ADD CONSTRAINT pk_units PRIMARY KEY (unit_id);


--
-- Name: proposal_context proposal_context_pkey; Type: CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.proposal_context
    ADD CONSTRAINT proposal_context_pkey PRIMARY KEY (proposal_context_id);


--
-- Name: proposal proposal_pkey; Type: CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.proposal
    ADD CONSTRAINT proposal_pkey PRIMARY KEY (proposal_id);


--
-- Name: proposal_slot proposal_slot_pkey; Type: CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.proposal_slot
    ADD CONSTRAINT proposal_slot_pkey PRIMARY KEY (slot_id);


--
-- Name: shadow_tags shadow_tags_pkey; Type: CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.shadow_tags
    ADD CONSTRAINT shadow_tags_pkey PRIMARY KEY (shadow_tag_id);


--
-- Name: tag tag_pkey; Type: CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.tag
    ADD CONSTRAINT tag_pkey PRIMARY KEY (tag_id);


--
-- Name: tag_relation tag_relation_pkey; Type: CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.tag_relation
    ADD CONSTRAINT tag_relation_pkey PRIMARY KEY (tag_relation_id);


--
-- Name: target target_pkey; Type: CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.target
    ADD CONSTRAINT target_pkey PRIMARY KEY (target_id);


--
-- Name: target_slot target_slot_pkey; Type: CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.target_slot
    ADD CONSTRAINT target_slot_pkey PRIMARY KEY (target_slot_id);


--
-- Name: tokens tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.tokens
    ADD CONSTRAINT tokens_pkey PRIMARY KEY (token_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- Name: tag_relation fk3vyajpbcb8wl8380yntahtgtf; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.tag_relation
    ADD CONSTRAINT fk3vyajpbcb8wl8380yntahtgtf FOREIGN KEY (parent_tag_id) REFERENCES public.tag(tag_id);


--
-- Name: tag_relation fk6x8vvlp985udfs7g15uuxj42c; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.tag_relation
    ADD CONSTRAINT fk6x8vvlp985udfs7g15uuxj42c FOREIGN KEY (child_tag_id) REFERENCES public.tag(tag_id);


--
-- Name: authority fk_authority__user_id; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.authority
    ADD CONSTRAINT fk_authority__user_id FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE;


--
-- Name: dish_tags fk_dish__dish_tags; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.dish_tags
    ADD CONSTRAINT fk_dish__dish_tags FOREIGN KEY (dish_id) REFERENCES public.dish(dish_id) ON DELETE CASCADE;


--
-- Name: dish fk_dish__user_id; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.dish
    ADD CONSTRAINT fk_dish__user_id FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE;


--
-- Name: factors fk_factors_on_from_unit; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.factors
    ADD CONSTRAINT fk_factors_on_from_unit FOREIGN KEY (from_unit) REFERENCES public.units(unit_id);


--
-- Name: factors fk_factors_on_to_unit; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.factors
    ADD CONSTRAINT fk_factors_on_to_unit FOREIGN KEY (to_unit) REFERENCES public.units(unit_id);


--
-- Name: list_item fk_list__list_id; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.list_item
    ADD CONSTRAINT fk_list__list_id FOREIGN KEY (list_id) REFERENCES public.list(list_id) ON DELETE CASCADE;


--
-- Name: list fk_list__user_id; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.list
    ADD CONSTRAINT fk_list__user_id FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE;


--
-- Name: meal_plan_slot fk_meal_plan__meal_plan_slot; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.meal_plan_slot
    ADD CONSTRAINT fk_meal_plan__meal_plan_slot FOREIGN KEY (meal_plan_id) REFERENCES public.meal_plan(meal_plan_id) ON DELETE CASCADE;


--
-- Name: meal_plan fk_meal_plan__user_id; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.meal_plan
    ADD CONSTRAINT fk_meal_plan__user_id FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE;


--
-- Name: proposal fk_proposal__user_id; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.proposal
    ADD CONSTRAINT fk_proposal__user_id FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE;


--
-- Name: proposal_approach fk_proposal_approach__proposal_context; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.proposal_approach
    ADD CONSTRAINT fk_proposal_approach__proposal_context FOREIGN KEY (proposal_context_id) REFERENCES public.proposal_context(proposal_context_id) ON DELETE CASCADE;


--
-- Name: proposal_context fk_proposal_context__proposal; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.proposal_context
    ADD CONSTRAINT fk_proposal_context__proposal FOREIGN KEY (proposal_id) REFERENCES public.proposal(proposal_id) ON DELETE CASCADE;


--
-- Name: proposal_dish fk_proposal_dish__proposal_slot; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.proposal_dish
    ADD CONSTRAINT fk_proposal_dish__proposal_slot FOREIGN KEY (slot_id) REFERENCES public.proposal_slot(slot_id) ON DELETE CASCADE;


--
-- Name: proposal_slot fk_proposal_slot__proposal; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.proposal_slot
    ADD CONSTRAINT fk_proposal_slot__proposal FOREIGN KEY (proposal_id) REFERENCES public.proposal(proposal_id) ON DELETE CASCADE;


--
-- Name: list_tag_stats fk_stats__user_id; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.list_tag_stats
    ADD CONSTRAINT fk_stats__user_id FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE;


--
-- Name: target_slot fk_target__target_slot; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.target_slot
    ADD CONSTRAINT fk_target__target_slot FOREIGN KEY (target_id) REFERENCES public.target(target_id) ON DELETE CASCADE;


--
-- Name: target fk_target__user_id; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.target
    ADD CONSTRAINT fk_target__user_id FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE;


--
-- Name: user_devices fk_user_devices__user_id; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.user_devices
    ADD CONSTRAINT fk_user_devices__user_id FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE;


--
-- Name: category_tags fkclr8vrg8b1cwgwjsgcd5jtj6a; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.category_tags
    ADD CONSTRAINT fkclr8vrg8b1cwgwjsgcd5jtj6a FOREIGN KEY (tag_id) REFERENCES public.tag(tag_id);


--
-- Name: meal_plan_slot fkdit15dhtc9j583c1pp21c8ss0; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.meal_plan_slot
    ADD CONSTRAINT fkdit15dhtc9j583c1pp21c8ss0 FOREIGN KEY (dish_dish_id) REFERENCES public.dish(dish_id);


--
-- Name: list_item fklcvoij9ynqfllhxgn9v6qpsh8; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.list_item
    ADD CONSTRAINT fklcvoij9ynqfllhxgn9v6qpsh8 FOREIGN KEY (tag_id) REFERENCES public.tag(tag_id);


--
-- Name: category_tags fkns9s1sef980caqqamoee8srdw; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.category_tags
    ADD CONSTRAINT fkns9s1sef980caqqamoee8srdw FOREIGN KEY (category_id) REFERENCES public.list_category(category_id);


--
-- Name: dish_tags fkpy8j9ypbt3d59bjs0hgl3wcct; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.dish_tags
    ADD CONSTRAINT fkpy8j9ypbt3d59bjs0hgl3wcct FOREIGN KEY (tag_id) REFERENCES public.tag(tag_id);


--
-- Name: list_category fkrhcs3i2p15y79hn00y5ic41gn; Type: FK CONSTRAINT; Schema: public; Owner: bank
--

ALTER TABLE ONLY public.list_category
    ADD CONSTRAINT fkrhcs3i2p15y79hn00y5ic41gn FOREIGN KEY (layout_id) REFERENCES public.list_layout(layout_id);





