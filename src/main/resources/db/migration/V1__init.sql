--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.6
-- Dumped by pg_dump version 9.5.6

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- Name: copy_dishes(integer, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION copy_dishes(integer, integer) RETURNS integer
    LANGUAGE plpgsql
    AS $_$   DECLARE
      pOrigUser ALIAS for $1;
      pNewUser ALIAS for $2;
      pDish	record;
      nDish int;
   BEGIN
      FOR pDish IN select * from Dish o 
      				left join dish n on o.dish_name = n.dish_name and n.user_id = pNewUser
					where o.user_id = pOrigUser
					and n.dish_id is null LOOP
         insert into dish (dish_id,description, dish_name, user_id, last_added)
				select nextval('hibernate_sequence'),description, dish_name, pNewUser,last_added  
				from dish where user_id = pOrigUser and dish_id = pDish.dish_id 
         returning dish_id into nDish;
         RAISE NOTICE 'dish created(new:%, old:%)',nDish,pDish.dish_id;
         insert into dish_tags (dish_id, tag_id)
			select nDish, tag_id from dish_tags where dish_id = pDish.dish_id;
      END LOOP;
      return 1;
   END;$_$;


--
-- Name: deleteallperishable(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION deleteallperishable() RETURNS integer
    LANGUAGE plpgsql
    AS $$   BEGIN
execute 'delete    from target_proposal_dish';
execute 'delete    from target_proposal_slot';
execute 'delete    from target_proposal';

execute 'delete    from target_tags';
execute 'delete    from target_slots';
execute 'delete    from target_slot';
execute 'delete    from target';

execute 'delete    from meal_plan_slot';
execute 'delete   from meal_plan';

execute 'delete    from proposal_context_slot';
execute 'delete    from proposal_context';

execute 'delete    from list_item';
execute 'delete    from list';

execute 'delete    from auto_tag_instructions';
execute 'delete    from shadow_tags';

   return 1;
   END;
$$;


--
-- Name: movedish(integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION movedish("pFirstId" integer) RETURNS integer
    LANGUAGE plpgsql
    AS $_$   DECLARE
   pFirstId ALIAS for $1;
   pDish record;
      newId	int;
      oldId int;
      i int;
   BEGIN
   oldId = 11;
   newId = 1100;
   i=0;

ALTER TABLE ONLY dish_tags
    DROP CONSTRAINT fkbh371e2vv53a3arqea0hf3jkl;
    
      FOR pDish IN select * from Dish o LOOP
      oldId = pDish.dish_id;
      newId = pFirstId + i;
-- update dish_tags
update dish_tags set dish_id = newId where dish_id = oldId;      

-- and now - update the dish itself
update dish set dish_id = newId where dish_id = oldId;
i=i+1;
END LOOP;
ALTER TABLE ONLY dish_tags
    ADD CONSTRAINT fkbh371e2vv53a3arqea0hf3jkl FOREIGN KEY (dish_id) REFERENCES dish(dish_id);

   return 1;
   END;
$_$;


--
-- Name: movelistcategory(integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION movelistcategory("pFirstId" integer) RETURNS integer
    LANGUAGE plpgsql
    AS $_$   DECLARE
   pFirstId ALIAS for $1;
   pCat record;
      newId	int;
      oldId int;
      i int;
   BEGIN
   oldId = 11;
   newId = 1100;
   i=0;
ALTER TABLE ONLY category_tags
    DROP CONSTRAINT fkns9s1sef980caqqamoee8srdw;
      FOR pCat IN select * from list_category o LOOP
      oldId = pCat.category_id;
      newId = pFirstId + i;
-- update dish_tags
update category_tags set category_id = newId where category_id = oldId;      

-- and now - update the dish itself
update list_category set category_id = newId where category_id = oldId;      
i=i+1;
END LOOP;
ALTER TABLE ONLY category_tags
    ADD CONSTRAINT fkns9s1sef980caqqamoee8srdw FOREIGN KEY (category_id) REFERENCES list_category(category_id);

   return 1;
   END;
$_$;


--
-- Name: movelistlayout(integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION movelistlayout("pFirstId" integer) RETURNS integer
    LANGUAGE plpgsql
    AS $_$   DECLARE
   pFirstId ALIAS for $1;
   pRec record;
      newId	int;
      oldId int;
      i int;
   BEGIN
   oldId = 11;
   newId = 1100;
   i=0;
   
   ALTER TABLE ONLY list_category
    DROP CONSTRAINT fkrhcs3i2p15y79hn00y5ic41gn;
    
      FOR pRec IN select * from list_category o LOOP
      oldId = pRec.layout_id;
      newId = pFirstId + i;
-- update dish_tags
update list_category set layout_id = newId where layout_id = oldId;      

-- and now - update the dish itself
update list_layout set layout_id = newId where layout_id = oldId;      
i=i+1;
END LOOP;
ALTER TABLE ONLY list_category
    ADD CONSTRAINT fkrhcs3i2p15y79hn00y5ic41gn FOREIGN KEY (layout_id) REFERENCES list_layout(layout_id);

   return 1;
   END;
$_$;


--
-- Name: movetag(integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION movetag("pFirstInt" integer DEFAULT 100000) RETURNS integer
    LANGUAGE plpgsql
    AS $_$   DECLARE
   pFirstId ALIAS for $1;
   pTag record;
      newId	int;
      oldId int;
      i int;
   BEGIN
   oldId = 11;
   newId = 1100;
   i=0;
ALTER TABLE ONLY tag_relation
    drop CONSTRAINT fk6x8vvlp985udfs7g15uuxj42c ;
ALTER TABLE ONLY dish_tags
    drop CONSTRAINT fkpy8j9ypbt3d59bjs0hgl3wcct;

ALTER TABLE ONLY category_tags
    drop CONSTRAINT fkclr8vrg8b1cwgwjsgcd5jtj6a;    

ALTER TABLE ONLY tag_relation
    drop CONSTRAINT fk3vyajpbcb8wl8380yntahtgtf;
 FOR pTag IN select * from Tag o LOOP
      oldId = pTag.tag_id;
      newId = pFirstId + i;
-- update dish_tags
update dish_tags set tag_id = newId where tag_id = oldId;      

-- update tag_relation
update tag_relation set child_tag_id = newId where child_tag_id = oldId;
update tag_relation set parent_tag_id = newId where parent_tag_id = oldId;

-- update tag_search_group
update tag_search_group set group_id = newId where group_id = oldId;
update tag_search_group set member_id = newId where member_id = oldId;

-- update category_tags
update category_tags set tag_id = newId where tag_id = oldId;

-- update list_tag_stats
update list_tag_stats set tag_id = newId where tag_id = oldId;

-- and now - update the tag itself
update tag set tag_id = newId where tag_id = oldId;
i=i+1;
END LOOP;
ALTER TABLE ONLY tag_relation
    ADD CONSTRAINT fk6x8vvlp985udfs7g15uuxj42c FOREIGN KEY (child_tag_id) REFERENCES tag(tag_id);
ALTER TABLE ONLY dish_tags
    ADD CONSTRAINT fkpy8j9ypbt3d59bjs0hgl3wcct FOREIGN KEY (tag_id) REFERENCES tag(tag_id);

ALTER TABLE ONLY tag_relation
    ADD CONSTRAINT fk3vyajpbcb8wl8380yntahtgtf FOREIGN KEY (parent_tag_id) REFERENCES tag(tag_id);
ALTER TABLE ONLY category_tags
    ADD CONSTRAINT fkclr8vrg8b1cwgwjsgcd5jtj6a FOREIGN KEY (tag_id) REFERENCES tag(tag_id);
   return 1;
   END;$_$;


--
-- Name: movetagsattelites(integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION movetagsattelites("pFirstId" integer) RETURNS integer
    LANGUAGE plpgsql
    AS $_$
   DECLARE
   pFirstId ALIAS for $1;
   pRec record;
      newId	int;
      oldId int;
      i int;
   BEGIN
   i=0;
      FOR pRec IN select * from tag_relation o LOOP
      oldId = pRec.tag_relation_id;
      newId = pFirstId + i;

-- and now - update the dish itself
update tag_relation set tag_relation_id = newId where tag_relation_id = oldId;      
i=i+1;
END LOOP;


   i=0;
      FOR pRec IN select * from tag_search_group o LOOP
      oldId = pRec.tag_search_group_id;
      newId = pFirstId + i;

-- and now - update the dish itself
update tag_search_group set tag_search_group_id = newId where tag_search_group_id = oldId;      
i=i+1;
END LOOP;


   i=0;
      FOR pRec IN select * from list_tag_stats o LOOP
      oldId = pRec.list_tag_stat_id;
      newId = pFirstId + i;

-- and now - update the dish itself
update list_tag_stats set list_tag_stat_id = newId where list_tag_stat_id = oldId;      
i=i+1;
END LOOP;

   return 1;
   END;

$_$;


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: authority; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE authority (
    authority_id bigint NOT NULL,
    name character varying(50) NOT NULL,
    user_id bigint
);


--
-- Name: authority_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE authority_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: auto_tag_instructions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE auto_tag_instructions (
    instruction_type character varying(31) NOT NULL,
    instruction_id bigint NOT NULL,
    assign_tag_id bigint,
    is_invert boolean,
    search_terms character varying(255),
    invert_filter character varying(255)
);


--
-- Name: auto_tag_instructions_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE auto_tag_instructions_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: category_tags; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE category_tags (
    category_id bigint NOT NULL,
    tag_id bigint NOT NULL
);


--
-- Name: dish; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE dish (
    dish_id bigint NOT NULL,
    description character varying(255),
    dish_name character varying(255),
    user_id bigint,
    last_added timestamp without time zone,
    auto_tag_status integer
);


--
-- Name: dish_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE dish_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dish_tags; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE dish_tags (
    dish_id bigint NOT NULL,
    tag_id bigint NOT NULL
);


--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: list; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE list (
    list_id bigint NOT NULL,
    created_on timestamp without time zone,
    list_type integer,
    user_id bigint,
    list_layout_type character varying(255),
    list_types character varying(255)
);


--
-- Name: list_category; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE list_category (
    category_id bigint NOT NULL,
    name character varying(255),
    layout_id bigint
);


--
-- Name: list_item; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE list_item (
    item_id bigint NOT NULL,
    added_on timestamp without time zone,
    crossed_off timestamp without time zone,
    free_text character varying(255),
    source character varying(255),
    list_id bigint,
    list_category character varying(255),
    tag_id bigint,
    used_count integer
);


--
-- Name: list_item_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE list_item_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: list_layout; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE list_layout (
    layout_id bigint NOT NULL,
    layout_type character varying(255),
    name character varying(255)
);


--
-- Name: list_layout_category_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE list_layout_category_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: list_layout_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE list_layout_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: list_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE list_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: list_tag_stats; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE list_tag_stats (
    list_tag_stat_id bigint NOT NULL,
    added_count integer,
    removed_count integer,
    tag_id bigint,
    user_id bigint
);


--
-- Name: list_tag_stats_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE list_tag_stats_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: meal_plan; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE meal_plan (
    meal_plan_id bigint NOT NULL,
    created timestamp without time zone,
    meal_plan_type character varying(255),
    name character varying(255),
    user_id bigint
);


--
-- Name: meal_plan_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE meal_plan_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: meal_plan_slot; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE meal_plan_slot (
    meal_plan_slot_id bigint NOT NULL,
    dish_dish_id bigint,
    meal_plan_id bigint NOT NULL
);


--
-- Name: meal_plan_slot_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE meal_plan_slot_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: proposal_context; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE proposal_context (
    proposal_context_id bigint NOT NULL,
    approach_type character varying(255),
    dish_count_per_slot integer,
    maximum_empties integer,
    proposal_count integer,
    proposal_id bigint,
    refresh_flag character varying(255),
    current_attempt_index integer
);


--
-- Name: proposal_context_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE proposal_context_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: proposal_context_slot; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE proposal_context_slot (
    proposal_context_slot_id bigint NOT NULL,
    approach_order character varying(255),
    dish_dish_id bigint,
    proposal_context_id bigint NOT NULL,
    sort_key integer
);


--
-- Name: proposal_context_slot_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE proposal_context_slot_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: tag_relation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE tag_relation (
    tag_relation_id bigint NOT NULL,
    child_tag_id bigint,
    parent_tag_id bigint
);


--
-- Name: selectabletags; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW selectabletags AS
 SELECT c.tag_relation_id,
    c.child_tag_id,
    c.parent_tag_id
   FROM (tag_relation c
     LEFT JOIN tag_relation p ON ((c.child_tag_id = p.parent_tag_id)))
  WHERE (p.* IS NULL);


--
-- Name: shadow_tags; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE shadow_tags (
    shadow_tag_id bigint NOT NULL,
    dish_id bigint,
    tag_id bigint
);


--
-- Name: shadow_tags_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE shadow_tags_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: tag; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE tag (
    tag_id bigint NOT NULL,
    description character varying(255),
    name character varying(255),
    tag_type character varying(255),
    rating_family character varying(255),
    tag_type_default boolean,
    auto_tag_flag integer,
    is_parent_tag boolean,
    assign_select boolean,
    search_select boolean,
    is_verified boolean,
    power double precision
);


--
-- Name: tag_relation_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE tag_relation_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: tag_search_group; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE tag_search_group (
    tag_search_group_id bigint NOT NULL,
    group_id bigint,
    member_id bigint
);


--
-- Name: tag_search_group_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE tag_search_group_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: tag_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE tag_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: target; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE target (
    target character varying(31) NOT NULL,
    target_id bigint NOT NULL,
    created timestamp without time zone,
    last_updated timestamp without time zone,
    last_used timestamp without time zone,
    target_name character varying(255),
    target_tag_ids character varying(255),
    user_id bigint,
    proposal_id bigint
);


--
-- Name: target_proposal; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE target_proposal (
    proposal_id bigint NOT NULL,
    created timestamp without time zone,
    current_proposal_index integer,
    for_target_id bigint,
    last_updated timestamp without time zone,
    last_used timestamp without time zone,
    proposal_index_list character varying(255),
    refresh_flag character varying(255),
    regenerate_on_refresh boolean,
    slot_sort_order character varying(255),
    target_name character varying(255),
    target_tag_ids character varying(255),
    user_id bigint,
    can_be_refreshed boolean
);


--
-- Name: target_proposal_dish; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE target_proposal_dish (
    proposal_dish_id bigint NOT NULL,
    dish_id bigint,
    matched_tag_ids character varying(255),
    target_proposal_slot_slot_id bigint
);


--
-- Name: target_proposal_dish_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE target_proposal_dish_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: target_proposal_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE target_proposal_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: target_proposal_slot; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE target_proposal_slot (
    slot_id bigint NOT NULL,
    selected_dish_index integer,
    slot_dish_tag_id bigint,
    slot_order integer,
    target_id bigint,
    target_slot_id bigint,
    target_tag_ids character varying(255),
    target_proposal_proposal_id bigint
);


--
-- Name: target_proposal_slot_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE target_proposal_slot_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: target_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE target_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: target_slot; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE target_slot (
    target character varying(31) NOT NULL,
    target_slot_id bigint NOT NULL,
    slot_dish_tag_id bigint,
    slot_order integer,
    target_id bigint,
    target_tag_ids character varying(255)
);


--
-- Name: target_slots; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE target_slots (
    target_entity_target_id bigint NOT NULL,
    slots_target_slot_id bigint NOT NULL
);


--
-- Name: target_tags; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE target_tags (
    target_entity_target_id bigint NOT NULL,
    tags_target_slot_id bigint NOT NULL
);


--
-- Name: user_id_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE user_id_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE users (
    user_id bigint NOT NULL,
    email character varying(255),
    enabled boolean,
    last_password_reset_date timestamp without time zone,
    password character varying(255),
    username character varying(255)
);


--
-- Data for Name: authority; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO authority (authority_id, name, user_id) VALUES (1, 'ROLE_USER', 1);
INSERT INTO authority (authority_id, name, user_id) VALUES (2, 'ROLE_USER', 20);
INSERT INTO authority (authority_id, name, user_id) VALUES (3, 'ROLE_USER', 23);
INSERT INTO authority (authority_id, name, user_id) VALUES (4, 'ROLE_USER', 26);
INSERT INTO authority (authority_id, name, user_id) VALUES (5, 'ROLE_USER', 29);


--
-- Name: authority_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('authority_seq', 1, false);


--
-- Data for Name: auto_tag_instructions; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Name: auto_tag_instructions_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('auto_tag_instructions_sequence', 1000, false);


--
-- Data for Name: category_tags; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO category_tags (category_id, tag_id) VALUES (11, 357);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 358);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 359);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 360);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 361);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 371);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 374);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 406);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 408);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 418);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 420);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 434);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 435);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 436);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 437);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 438);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 439);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 440);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 441);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 442);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 443);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 469);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 469);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 470);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 470);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 472);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 472);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 473);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 473);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 1);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 1);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 3);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 3);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 4);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 4);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 5);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 5);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 6);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 6);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 10);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 10);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 12);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 12);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 13);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 13);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 15);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 15);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 16);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 16);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 17);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 17);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 18);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 18);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 19);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 19);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 20);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 20);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 21);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 21);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 22);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 22);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 23);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 23);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 25);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 25);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 26);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 26);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 27);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 27);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 28);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 28);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 29);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 29);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 30);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 30);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 31);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 31);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 32);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 32);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 33);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 33);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 34);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 34);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 36);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 36);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 37);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 37);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 38);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 38);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 39);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 39);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 40);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 40);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 41);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 41);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 42);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 42);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 43);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 43);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 44);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 44);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 45);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 45);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 46);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 46);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 47);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 47);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 48);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 48);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 49);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 49);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 50);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 50);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 51);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 51);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 52);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 52);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 53);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 53);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 54);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 54);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 55);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 55);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 56);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 77);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 78);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 78);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 79);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 79);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 80);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 80);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 81);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 81);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 82);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 82);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 83);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 83);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 84);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 84);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 85);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 85);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 89);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 89);
INSERT INTO category_tags (category_id, tag_id) VALUES (10, 90);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 90);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 91);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 91);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 92);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 92);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 93);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 93);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 94);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 94);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 95);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 95);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 96);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 96);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 97);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 97);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 98);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 98);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 99);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 99);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 100);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 100);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 101);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 101);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 102);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 102);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 103);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 103);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 104);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 104);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 105);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 105);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 106);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 106);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 107);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 107);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 108);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 108);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 109);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 109);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 110);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 110);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 111);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 111);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 112);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 112);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 113);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 113);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 114);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 114);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 115);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 115);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 116);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 116);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 117);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 117);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 118);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 118);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 119);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 119);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 120);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 120);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 121);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 121);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 122);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 122);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 123);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 123);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 124);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 124);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 125);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 125);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 126);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 126);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 127);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 127);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 128);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 128);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 129);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 129);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 130);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 130);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 131);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 131);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 132);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 132);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 133);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 133);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 134);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 134);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 135);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 135);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 136);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 136);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 138);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 138);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 139);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 139);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 140);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 140);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 141);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 141);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 144);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 144);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 145);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 145);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 146);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 146);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 147);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 147);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 148);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 148);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 149);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 149);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 150);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 150);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 151);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 151);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 158);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 158);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 159);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 159);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 161);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 161);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 162);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 162);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 163);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 163);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 164);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 164);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 165);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 165);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 166);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 166);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 167);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 167);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 168);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 168);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 169);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 169);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 170);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 170);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 171);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 171);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 172);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 172);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 173);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 173);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 174);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 174);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 175);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 175);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 176);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 176);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 177);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 177);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 178);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 178);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 179);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 179);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 180);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 180);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 181);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 181);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 182);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 182);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 183);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 183);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 184);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 184);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 185);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 185);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 186);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 186);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 187);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 187);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 188);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 188);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 189);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 189);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 190);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 190);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 191);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 191);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 192);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 192);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 193);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 193);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 194);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 194);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 195);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 195);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 196);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 196);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 198);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 198);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 200);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 200);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 201);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 201);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 202);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 202);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 203);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 203);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 205);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 205);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 206);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 206);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 207);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 207);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 208);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 208);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 209);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 209);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 210);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 210);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 211);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 211);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 212);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 212);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 213);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 213);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 214);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 214);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 215);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 215);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 217);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 217);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 220);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 220);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 221);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 221);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 222);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 222);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 223);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 223);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 224);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 224);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 225);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 225);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 226);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 226);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 227);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 227);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 228);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 228);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 229);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 229);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 230);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 230);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 231);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 231);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 232);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 232);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 233);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 233);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 234);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 234);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 235);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 235);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 236);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 236);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 237);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 237);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 238);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 238);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 239);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 239);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 240);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 240);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 241);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 241);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 242);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 242);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 243);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 243);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 244);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 244);
INSERT INTO category_tags (category_id, tag_id) VALUES (10, 245);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 245);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 247);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 247);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 248);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 248);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 249);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 249);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 250);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 250);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 251);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 251);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 252);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 252);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 253);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 253);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 254);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 254);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 255);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 255);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 256);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 256);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 257);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 257);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 258);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 258);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 259);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 259);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 260);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 260);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 261);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 261);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 262);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 262);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 263);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 263);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 264);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 264);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 265);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 265);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 266);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 266);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 269);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 269);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 270);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 270);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 271);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 271);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 272);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 272);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 273);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 273);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 274);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 274);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 275);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 275);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 276);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 276);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 277);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 277);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 278);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 278);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 279);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 279);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 282);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 282);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 283);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 283);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 284);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 284);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 285);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 285);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 286);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 286);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 287);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 287);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 288);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 288);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 289);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 289);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 293);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 293);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 294);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 294);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 295);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 295);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 296);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 296);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 297);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 297);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 298);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 298);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 299);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 299);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 308);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 308);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 309);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 309);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 310);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 310);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 311);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 311);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 312);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 312);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 314);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 314);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 318);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 318);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 334);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 334);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 335);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 335);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 336);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 336);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 337);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 337);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 338);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 338);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 339);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 339);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 340);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 340);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 341);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 341);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 342);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 342);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 343);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 343);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 345);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 345);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 347);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 347);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 348);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 348);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 349);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 349);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 350);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 350);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 351);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 351);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 352);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 352);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 353);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 353);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 354);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 56);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 57);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 57);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 58);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 58);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 59);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 59);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 60);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 60);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 61);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 61);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 62);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 62);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 63);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 63);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 65);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 65);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 66);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 66);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 67);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 67);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 68);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 68);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 69);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 69);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 70);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 70);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 71);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 71);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 72);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 72);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 73);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 73);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 74);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 74);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 75);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 75);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 76);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 76);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 77);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 354);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 355);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 355);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 356);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 356);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 357);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 358);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 359);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 360);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 361);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 406);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 408);
INSERT INTO category_tags (category_id, tag_id) VALUES (9, 418);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 420);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 434);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 435);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 436);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 437);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 438);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 439);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 440);
INSERT INTO category_tags (category_id, tag_id) VALUES (2, 441);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 441);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 442);
INSERT INTO category_tags (category_id, tag_id) VALUES (5, 443);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 444);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 444);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 446);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 446);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 447);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 447);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 448);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 448);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 449);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 449);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 450);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 450);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 451);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 451);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 452);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 452);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 453);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 453);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 454);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 454);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 455);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 455);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 456);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 456);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 457);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 457);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 458);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 458);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 459);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 459);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 460);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 460);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 461);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 461);
INSERT INTO category_tags (category_id, tag_id) VALUES (7, 462);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 462);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 463);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 463);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 464);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 464);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 465);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 465);
INSERT INTO category_tags (category_id, tag_id) VALUES (8, 466);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 466);
INSERT INTO category_tags (category_id, tag_id) VALUES (6, 467);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 467);
INSERT INTO category_tags (category_id, tag_id) VALUES (10, 468);
INSERT INTO category_tags (category_id, tag_id) VALUES (11, 468);


--
-- Data for Name: dish; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (1, NULL, 'Israeli Couscous', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (2, NULL, 'Breakfast Casserole', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (3, NULL, 'dijon-tarragon cream chicken', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (4, NULL, 'Thai Chicken with Basil Stir-Fry', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (5, NULL, 'red beans and rice', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (6, NULL, 'chicken estragon', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (7, NULL, 'quick chicken curry', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (8, NULL, 'spicy pumpkin chili', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (9, NULL, 'ginger beef', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (10, NULL, 'four vegetable soup', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (11, NULL, 'seared pork chops with mushroom gravy', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (12, NULL, 'greek island chicken', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (13, NULL, 'green chili', 20, '2018-01-13 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (14, NULL, 'Pecan-Crusted Pork with Pumpkin Butter', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (15, NULL, 'Chef John''s Salmon Cakes', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (16, NULL, 'Cheeseburger Macaroni', 20, '2018-02-03 08:22:03.125', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (17, NULL, 'pasta with butternut squash, spinach and prosciutto', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (18, NULL, 'cod with leeks, tomatoes, and olives', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (19, NULL, 'lamb and eggplant pasta with goat cheese', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (20, NULL, 'skillet lemon chicken with rice and peas', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (21, NULL, 'steak with blue cheese butter and sour cream potatoes', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (22, NULL, 'pork chops with roasted red pepper cream', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (23, NULL, 'cod with tarragon and potatoes', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (24, NULL, 'ble', 20, '2018-01-28 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (25, NULL, 'Ham and Potato Soup', 20, '2018-02-24 13:33:40.506', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (26, NULL, 'slow cooker black bean soup', 20, '2018-02-08 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (27, NULL, 'tuna casserole I', 20, '2017-12-09 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (28, NULL, 'Porcupine Meatballs', 20, '2018-01-13 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (29, NULL, 'Broiled Salmon with Potato Crust', 20, '2018-01-06 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (30, NULL, 'chocolate crinkles', 20, '2017-12-10 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (31, NULL, 'garbanzo bean salad', 20, '2017-11-18 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (32, NULL, 'christmas bread', 20, '2017-12-23 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (33, NULL, 'homemade eggnog', 20, '2017-12-10 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (34, NULL, 'ginger candied carrots', 20, '2017-12-23 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (35, NULL, 'pecan pie cookies', 20, '2017-12-10 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (36, NULL, 'Frozen Dinner', 20, '2018-02-24 13:33:40.506', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (37, NULL, 'lentils and spicy sausage', 20, '2018-01-28 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (38, NULL, 'tuna caper spaghetti', 20, '2017-12-09 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (39, NULL, 'carbonara', 20, '2018-02-03 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (40, NULL, 'Split Pea and Ham Soup', 20, '2017-12-02 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (41, NULL, 'pizzadillas', 20, '2018-01-13 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (42, NULL, 'Lentil and Bulgur Pilaf', 20, '2018-01-13 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (43, NULL, 'arrugula gnocchi', 20, '2018-02-08 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (44, NULL, 'Red Chili', 20, '2018-02-10 18:56:28.335', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (45, NULL, 'broccoli and carrots', 20, '2018-02-10 18:56:28.335', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (46, NULL, 'curry lentils', 20, '2018-02-10 18:56:28.335', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (47, NULL, 'Chicken Cassolet', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (48, NULL, 'quick pasta primavera', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (49, NULL, 'moroccan chicken soup', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (50, NULL, 'farfalla with sun-dried tomatoes, arugula and goat cheese', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (51, NULL, 'pan-seared chicken with mushrooms and boursin', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (52, NULL, 'crispy dijon chicken breasts', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (53, NULL, 'pasta with lemony chicken and asparagus', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (54, NULL, 'Broccoli Beef', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (55, NULL, 'Chef John''s Beef Goulash', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (56, NULL, 'Hoppin'' John', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (57, NULL, 'snickerdoodles', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (58, NULL, 'hot spiced cider', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (59, NULL, 'buckeyes', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (60, NULL, 'Salmon with Asparagus and Chive Butter Sauce', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (61, NULL, 'Slow Cooker Beef Stew', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (62, NULL, 'Fish Tacos', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (63, NULL, 'Tuna and Pasta Salad', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (64, NULL, 'Chef John''s Chicken Kiev', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (65, NULL, 'Slow Cooker Chicken Chili', 20, '2018-02-03 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (66, NULL, 'Vegetarian Korma', 20, '2018-02-03 08:22:03.125', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (67, NULL, 'Pie Crust', 20, '2017-11-21 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (68, NULL, 'Sweet Potato Casserole', 20, '2017-11-21 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (69, NULL, 'Zucchini Soup', 20, '2018-01-20 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (70, NULL, 'pan-seared cod with herb butter sauce', 20, '2018-01-28 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (71, NULL, 'pecan sandies', 20, '2017-12-10 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (72, NULL, 'Pumpkin Pie filling', 20, '2017-11-21 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (73, NULL, 'fudge', 20, '2017-12-10 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (74, NULL, 'couscous', 20, '2017-12-09 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (75, NULL, 'cranberry sauce', 20, '2017-11-21 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (76, NULL, 'beef pumpkin stew', 20, '2018-01-28 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (77, NULL, 'Beef and Guiness Stew', 20, '2017-11-11 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (78, NULL, 'Tomato Bacon Pasta', 20, '2018-01-20 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (79, NULL, 'gingerbread', 20, '2017-12-10 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (80, NULL, 'moroccan stew', 20, '2017-12-16 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (81, NULL, 'Crock Pot Chicken Jambalaya', 20, '2018-01-20 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (82, NULL, 'Crock Pot Olive Garden Pasta', 20, '2017-11-18 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (83, NULL, 'Kate Salad', 20, '2018-01-06 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (84, NULL, 'Prosciutto-Wrapped Cod', 20, '2017-11-18 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (85, NULL, 'cod with mediterranean salsa', 20, '2017-11-11 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (86, NULL, 'pea and ham pasta', 20, '2018-02-24 13:33:40.506', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (87, NULL, 'mixed veggies', 20, '2017-11-18 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (88, NULL, 'golden chicken rice', 20, '2018-01-06 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (89, NULL, 'Gulliver''s Creamed Corn', 20, '2017-11-21 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (90, NULL, 'scoozi', 20, '2017-12-02 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (91, NULL, 'red pepper risotto', 20, '2018-02-24 13:33:40.506', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (92, NULL, 'Mom''s Stuffing', 20, '2017-11-21 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (93, NULL, 'Turkey Gravy', 20, '2017-11-21 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (94, NULL, 'madame farfalla', 20, '2017-11-11 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (95, NULL, 'French Onion Soup', 20, '2017-12-23 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (96, NULL, 'heuvos rancheros', 20, '2018-01-28 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (97, NULL, 'mashed potatoes', 20, '2018-02-24 13:33:40.506', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (98, NULL, 'quick burgers and pasta', 20, '2018-02-10 18:56:28.335', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (99, NULL, 'Swedish Beef Crockpot', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (100, NULL, 'crispy cucumber salad', 20, '2018-01-28 00:00:00', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (101, NULL, 'crockpot corn chowder', 20, '2018-02-10 18:56:28.335', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (102, NULL, 'quiche lorraine', 20, '2018-02-17 11:22:14.13', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (103, NULL, 'Cream of Roasted Carrot Soup', 20, '2018-02-17 11:22:14.13', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (104, NULL, 'schnitzel with sauce', 20, '2018-02-24 13:33:40.506', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (105, NULL, 'Chicken Flautas', 20, '2018-02-24 13:33:40.506', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (106, NULL, 'Quiche Lorraine', 20, NULL, NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (107, NULL, 'tuna casserole II', 20, '2018-02-17 11:22:14.13', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (108, NULL, 'chocolate chip cookies', 20, '2018-02-17 11:22:14.13', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (109, NULL, 'Stir Fry - Mostly Green with Orange', 20, '2018-02-17 11:22:14.13', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (110, NULL, 'Jack Salad', 20, '2018-02-17 11:22:14.13', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (111, NULL, 'to delete', 20, '2018-02-24 13:21:01.186', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (112, NULL, 'side salad', 20, '2018-02-24 13:33:40.506', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (113, NULL, 'Side of Rice', 20, '2018-02-24 13:33:40.506', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (114, NULL, 'peas, carrots and corn', 20, '2018-02-24 13:33:40.506', NULL);
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status) VALUES (115, NULL, 'Boo-yah', 20, '2018-02-24 13:33:40.506', NULL);


--
-- Name: dish_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('dish_sequence', 1000, false);


--
-- Data for Name: dish_tags; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO dish_tags (dish_id, tag_id) VALUES (109, 13);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (109, 15);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 15);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (109, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (109, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (109, 21);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (114, 25);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 25);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (112, 32);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (110, 33);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (112, 33);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (110, 34);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (112, 34);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (110, 36);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (109, 41);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 59);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (109, 81);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (112, 81);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (114, 81);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 81);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 101);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 110);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (113, 123);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (114, 155);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 165);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 185);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 187);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (111, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (112, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (113, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (114, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (109, 212);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (109, 217);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (114, 245);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 251);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (109, 256);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 301);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 307);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 315);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (109, 318);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (109, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (111, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 321);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 328);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (110, 336);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (108, 337);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (108, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (109, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (110, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (111, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (112, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (113, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (114, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 346);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 346);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (108, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (108, 350);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (108, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (108, 361);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 397);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 400);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 419);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 426);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (110, 432);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (112, 432);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (113, 432);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (114, 432);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (108, 433);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (96, 1);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (74, 2);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (101, 2);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (101, 2);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (103, 2);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (103, 2);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (106, 4);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (105, 11);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (28, 12);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (5, 13);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (27, 13);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (28, 13);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (56, 13);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (88, 13);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (5, 15);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (10, 15);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (25, 15);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (26, 15);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (28, 15);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (40, 15);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (77, 15);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (81, 15);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (82, 15);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (92, 15);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (101, 15);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (101, 15);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (107, 15);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (4, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (5, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (6, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (8, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (10, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (11, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (12, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (13, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (16, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (19, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (20, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (22, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (24, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (25, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (26, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (28, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (37, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (38, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (40, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (42, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (44, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (46, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (47, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (49, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (55, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (56, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (61, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (63, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (65, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (69, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (77, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (78, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (80, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (81, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (82, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (86, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (88, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (91, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (92, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (93, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (95, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (99, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (103, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (105, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (107, 16);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (28, 17);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (2, 18);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (13, 18);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (16, 18);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (44, 18);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (56, 18);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (95, 18);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (96, 18);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (107, 18);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (4, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (6, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (8, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (10, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (12, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (13, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (16, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (18, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (19, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (20, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (22, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (23, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (25, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (26, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (37, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (39, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (40, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (42, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (44, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (47, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (48, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (50, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (53, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (54, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (55, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (61, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (64, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (65, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (77, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (78, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (80, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (84, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (86, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (91, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (103, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (105, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (107, 19);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (16, 20);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (25, 20);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (45, 21);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (54, 21);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (90, 21);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (90, 22);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (6, 23);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (6, 23);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (12, 23);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (17, 23);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (18, 23);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (51, 23);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (60, 23);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (70, 23);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (86, 23);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (90, 23);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (91, 23);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (93, 23);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (103, 23);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (9, 24);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (17, 24);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (38, 24);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (39, 24);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (43, 24);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (48, 24);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (53, 24);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (78, 24);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (82, 24);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (86, 24);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (89, 25);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (101, 25);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (101, 25);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (5, 26);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (17, 27);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (84, 27);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (56, 28);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (5, 28);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (65, 29);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (82, 29);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (5, 29);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (19, 30);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (94, 31);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (94, 32);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (94, 34);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (105, 36);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (1, 37);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (29, 38);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (60, 38);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (18, 46);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (23, 46);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (62, 46);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (70, 46);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (84, 46);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (85, 46);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (15, 47);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (25, 47);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (55, 47);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (62, 47);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (64, 47);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (79, 47);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (81, 47);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (89, 47);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (21, 48);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (21, 48);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (25, 48);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (91, 49);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (6, 49);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (6, 49);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (80, 50);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (91, 50);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (6, 50);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (6, 50);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (6, 51);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (6, 51);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (23, 52);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (6, 52);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (6, 52);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (10, 52);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (6, 53);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (6, 53);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (34, 54);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (55, 54);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (10, 55);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (51, 55);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (70, 55);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (85, 55);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (1, 55);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (10, 56);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (10, 57);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (18, 57);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (40, 57);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (42, 57);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (55, 57);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (103, 57);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (10, 58);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (17, 58);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (40, 58);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (70, 58);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (77, 58);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (93, 58);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (10, 59);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (21, 59);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (21, 59);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (23, 59);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (10, 60);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (55, 61);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (76, 61);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (107, 61);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (9, 61);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (14, 62);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (15, 62);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (23, 62);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (27, 62);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (52, 62);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (64, 62);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (104, 62);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (107, 62);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (31, 64);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (42, 64);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 64);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (105, 64);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (106, 64);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (5, 64);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (27, 67);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (32, 67);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (33, 67);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (57, 67);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (72, 67);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (73, 67);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (89, 67);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (101, 67);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (101, 67);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (107, 67);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (2, 69);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (15, 69);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (30, 69);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (32, 69);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (33, 69);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (57, 69);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (62, 69);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (64, 69);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (68, 69);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (72, 69);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (79, 69);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (92, 69);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (96, 69);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (102, 69);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (104, 69);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (106, 69);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (27, 71);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (102, 71);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (106, 71);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (102, 72);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (106, 73);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (40, 74);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (77, 74);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (78, 74);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (102, 74);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (100, 75);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (94, 76);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (100, 76);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (100, 77);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (37, 78);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (57, 79);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (79, 79);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (108, 79);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (108, 80);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (5, 81);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (10, 81);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (25, 81);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (26, 81);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (34, 81);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (40, 81);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (45, 81);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (61, 81);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (63, 81);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 81);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (76, 81);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (77, 81);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (82, 81);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (87, 81);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (88, 81);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (103, 81);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (26, 82);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (32, 84);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (105, 85);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (106, 85);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (37, 89);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (96, 89);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (98, 90);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (98, 91);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (98, 92);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (15, 95);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (75, 97);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (18, 99);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (85, 99);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (85, 100);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (4, 101);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (19, 101);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (22, 101);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (41, 101);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (85, 101);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (86, 101);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (15, 102);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (38, 102);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (62, 102);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (84, 102);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (85, 102);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (85, 103);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (1, 104);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (20, 104);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (23, 104);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (27, 104);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (42, 104);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (48, 104);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (53, 104);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (70, 104);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (85, 104);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (82, 105);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (94, 105);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (94, 106);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (94, 107);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (48, 108);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (48, 109);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (53, 109);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (60, 109);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (48, 110);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (78, 110);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (17, 111);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (48, 111);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (49, 112);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (49, 113);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (80, 113);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (83, 113);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (1, 114);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (49, 114);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (74, 114);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (49, 115);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (17, 116);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (17, 117);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (53, 117);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (18, 118);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (18, 119);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (19, 120);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (19, 121);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (19, 122);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (50, 122);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (20, 123);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (54, 123);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (21, 124);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (21, 124);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (21, 125);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (21, 125);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (3, 126);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (22, 126);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 126);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (72, 126);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (22, 127);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (22, 128);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (23, 129);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (52, 129);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (62, 129);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (50, 130);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (50, 131);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (53, 131);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (86, 131);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (91, 131);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (50, 132);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (50, 133);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (55, 133);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (50, 134);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (78, 134);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (1, 135);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (50, 135);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (67, 142);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (68, 142);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (72, 142);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (75, 142);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (89, 142);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (92, 142);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (93, 142);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (24, 155);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (31, 155);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (37, 155);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (39, 155);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (39, 155);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (39, 155);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (39, 155);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (39, 155);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (69, 155);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (107, 155);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (93, 159);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (86, 161);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (86, 162);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (86, 163);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (82, 164);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (54, 165);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (61, 165);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (82, 165);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (95, 165);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (38, 166);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (41, 166);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (82, 166);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (61, 167);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (68, 167);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (80, 167);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (27, 168);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (44, 168);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (55, 168);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (65, 168);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (65, 169);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (89, 170);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (89, 171);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (92, 172);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (92, 173);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (79, 174);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (73, 175);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (73, 176);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (73, 177);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (35, 178);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (35, 179);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (54, 181);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (55, 181);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (57, 181);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (67, 181);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (68, 181);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (71, 181);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (73, 181);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (75, 181);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (77, 181);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (79, 181);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (89, 181);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (95, 181);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (100, 181);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (108, 181);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (1, 181);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (14, 181);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (30, 181);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (32, 181);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (33, 181);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (35, 181);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (57, 182);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (68, 182);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (71, 182);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (73, 182);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (108, 182);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (30, 182);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (33, 182);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (35, 182);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (26, 183);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (65, 184);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (8, 184);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (13, 184);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (26, 184);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (44, 184);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (62, 185);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (81, 185);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (82, 185);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (26, 185);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (80, 186);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (103, 186);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (26, 186);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (43, 186);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 187);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (81, 187);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (87, 187);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (8, 187);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (44, 187);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (82, 188);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (8, 188);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (44, 188);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (65, 189);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (8, 189);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 190);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (76, 190);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (8, 190);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (72, 191);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (8, 191);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (61, 192);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (68, 192);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (72, 192);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (102, 192);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (106, 192);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (8, 192);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (33, 192);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (43, 192);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (53, 193);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (72, 193);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (8, 193);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (14, 193);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (72, 194);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (79, 194);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (8, 194);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (14, 194);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (34, 194);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (105, 198);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (54, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (57, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (58, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (60, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (62, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (63, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (74, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (80, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (83, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (95, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (99, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (1, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (9, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (14, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (15, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (27, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (31, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (33, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (38, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (41, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (43, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (45, 199);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (43, 200);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (43, 201);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (43, 202);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (52, 203);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (65, 203);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (99, 203);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (100, 203);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (21, 203);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (21, 203);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (43, 203);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (60, 204);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (62, 204);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (70, 204);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (84, 204);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (85, 204);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (18, 204);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (23, 204);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (29, 204);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (29, 205);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (29, 206);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (92, 206);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (2, 206);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (29, 207);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (20, 208);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (29, 208);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (70, 208);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (31, 209);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (27, 210);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (31, 210);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (38, 210);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (83, 210);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (107, 210);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (31, 211);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (83, 211);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (100, 211);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (4, 212);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (9, 212);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (54, 212);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 212);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (75, 212);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (76, 212);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (80, 212);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (75, 213);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (16, 218);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 219);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (104, 219);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (25, 227);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 227);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (97, 227);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (101, 227);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (101, 227);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (25, 228);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (14, 229);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (35, 229);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (68, 229);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (71, 229);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (81, 230);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (81, 231);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (81, 232);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (40, 233);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (40, 234);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (9, 237);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (59, 237);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (13, 238);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (41, 238);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (62, 238);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (96, 238);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (79, 242);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (80, 242);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (88, 242);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (14, 243);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (57, 243);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (80, 243);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (24, 244);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (20, 245);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (48, 245);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 245);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (86, 245);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (107, 245);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (1, 245);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (10, 245);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (42, 247);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (51, 247);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (64, 247);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (70, 247);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (76, 247);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (82, 247);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (91, 247);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (92, 247);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (93, 247);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (51, 248);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (52, 249);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (60, 249);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (52, 250);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (9, 251);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (61, 251);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (76, 251);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (77, 251);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (99, 251);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (77, 252);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (13, 255);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (62, 255);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (80, 255);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (4, 256);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (9, 256);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (54, 256);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (54, 257);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (55, 258);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (55, 259);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (56, 260);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (1, 261);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (1, 262);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (1, 263);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (88, 263);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (104, 264);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (2, 267);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (30, 267);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (32, 267);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (33, 267);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (59, 267);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (71, 267);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (68, 268);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (75, 268);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (89, 268);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (92, 268);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (42, 271);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (58, 271);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (58, 272);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (79, 272);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (58, 273);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (30, 274);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (95, 278);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (95, 279);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (95, 282);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (13, 284);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (62, 284);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 284);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 285);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (46, 286);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 286);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (88, 286);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (42, 287);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 287);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (3, 288);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (76, 289);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (30, 293);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (62, 293);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (30, 294);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (59, 294);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (71, 294);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (59, 295);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (79, 296);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (79, 297);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (10, 301);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (25, 301);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (61, 301);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 301);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (69, 301);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (76, 301);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (80, 301);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (82, 301);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (95, 301);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (95, 301);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (17, 303);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (43, 303);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (50, 303);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (53, 303);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (86, 303);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (76, 308);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (76, 309);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (14, 310);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (42, 310);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (76, 310);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (13, 311);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (13, 312);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (15, 315);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (36, 315);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (77, 315);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (85, 315);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (39, 316);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (44, 317);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (101, 317);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (101, 317);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (2, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (3, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (4, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (5, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (6, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (7, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (8, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (9, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (10, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (11, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (12, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (13, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (14, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (15, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (16, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (17, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (18, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (19, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (20, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (21, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (21, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (22, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (23, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (25, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (26, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (27, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (28, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (29, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (36, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (37, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (38, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (39, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (40, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (41, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (42, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (43, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (44, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (46, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (47, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (48, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (49, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (50, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (50, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (51, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (52, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (53, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (54, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (55, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (56, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (60, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (61, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (62, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (63, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (64, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (65, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (69, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (70, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (76, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (77, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (78, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (80, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (81, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (82, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (84, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (85, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (86, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (88, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (90, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (91, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (94, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (95, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (96, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (98, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (99, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (101, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (101, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (103, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (104, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (105, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (106, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (107, 320);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (39, 321);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 321);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (103, 321);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (106, 321);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (65, 322);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (105, 322);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (5, 323);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (26, 323);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (47, 323);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (61, 323);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (65, 323);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (76, 323);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (77, 323);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (80, 323);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (81, 323);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (82, 323);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (99, 323);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (101, 323);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (101, 323);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (16, 324);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (76, 324);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (104, 324);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (77, 325);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (16, 327);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (39, 327);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (104, 327);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (103, 328);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (106, 328);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (39, 330);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (1, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (12, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (15, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (28, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (42, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (43, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (51, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (52, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (53, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (55, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (56, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (60, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (61, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (63, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (64, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (65, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (68, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (70, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (76, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (77, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (80, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (82, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (86, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (91, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (92, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (97, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (99, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (100, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (102, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (103, 334);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (1, 335);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (4, 335);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (8, 335);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (9, 335);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (38, 335);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (39, 335);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (56, 335);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (1, 336);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (3, 336);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (4, 336);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (9, 336);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (12, 336);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (15, 336);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (18, 336);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (19, 336);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (23, 336);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (29, 336);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (31, 336);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (38, 336);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (39, 336);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (42, 336);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (47, 336);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (48, 336);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (52, 336);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (55, 336);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (61, 336);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (63, 336);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (74, 336);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (83, 336);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (85, 336);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (91, 336);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (28, 337);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (39, 337);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (8, 338);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (47, 338);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (65, 338);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (2, 340);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (5, 340);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (46, 340);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (47, 340);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (47, 341);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (55, 341);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (77, 341);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (86, 341);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (42, 342);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (49, 342);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (69, 342);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (87, 342);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (8, 343);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (26, 343);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (65, 343);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (69, 343);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (1, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (2, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (3, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (4, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (6, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (7, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (8, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (9, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (10, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (11, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (12, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (13, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (14, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (16, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (17, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (18, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (19, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (20, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (21, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (22, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (23, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (24, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (25, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (26, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (27, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (28, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (30, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (32, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (33, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (34, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (35, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (37, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (38, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (39, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (40, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (41, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (43, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (44, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (45, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (47, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (48, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (49, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (50, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (51, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (52, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (53, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (54, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (55, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (56, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (57, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (58, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (61, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (62, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (63, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (64, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (65, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (67, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (68, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (69, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (70, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (71, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (72, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (73, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (75, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (76, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (78, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (79, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (80, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (81, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (82, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (83, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (86, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (87, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (88, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (89, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (90, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (91, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (92, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (93, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (94, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (95, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (96, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (97, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (98, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (99, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (100, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (102, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (104, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (107, 344);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (69, 345);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (2, 346);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (3, 346);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (4, 346);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (8, 346);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (9, 346);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (11, 346);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (13, 346);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (42, 346);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (44, 346);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (55, 346);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (61, 346);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (64, 346);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (76, 346);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (78, 346);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (88, 346);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (91, 346);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (96, 346);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (99, 346);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (104, 346);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (105, 346);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (11, 347);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (13, 347);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (14, 347);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (20, 347);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (21, 347);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (21, 347);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (22, 347);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (30, 347);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (49, 347);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (55, 347);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (62, 347);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (64, 347);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (65, 347);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 347);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (70, 347);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (76, 347);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (84, 347);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (88, 347);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (93, 347);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (103, 347);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (105, 347);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (3, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (6, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (6, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (11, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (15, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (17, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (20, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (21, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (21, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (25, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (27, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (32, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (34, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (35, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (40, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (40, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (51, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (53, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (57, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (59, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (60, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (61, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (64, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (67, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (68, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (70, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (71, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (73, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (76, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (79, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (84, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (89, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (92, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (93, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (95, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (97, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (101, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (101, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (104, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (107, 348);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (11, 349);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (51, 349);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (107, 349);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (11, 350);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (13, 350);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (25, 350);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (27, 350);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (30, 350);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (32, 350);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (35, 350);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (54, 350);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (57, 350);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (61, 350);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (62, 350);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (64, 350);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (67, 350);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (68, 350);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (70, 350);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (71, 350);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (76, 350);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (79, 350);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (89, 350);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (93, 350);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (99, 350);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (104, 350);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (107, 350);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (11, 351);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (47, 351);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (55, 351);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (1, 352);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (3, 352);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (11, 352);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (23, 352);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (29, 352);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (52, 352);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (8, 353);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (13, 353);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (18, 353);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (19, 353);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (44, 353);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (47, 353);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (49, 353);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (65, 353);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (80, 353);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (82, 353);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (86, 353);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (6, 354);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (6, 354);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (10, 354);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (12, 354);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (47, 354);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (53, 354);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (63, 354);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (92, 354);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (8, 355);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (12, 355);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 355);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (80, 355);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (91, 355);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (12, 356);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (38, 356);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (94, 356);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (12, 357);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (15, 357);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (68, 357);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (84, 357);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (8, 358);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (12, 358);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (72, 358);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (75, 358);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (79, 358);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (12, 359);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (79, 359);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (1, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (8, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (12, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (15, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (30, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (32, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (42, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (51, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (52, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (53, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (55, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (56, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (57, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (60, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (61, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (62, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (63, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (64, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (67, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (68, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (70, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (71, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (72, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (75, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (76, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (77, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (80, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (88, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (89, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (91, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (92, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (97, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (99, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (100, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (102, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (103, 360);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (58, 361);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (68, 361);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (72, 361);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (76, 361);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (80, 361);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (44, 363);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (65, 363);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (105, 363);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 364);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (42, 368);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (64, 368);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (99, 368);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (104, 395);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (39, 396);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 396);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (103, 396);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (105, 396);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (106, 396);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (16, 397);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (44, 397);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (65, 397);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (101, 397);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (16, 399);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (65, 399);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 400);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (104, 400);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (103, 401);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (105, 401);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (106, 401);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (3, 406);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (4, 406);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (6, 406);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (6, 406);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (12, 406);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (20, 406);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (51, 406);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (52, 406);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (53, 406);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (64, 406);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (65, 406);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (81, 406);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (88, 406);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (104, 406);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (105, 406);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (29, 415);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (59, 415);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (60, 415);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (84, 415);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (65, 416);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (39, 419);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (65, 419);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 419);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (101, 419);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (103, 419);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (104, 419);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (106, 419);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (16, 422);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (105, 422);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (44, 425);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (16, 426);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (106, 426);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (66, 427);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (103, 427);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (104, 427);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (105, 427);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (65, 428);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (101, 428);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (1, 432);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (24, 432);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (31, 432);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (34, 432);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (45, 432);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (56, 432);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (63, 432);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (68, 432);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (74, 432);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (75, 432);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (83, 432);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (89, 432);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (91, 432);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (92, 432);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (93, 432);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (97, 432);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (100, 432);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (30, 433);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (32, 433);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (33, 433);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (35, 433);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (57, 433);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (58, 433);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (59, 433);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (67, 433);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (71, 433);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (72, 433);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (73, 433);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (79, 433);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (3, 434);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (4, 434);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (13, 434);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (90, 434);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (91, 434);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (8, 435);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (16, 435);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (28, 435);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (44, 435);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (82, 435);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (16, 436);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (6, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (6, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (10, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (13, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (22, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (24, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (41, 453);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (9, 454);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (42, 455);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (42, 456);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (46, 456);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (61, 457);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (14, 458);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (15, 459);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (62, 460);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (109, 460);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (62, 461);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (62, 462);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (62, 463);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (62, 464);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (99, 464);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (62, 465);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 465);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (63, 466);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (63, 467);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (36, 468);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (41, 472);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (14, 473);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (11, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (16, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (17, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (20, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (25, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (37, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (42, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (46, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (49, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (55, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (65, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (69, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (77, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (81, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (88, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (91, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (93, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (99, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (101, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (101, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (103, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (109, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 437);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (48, 438);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (90, 438);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (38, 439);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (39, 439);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (39, 440);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (47, 441);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (11, 442);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (22, 442);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (47, 443);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (109, 443);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (115, 443);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (21, 444);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (21, 444);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (68, 444);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (97, 444);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (103, 444);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (88, 446);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (4, 447);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (4, 448);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (4, 449);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (4, 450);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (4, 451);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (4, 452);
INSERT INTO dish_tags (dish_id, tag_id) VALUES (9, 452);


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('hibernate_sequence', 1, false);


--
-- Data for Name: list; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: list_category; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO list_category (category_id, name, layout_id) VALUES (1, 'Produce', 1);
INSERT INTO list_category (category_id, name, layout_id) VALUES (2, 'Meat', 1);
INSERT INTO list_category (category_id, name, layout_id) VALUES (3, 'Dairy', 1);
INSERT INTO list_category (category_id, name, layout_id) VALUES (4, 'Other Things', 1);
INSERT INTO list_category (category_id, name, layout_id) VALUES (5, 'Meat', 5);
INSERT INTO list_category (category_id, name, layout_id) VALUES (6, 'Produce', 5);
INSERT INTO list_category (category_id, name, layout_id) VALUES (7, 'Dairy', 5);
INSERT INTO list_category (category_id, name, layout_id) VALUES (8, 'Dry', 5);
INSERT INTO list_category (category_id, name, layout_id) VALUES (9, 'Other', 5);
INSERT INTO list_category (category_id, name, layout_id) VALUES (10, 'Frozen', 5);
INSERT INTO list_category (category_id, name, layout_id) VALUES (11, 'All', 11);


--
-- Data for Name: list_item; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Name: list_item_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('list_item_sequence', 1000, false);


--
-- Data for Name: list_layout; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO list_layout (layout_id, layout_type, name) VALUES (1, 'FineGrained', 'FineGrained');
INSERT INTO list_layout (layout_id, layout_type, name) VALUES (5, 'RoughGrained', 'RoughGrained');
INSERT INTO list_layout (layout_id, layout_type, name) VALUES (11, 'All', 'All');


--
-- Name: list_layout_category_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('list_layout_category_sequence', 1000, false);


--
-- Name: list_layout_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('list_layout_sequence', 1000, false);


--
-- Name: list_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('list_sequence', 1000, false);


--
-- Data for Name: list_tag_stats; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (1, 1, 1, 1, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (2, 3, 3, 12, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (3, 42, 31, 13, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (4, 47, 34, 15, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (5, 64, 47, 16, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (6, 3, 3, 17, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (7, 8, 0, 18, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (8, 48, 32, 19, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (9, 6, 2, 20, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (10, 37, 28, 20, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (11, 11, 0, 21, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (12, 3, 3, 21, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (13, 2, 0, 22, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (14, 3, 3, 22, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (15, 46, 31, 23, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (16, 42, 30, 25, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (17, 16, 6, 25, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (18, 39, 28, 26, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (19, 1, 0, 27, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (20, 1, 0, 27, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (21, 1, 0, 28, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (22, 39, 28, 28, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (23, 2, 2, 29, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (24, 41, 27, 29, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (25, 1, 0, 31, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (26, 1, 0, 31, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (27, 10, 0, 32, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (28, 42, 29, 32, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (29, 26, 2, 65, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (30, 56, 46, 66, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (31, 63, 46, 67, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (32, 63, 45, 68, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (33, 2, 0, 76, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (34, 40, 28, 76, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (35, 39, 27, 77, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (36, 1, 1, 77, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (37, 4, 2, 78, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (38, 39, 28, 78, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (39, 4, 2, 79, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (40, 2, 1, 79, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (41, 2, 0, 80, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (42, 2, 1, 80, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (43, 64, 50, 81, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (44, 27, 3, 81, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (45, 1, 0, 82, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (46, 2, 1, 82, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (47, 1, 0, 84, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (48, 2, 2, 85, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (49, 4, 0, 89, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (50, 39, 28, 89, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (51, 6, 3, 90, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (52, 39, 28, 90, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (53, 6, 0, 91, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (54, 39, 27, 91, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (55, 6, 0, 92, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (56, 39, 28, 92, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (57, 0, 0, 93, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (58, 0, 0, 94, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (59, 24, 4, 95, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (60, 63, 47, 95, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (61, 26, 8, 96, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (62, 63, 48, 96, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (63, 1, 0, 97, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (64, 63, 46, 97, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (65, 63, 46, 98, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (66, 25, 2, 98, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (67, 1, 0, 99, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (68, 2, 0, 99, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (69, 1, 0, 100, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (70, 5, 0, 101, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (71, 3, 0, 102, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (72, 1, 0, 102, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (73, 1, 0, 103, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (74, 4, 0, 104, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (75, 2, 1, 105, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (76, 2, 0, 105, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (77, 1, 0, 3, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (78, 10, 0, 23, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (79, 10, 0, 33, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (80, 41, 28, 33, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (81, 22, 5, 34, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (82, 63, 47, 34, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (83, 5, 0, 36, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (84, 1, 0, 37, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (85, 2, 0, 38, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (86, 1, 0, 40, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (87, 4, 2, 41, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (88, 2, 0, 42, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (89, 20, 14, 42, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (90, 2, 0, 106, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (91, 1, 0, 106, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (92, 1, 0, 107, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (93, 1, 0, 107, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (94, 3, 1, 110, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (95, 5, 0, 113, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (96, 1, 1, 114, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (97, 2, 0, 118, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (98, 2, 0, 119, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (99, 2, 0, 122, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (100, 5, 2, 123, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (101, 3, 3, 126, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (102, 2, 0, 130, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (103, 2, 0, 131, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (104, 6, 0, 131, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (105, 2, 0, 132, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (106, 2, 0, 133, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (107, 2, 0, 134, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (108, 2, 0, 134, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (109, 2, 0, 135, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (110, 24, 4, 138, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (111, 25, 9, 139, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (112, 22, 10, 140, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (113, 4, 4, 141, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (114, 1, 0, 159, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (115, 3, 0, 161, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (116, 3, 0, 162, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (117, 5, 2, 163, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (118, 1, 0, 164, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (119, 2, 0, 164, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (120, 3, 2, 165, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (121, 2, 0, 165, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (122, 3, 0, 166, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (123, 2, 0, 166, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (124, 5, 1, 167, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (125, 6, 5, 168, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (126, 1, 0, 168, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (127, 2, 1, 169, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (128, 1, 0, 169, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (129, 1, 0, 170, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (130, 1, 1, 171, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (131, 1, 0, 172, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (132, 1, 1, 173, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (133, 2, 1, 174, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (134, 2, 0, 175, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (135, 2, 0, 176, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (136, 2, 1, 177, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (137, 1, 0, 183, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (138, 2, 1, 183, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (139, 4, 1, 184, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (140, 7, 6, 184, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (141, 7, 4, 185, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (142, 5, 1, 185, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (143, 6, 3, 186, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (144, 2, 1, 186, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (145, 2, 0, 187, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (146, 11, 0, 187, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (147, 4, 0, 188, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (148, 3, 0, 188, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (149, 2, 0, 189, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (150, 2, 0, 189, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (151, 2, 0, 190, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (152, 2, 1, 191, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (153, 8, 5, 192, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (154, 5, 1, 192, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (155, 1, 0, 193, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (156, 2, 1, 193, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (157, 1, 0, 194, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (158, 5, 3, 194, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (159, 2, 0, 198, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (160, 1, 0, 200, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (161, 2, 1, 200, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (162, 1, 0, 201, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (163, 2, 1, 201, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (164, 1, 0, 202, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (165, 2, 1, 202, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (166, 4, 1, 203, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (167, 43, 28, 203, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (168, 3, 0, 205, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (169, 2, 0, 206, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (170, 3, 1, 206, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (171, 3, 0, 207, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (172, 3, 0, 208, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (173, 3, 1, 209, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (174, 1, 0, 209, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (175, 7, 0, 210, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (176, 3, 1, 210, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (177, 42, 28, 211, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (178, 6, 1, 211, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (179, 10, 2, 212, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (180, 1, 0, 213, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (181, 23, 11, 214, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (182, 3, 1, 217, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (183, 22, 4, 220, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (184, 63, 46, 220, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (185, 25, 2, 221, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (186, 63, 46, 221, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (187, 20, 10, 222, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (188, 63, 46, 222, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (189, 22, 5, 223, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (190, 63, 49, 223, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (191, 22, 10, 224, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (192, 63, 47, 224, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (193, 23, 13, 225, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (194, 63, 45, 225, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (195, 23, 14, 226, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (196, 63, 45, 226, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (197, 13, 2, 227, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (198, 35, 27, 227, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (199, 35, 27, 228, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (200, 5, 0, 228, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (201, 3, 1, 229, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (202, 4, 0, 230, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (203, 4, 0, 231, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (204, 4, 2, 232, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (205, 2, 0, 233, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (206, 2, 0, 234, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (207, 20, 6, 236, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (208, 2, 0, 237, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (209, 5, 3, 237, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (210, 5, 0, 238, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (211, 8, 7, 242, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (212, 4, 2, 243, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (213, 18, 15, 244, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (214, 5, 4, 244, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (215, 42, 30, 245, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (216, 10, 6, 245, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (217, 2, 0, 247, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (218, 8, 0, 247, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (219, 1, 0, 250, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (220, 3, 2, 251, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (221, 2, 0, 251, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (222, 1, 0, 252, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (223, 1, 0, 252, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (224, 25, 7, 253, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (225, 20, 1, 254, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (226, 63, 46, 254, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (227, 7, 5, 255, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (228, 3, 2, 256, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (229, 2, 0, 263, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (230, 5, 3, 264, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (231, 1, 1, 265, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (232, 21, 3, 269, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (233, 21, 7, 270, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (234, 1, 1, 271, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (235, 2, 1, 272, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (236, 2, 0, 274, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (237, 1, 0, 277, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (238, 1, 0, 278, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (239, 1, 0, 279, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (240, 1, 0, 281, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (241, 1, 0, 282, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (242, 5, 1, 284, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (243, 1, 1, 285, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (244, 4, 4, 286, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (245, 2, 0, 287, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (246, 1, 0, 289, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (247, 2, 1, 293, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (248, 2, 0, 294, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (249, 2, 1, 296, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (250, 2, 1, 297, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (251, 1, 0, 308, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (252, 1, 1, 309, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (253, 2, 0, 310, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (254, 3, 1, 311, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (255, 3, 1, 312, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (256, 3, 2, 318, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (257, 20, 18, 334, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (258, 48, 35, 334, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (259, 3, 1, 335, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (260, 7, 5, 335, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (261, 15, 13, 336, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (262, 9, 4, 336, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (263, 10, 4, 337, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (264, 6, 4, 337, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (265, 2, 0, 338, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (266, 6, 3, 338, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (267, 1, 0, 340, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (268, 43, 30, 340, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (269, 5, 3, 341, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (270, 5, 1, 342, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (271, 6, 4, 343, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (272, 8, 1, 343, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (273, 6, 1, 345, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (274, 3, 1, 345, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (275, 12, 8, 347, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (276, 5, 1, 347, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (277, 20, 7, 348, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (278, 42, 29, 348, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (279, 1, 0, 349, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (280, 2, 1, 349, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (281, 17, 9, 350, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (282, 38, 29, 350, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (283, 6, 4, 351, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (284, 2, 1, 352, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (285, 2, 2, 352, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (286, 7, 3, 353, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (287, 13, 0, 353, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (288, 1, 0, 354, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (289, 45, 32, 354, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (290, 3, 1, 355, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (291, 9, 1, 355, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (292, 2, 0, 356, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (293, 3, 1, 356, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (294, 2, 1, 357, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (295, 3, 1, 357, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (296, 3, 1, 358, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (297, 4, 2, 358, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (298, 2, 1, 359, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (299, 2, 0, 359, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (300, 43, 28, 360, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (301, 23, 21, 360, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (302, 8, 6, 361, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (303, 2, 1, 361, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (304, 1, 0, 368, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (305, 2, 1, 368, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (306, 12, 2, 406, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (307, 42, 29, 406, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (308, 8, 4, 434, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (309, 3, 3, 434, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (310, 7, 0, 435, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (311, 8, 4, 435, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (312, 2, 1, 436, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (313, 1, 1, 436, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (314, 60, 45, 437, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (315, 21, 19, 437, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (316, 2, 0, 438, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (317, 3, 3, 438, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (318, 2, 1, 439, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (319, 7, 2, 439, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (320, 6, 0, 440, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (321, 2, 1, 440, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (322, 4, 3, 441, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (323, 2, 1, 442, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (324, 4, 2, 443, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (325, 4, 3, 443, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (326, 7, 7, 444, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (327, 2, 1, 446, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (328, 1, 0, 453, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (329, 1, 1, 455, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (330, 2, 1, 456, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (331, 3, 2, 460, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (332, 1, 0, 464, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (333, 3, 2, 468, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (334, 6, 0, 470, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (335, 1, 0, 472, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (336, 26, 6, 44, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (337, 63, 48, 44, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (338, 7, 1, 45, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (339, 63, 46, 45, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (340, 3, 1, 46, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (341, 3, 0, 46, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (342, 12, 8, 47, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (343, 35, 27, 47, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (344, 5, 0, 48, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (345, 35, 27, 48, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (346, 4, 1, 49, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (347, 39, 27, 49, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (348, 8, 3, 50, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (349, 39, 27, 50, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (350, 2, 0, 12, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (351, 8, 5, 13, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (352, 19, 4, 15, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (353, 23, 1, 16, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (354, 2, 0, 17, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (355, 2, 1, 18, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (356, 20, 5, 19, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (357, 39, 28, 51, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (358, 39, 27, 52, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (359, 39, 27, 53, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (360, 1, 0, 54, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (361, 2, 0, 55, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (362, 4, 3, 57, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (363, 2, 0, 57, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (364, 1, 0, 58, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (365, 5, 2, 58, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (366, 1, 0, 59, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (367, 2, 0, 61, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (368, 1, 0, 61, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (369, 6, 2, 62, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (370, 63, 48, 65, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (371, 25, 7, 66, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (372, 26, 2, 67, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (373, 25, 2, 68, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (374, 14, 2, 69, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (375, 1, 0, 69, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (376, 1, 0, 70, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (377, 4, 1, 71, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (378, 3, 1, 72, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (379, 1, 0, 73, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (380, 6, 1, 74, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (381, 1, 0, 74, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (382, 6, 2, 75, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (383, 39, 28, 75, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (384, 2, 0, 178, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (385, 2, 1, 179, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (386, 8, 3, 181, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (387, 42, 28, 181, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (388, 5, 3, 182, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (389, 2, 1, 182, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (390, 1, 0, 190, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (391, 1, 0, 191, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (392, 1, 0, 307, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (393, 4, 2, 341, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (394, 1, 0, 465, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (395, 7, 1, 342, 1);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (396, 20, 7, 147, 20);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id) VALUES (397, 19, 2, 151, 20);


--
-- Name: list_tag_stats_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('list_tag_stats_sequence', 1000, false);


--
-- Data for Name: meal_plan; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Name: meal_plan_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('meal_plan_sequence', 1000, false);


--
-- Data for Name: meal_plan_slot; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Name: meal_plan_slot_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('meal_plan_slot_sequence', 1000, false);


--
-- Data for Name: proposal_context; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Name: proposal_context_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('proposal_context_sequence', 1000, false);


--
-- Data for Name: proposal_context_slot; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Name: proposal_context_slot_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('proposal_context_slot_sequence', 1000, false);


--
-- Data for Name: shadow_tags; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Name: shadow_tags_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('shadow_tags_sequence', 1000, false);


--
-- Data for Name: tag; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (1, NULL, 'green chili - preprepared', 'Ingredient', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (2, 'dd', 'cheap 5', 'Rating', NULL, NULL, NULL, NULL, true, false, NULL, 5);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (3, NULL, 'salt and pepper shaker', 'NonEdible', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (4, NULL, 'prepared pie crust', 'Ingredient', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (5, NULL, 'big envelopes', 'NonEdible', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (6, NULL, 'duck', 'Ingredient', NULL, NULL, 5, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (7, NULL, 'Finger Food', 'TagType', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (8, NULL, 'Skillet Dish', 'TagType', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (9, NULL, 'Mutton', 'Ingredient', NULL, NULL, 7, NULL, false, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (10, NULL, 'delete', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (11, NULL, 'Mexican', 'TagType', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (12, NULL, 'applesauce', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (13, NULL, 'rice', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (14, NULL, 'Rice', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (15, NULL, 'celery', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (16, NULL, 'onion', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (17, NULL, 'vegetable soup', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (18, NULL, 'cheddar cheese', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (19, NULL, 'garlic', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (20, NULL, 'cream', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (21, NULL, 'broccoli', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (22, NULL, 'artichoke hearts', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (23, NULL, 'white wine', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (24, NULL, 'Pasta', 'TagType', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (25, NULL, 'canned corn', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (26, NULL, 'dry red beans', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (27, NULL, 'prosciutto', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (28, NULL, 'ham hock', 'Ingredient', NULL, NULL, 6, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (29, NULL, 'tabasco', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (30, NULL, 'ground lamb', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (31, NULL, 'cordon blue', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (32, NULL, 'lettuce', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (33, NULL, 'tomatoes', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (34, NULL, 'cucumber', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (35, NULL, 'Cultural Roots', 'TagType', NULL, NULL, NULL, NULL, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (36, NULL, 'feta cheese', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (37, NULL, 'feta cheese', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (38, NULL, 'salmon', 'Ingredient', NULL, NULL, 4, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (39, NULL, 'delete', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (40, NULL, 'delete', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (41, NULL, 'peanuts', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (42, NULL, 'jelly', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (43, NULL, 'rutabegas', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (44, NULL, 'bread', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (45, NULL, 'oranges', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (46, NULL, 'cod', 'Ingredient', NULL, NULL, 4, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (47, NULL, 'cayenne pepper', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (48, NULL, 'chives', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (49, NULL, 'risotto rice', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (50, NULL, 'saffron', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (51, NULL, 'mascarpone', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (52, NULL, 'tarragon', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (53, NULL, 'snow peas', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (54, NULL, 'caraway seeds', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (55, NULL, 'shallot', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (56, NULL, 'leek', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (57, NULL, 'bay leaf', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (58, NULL, 'fresh thyme', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (59, NULL, 'red potatoes', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (60, NULL, 'spinach', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (61, NULL, 'egg noodles', 'Ingredient', NULL, NULL, 1, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (62, NULL, 'bread crumbs', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (63, NULL, 'sponge', 'NonEdible', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (64, NULL, 'cheap 4', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 4);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (65, NULL, 'apples', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (66, NULL, 'muesli', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (67, NULL, 'milk', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (68, NULL, 'yogurt', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (69, NULL, 'eggs', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (70, NULL, 'creme fraiche', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (71, NULL, 'grated swiss cheese', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (72, NULL, 'pie crust', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (73, NULL, 'lardons', 'Ingredient', NULL, NULL, 6, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (74, NULL, 'lardons', 'Ingredient', NULL, NULL, 8, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (75, NULL, 'cucumbers', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (76, NULL, 'red pepper', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (77, NULL, 'white vinegar', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (78, NULL, 'red lentils', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (79, NULL, 'baking soda', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (80, NULL, 'chocolate chips', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (81, NULL, 'carrots', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (82, NULL, 'dry black beans', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (83, NULL, 'delete', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (84, NULL, 'yeast', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (85, NULL, 'salt and pepper', 'Ingredient', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (86, NULL, 'unused', 'TagType', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (87, NULL, 'Occasions', 'TagType', NULL, NULL, NULL, NULL, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (88, NULL, 'Prepared Meats', 'Ingredient', NULL, NULL, 7, NULL, false, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (89, NULL, 'merguez', 'Ingredient', NULL, NULL, 8, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (90, NULL, 'frozen hamburgers', 'Ingredient', NULL, NULL, 2, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (91, NULL, 'tortellini', 'Ingredient', NULL, NULL, 1, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (92, NULL, 'pesto pasta sauce', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (93, NULL, 'saram wrap', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (94, NULL, 'dry cat food', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (95, NULL, 'crackers', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (96, NULL, 'brown cookies', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (97, NULL, 'pears', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (98, NULL, 'grapefruit', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (99, NULL, 'kalamata olives', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (100, NULL, 'plum tomato', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (101, NULL, 'fresh basil', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (102, NULL, 'capers', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (103, NULL, 'orange zest', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (104, NULL, 'lemon juice', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (105, NULL, 'fusilli', 'Ingredient', NULL, NULL, 1, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (106, NULL, 'quail eggs', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (107, NULL, 'fresh dill', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (108, NULL, 'yellow bell pepper', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (109, NULL, 'asparagus', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (110, NULL, 'canned diced tomatoes', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (111, NULL, 'baby spinach', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (112, NULL, 'garam masala', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (113, NULL, 'canned chickpeas', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (114, NULL, 'couscous', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (115, NULL, 'rotisserie chicken', 'Ingredient', NULL, NULL, 3, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (116, NULL, 'butternut squash', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (117, NULL, 'campanelle', 'Ingredient', NULL, NULL, 1, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (118, NULL, 'leeks', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (119, NULL, 'fresh oregano', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (120, NULL, 'eggplant', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (121, NULL, 'penne', 'Ingredient', NULL, NULL, 1, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (122, NULL, 'goat cheese', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (123, NULL, 'white rice', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (124, NULL, 'strip steaks', 'Ingredient', NULL, NULL, 2, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (125, NULL, 'blue cheese', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (126, NULL, 'heavy cream', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (127, NULL, 'roasted red peppers', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (128, NULL, 'light brown sugar', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (129, NULL, 'mayonnaise', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (130, NULL, 'sun dried tomatoes', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (131, NULL, 'parmesan cheese', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (132, NULL, 'pine nuts', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (133, NULL, 'balsamic vinegar', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (134, NULL, 'farfalle', 'Ingredient', NULL, NULL, 1, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (135, NULL, 'arugula', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (136, NULL, 'salami', 'Ingredient', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (137, NULL, 'granola bars', 'Ingredient', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (138, NULL, 'brioche', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (139, NULL, 'nutella', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (140, NULL, 'compote', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (141, NULL, 'toothpaste', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (142, NULL, 'Good For Picnics', 'TagType', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (143, NULL, 'Low Fat', 'TagType', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (144, NULL, 'window cleaner', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (145, NULL, 'mopping fluid', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (146, NULL, 'soft butter', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (147, NULL, 'english muffins', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (148, NULL, 'coffee filters', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (149, NULL, 'page protectors', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (150, NULL, 'blue pen', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (151, NULL, 'clementines', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (152, NULL, 'Low Carbohydrates', 'TagType', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (153, NULL, 'Low Salt', 'TagType', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (154, NULL, 'Low Calorie', 'TagType', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (155, 'dd', 'unused', 'TagType', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (156, NULL, 'Low Glycemic Index', 'TagType', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (157, NULL, 'Preparation Type', 'TagType', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (158, NULL, 'notused1', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (159, NULL, 'gizzards', 'Ingredient', NULL, NULL, 5, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (160, NULL, 'Quick To Prepare', 'Rating', NULL, NULL, NULL, NULL, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (161, NULL, 'shell pasta', 'Ingredient', NULL, NULL, 1, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (162, NULL, 'sliced ham', 'Ingredient', NULL, NULL, 6, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (163, NULL, 'light cream', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (164, NULL, 'canned white kidney beans', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (165, NULL, 'beef stock', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (166, NULL, 'spaghetti sauce', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (167, NULL, 'sweet potatoes', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (168, NULL, 'paprika', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (169, NULL, 'mustard powder', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (170, NULL, 'whipping cream', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (171, NULL, 'white pepper', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (172, NULL, 'walnuts', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (173, NULL, 'poulty seasoning', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (174, NULL, 'star spice', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (175, NULL, 'marshmallows', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (176, NULL, 'baking chocolate', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (177, NULL, 'powdered cocoa', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (178, NULL, 'confectioners sugar', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (179, NULL, 'corn syrup', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (180, NULL, 'oven cleaner', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (181, NULL, 'white sugar', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (182, NULL, 'vanilla extract', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (183, NULL, 'salsa', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (184, NULL, 'chili powder', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (185, NULL, 'oregano', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (186, NULL, 'vegetable stock', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (187, NULL, 'green bell pepper', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (188, NULL, 'canned kidney beans', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (189, NULL, 'canned black beans', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (190, NULL, 'canned tomato sauce', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (191, NULL, 'pumpkin puree', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (192, NULL, 'nutmeg', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (193, NULL, 'ground cloves', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (194, NULL, 'ground ginger', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (195, NULL, 'sandwich bags', 'NonEdible', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (196, NULL, 'loose tea', 'Ingredient', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (197, NULL, 'Low In ...', 'TagType', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (198, NULL, 'corn tortillas', 'Ingredient', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (199, NULL, 'Vegetarian', 'TagType', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (200, NULL, 'arrugula', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (201, NULL, 'gnocchi', 'Ingredient', NULL, NULL, 1, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (202, NULL, 'gorgonzola', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (203, NULL, 'sour cream', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (204, NULL, 'Fish', 'TagType', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (205, NULL, 'potato chips', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (206, NULL, 'white bread', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (207, NULL, 'dill', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (208, NULL, 'lemon zest', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (209, NULL, 'canned garbanzo beans', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (210, NULL, 'canned tuna', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (211, NULL, 'scallion', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (212, NULL, 'fresh ginger', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (213, NULL, 'cranberries', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (214, NULL, 'kids shampoo', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (215, NULL, 'light bulb', 'NonEdible', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (216, NULL, 'Kitchen Supplies', 'NonEdible', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (217, NULL, 'canned sprouts', 'Ingredient', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (218, NULL, 'Quick To Prepare 3', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 3.10000000000000009);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (219, NULL, 'Quick To Prepare 2', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 2.10000000000000009);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (220, NULL, 'friday snack', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (221, NULL, 'diet coke', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (222, NULL, 'napkins', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (223, NULL, 'cat food', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (224, NULL, 'dog food', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (225, NULL, 'shampoo', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (226, NULL, 'shower soap', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (227, NULL, 'potatoes', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (228, NULL, 'smoked ham', 'Ingredient', NULL, NULL, 6, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (229, NULL, 'pecans', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (230, NULL, 'kielbasa', 'Ingredient', NULL, NULL, 8, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (231, NULL, 'canned whole tomatoes', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (232, NULL, 'cajun seasoning', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (233, NULL, 'ham steak', 'Ingredient', NULL, NULL, 6, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (234, NULL, 'green split peas', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (235, NULL, 'kitty litter', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (236, NULL, 'toilet paper', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (237, NULL, 'peanut butter', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (238, NULL, 'tortillas', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (239, NULL, 'delete', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (240, NULL, 'delete', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (241, NULL, 'delete', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (242, NULL, 'ground coriander', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (243, NULL, 'ground cinnamon', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (244, NULL, 'Farro', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (245, NULL, 'frozen peas', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (246, NULL, 'Special Diet', 'TagType', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (247, NULL, 'fresh parsley', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (248, NULL, 'boursin cheese', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (249, NULL, 'fresh chives', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (250, NULL, 'white wine vinegar', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (251, NULL, 'stew meat', 'Ingredient', NULL, NULL, 2, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (252, NULL, 'guiness beer', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (253, NULL, 'coffee', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (254, NULL, 'gum', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (348, NULL, 'butter', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (349, NULL, 'white mushrooms', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (350, NULL, 'flour', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (351, NULL, 'thyme', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (352, NULL, 'dijon mustard', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (353, NULL, 'diced canned tomatoes', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (354, NULL, 'parsley', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (355, NULL, 'red bell pepper', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (356, NULL, 'black pitted olives', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (357, NULL, 'lemon', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (358, NULL, 'cinnamon', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (359, NULL, 'honey', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (360, NULL, 'salt', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (361, NULL, 'brown sugar', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (362, 'lll', 'delete', 'Ingredient', NULL, NULL, NULL, NULL, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (363, NULL, 'Kids Like It 3', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 3);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (364, NULL, 'Kids Like It 2', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 2);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (365, NULL, 'Kids Like It 1', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 1);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (366, 'ddd', 'delete', 'Ingredient', NULL, NULL, NULL, NULL, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (255, NULL, 'ground cumin', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (256, NULL, 'soy sauce', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (257, NULL, 'round steak', 'Ingredient', NULL, NULL, 2, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (258, NULL, 'beef chuck roast', 'Ingredient', NULL, NULL, 2, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (259, NULL, 'dried marjoram', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (260, NULL, 'black-eyed peas', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (261, NULL, 'red wine vinegar', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (262, NULL, 'fresh mint', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (263, NULL, 'pistachios', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (264, NULL, 'boxed gravy', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (265, NULL, 'trash bags', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (266, NULL, 'delete', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (267, NULL, 'Christmas', 'TagType', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (268, NULL, 'Thanksgiving', 'TagType', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (269, NULL, 'sliced sausage', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (270, NULL, 'dish soap', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (271, NULL, 'allspice', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (272, NULL, 'whole cloves', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (273, NULL, 'cider', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (274, NULL, 'unsweetened chocolate', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (275, NULL, 'bag for Christmas Hats', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (276, NULL, 'envelopes', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (277, NULL, 'romano cheese', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (278, NULL, 'sherry', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (279, NULL, 'gruyere cheese', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (280, NULL, 'radishes', 'Ingredient', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (281, NULL, 'sea salt', 'Ingredient', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (282, NULL, 'baguette', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (283, NULL, 'paper towels', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (284, NULL, 'jalapeno pepper', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (285, NULL, 'unsalted cashews', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (286, NULL, 'curry powder', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (287, NULL, 'fresh cilantro', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (288, NULL, 'fresh tarragon', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (289, NULL, 'turnips', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (290, NULL, 'Quick To Table', 'Rating', NULL, NULL, NULL, NULL, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (291, NULL, 'Ease of Prep', 'Rating', NULL, NULL, NULL, NULL, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (292, NULL, 'Elegance', 'Rating', NULL, NULL, NULL, NULL, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (293, NULL, 'baking powder', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (294, NULL, 'powdered sugar', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (295, NULL, 'chocolate bar', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (296, NULL, 'fennel seeds', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (297, NULL, 'cardamon', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (298, NULL, 'square baking dish', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (299, NULL, 'dry wipes', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (300, NULL, 'Healthy 5', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 5);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (301, NULL, 'Soup', 'TagType', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (302, '', 'Halal', 'TagType', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (303, '', 'Kosher', 'TagType', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (304, NULL, 'unused', 'TagType', NULL, NULL, NULL, NULL, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (305, NULL, 'Healthy', 'Rating', NULL, NULL, NULL, NULL, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (306, NULL, 'Kids Like It', 'Rating', NULL, NULL, NULL, NULL, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (307, NULL, 'frozen green beans', 'Ingredient', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (308, NULL, 'bay leaves', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (309, NULL, 'dried thyme', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (310, NULL, 'fresh pumpkin', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (311, NULL, 'pork roast', 'Ingredient', NULL, NULL, 6, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (312, NULL, 'diced green chilis', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (313, NULL, 'Household Supplies', 'NonEdible', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (314, NULL, 'kuggin', 'NonEdible', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (315, NULL, 'cheap 2', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 2);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (316, NULL, 'Quick To Prepare 5', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 5);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (317, NULL, 'Quick To Prepare 4', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 4);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (318, NULL, 'sesame oil', 'Ingredient', NULL, NULL, NULL, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (319, NULL, 'Elegance 5', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 5);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (320, '', 'Main Dish', 'DishType', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (321, NULL, 'Yummy 4', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 4);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (322, NULL, 'Yummy 3', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 3);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (323, 'rr', 'crockpot', 'TagType', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (324, NULL, 'Yummy 5', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 5);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (325, NULL, 'Yummy 1', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 1);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (326, NULL, 'Yummy 2', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 2);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (327, NULL, 'Kids Like It 5', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 5);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (328, NULL, 'Kids Like It 4', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 4);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (329, NULL, 'Ease of Prep 5', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 5);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (330, NULL, 'Quick To Table 5', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 5);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (331, 'dd', 'Type Tag', 'TagType', NULL, NULL, NULL, NULL, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (332, NULL, 'cheap', 'Rating', NULL, true, NULL, true, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (333, NULL, 'Appetizer', 'DishType', NULL, true, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (334, NULL, 'black pepper', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (335, NULL, 'red pepper flakes', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (336, NULL, 'olive oil', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (337, NULL, 'egg', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (338, NULL, 'canned white beans', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (339, NULL, 'canned tomatoes', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (340, NULL, 'sausage', 'Ingredient', NULL, NULL, 8, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (341, NULL, 'tomato paste', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (342, NULL, 'zucchini', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (343, NULL, 'cumin', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (344, 'rrr', 'cheap 3', 'Rating', NULL, NULL, NULL, NULL, true, false, NULL, 3);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (345, NULL, 'laughing cow cheese', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (346, NULL, 'Meat', 'TagType', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (347, NULL, 'vegetable oil', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (367, NULL, 'Animal Products', 'NonEdible', NULL, NULL, NULL, true, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (368, NULL, 'Chicken', 'Ingredient', NULL, NULL, 3, true, false, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (369, NULL, 'New', 'TagType', NULL, true, NULL, true, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (370, NULL, 'New', 'Ingredient', NULL, true, NULL, true, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (371, 'mmm', 'Meat', 'Ingredient', NULL, NULL, 7, true, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (372, NULL, 'Beef', 'Ingredient', NULL, NULL, 2, true, false, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (373, NULL, 'Fish', 'Ingredient', NULL, NULL, 4, true, false, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (374, NULL, 'Poultry', 'Ingredient', NULL, NULL, 5, true, false, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (375, NULL, 'Pork', 'Ingredient', NULL, NULL, 6, true, false, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (376, NULL, 'pasta', 'Ingredient', NULL, NULL, 1, true, false, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (377, NULL, 'Spices', 'Ingredient', NULL, NULL, NULL, true, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (378, NULL, 'Eggs and Dairy', 'Ingredient', NULL, NULL, NULL, true, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (379, NULL, 'Condiments', 'Ingredient', NULL, NULL, NULL, true, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (380, NULL, 'New', 'NonEdible', NULL, true, NULL, true, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (381, NULL, 'Cheese', 'Ingredient', NULL, NULL, NULL, true, false, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (382, NULL, 'Baking', 'Ingredient', NULL, NULL, NULL, true, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (383, NULL, 'cleaning supplies', 'NonEdible', NULL, NULL, NULL, true, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (384, NULL, 'Office Supplies', 'NonEdible', NULL, NULL, NULL, true, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (385, NULL, 'Oil and Vinegar', 'Ingredient', NULL, NULL, NULL, true, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (386, NULL, 'Canned Vegetables', 'Ingredient', NULL, NULL, NULL, true, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (387, NULL, 'Difficulty', 'Rating', NULL, NULL, NULL, true, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (388, NULL, 'Produce', 'Ingredient', NULL, NULL, NULL, true, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (389, NULL, 'Personal Hygiene', 'NonEdible', NULL, NULL, NULL, true, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (390, NULL, 'Vegetables', 'Ingredient', NULL, NULL, NULL, true, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (391, NULL, 'Taste Factor', 'Rating', NULL, NULL, NULL, true, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (392, NULL, 'Drinks', 'Ingredient', NULL, NULL, NULL, true, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (393, NULL, 'Milk and Cream', 'Ingredient', NULL, NULL, NULL, true, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (394, NULL, 'sausage', 'Ingredient', NULL, NULL, 8, true, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (395, NULL, 'Elegance 4', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 4);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (396, NULL, 'Elegance 3', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 3);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (397, NULL, 'Elegance 2', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 2);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (398, NULL, 'Elegance 1', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 1);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (399, NULL, 'Ease of Prep 4', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 4);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (400, NULL, 'Ease of Prep 3', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 3);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (401, NULL, 'Ease of Prep 2', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 2);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (402, NULL, 'Ease of Prep 1', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 1);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (403, NULL, 'Frozen', 'Ingredient', NULL, NULL, NULL, true, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (404, NULL, 'Holiday', 'TagType', NULL, NULL, NULL, true, false, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (405, NULL, 'Prepared Soup', 'Ingredient', NULL, NULL, NULL, true, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (406, NULL, 'chicken breasts', 'Ingredient', NULL, NULL, 3, NULL, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (407, NULL, 'Dish Type', 'TagType', NULL, NULL, NULL, true, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (408, 'dd', 'notused3', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (409, NULL, 'Bakery - Bread Products', 'Ingredient', NULL, NULL, NULL, true, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (410, NULL, 'Nuts', 'Ingredient', NULL, NULL, NULL, true, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (411, NULL, 'Spreads', 'Ingredient', NULL, NULL, NULL, true, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (412, NULL, 'Tomato and Pasta Sauce', 'Ingredient', NULL, NULL, NULL, true, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (413, NULL, 'Snacks', 'Ingredient', NULL, NULL, NULL, true, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (414, NULL, 'Coffee and Tea', 'Ingredient', NULL, NULL, NULL, true, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (415, NULL, 'cheap 1', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 1);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (416, 'dd', 'Quick to Prepare 1', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 1.10000000000000009);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (417, NULL, 'Healthy 4', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 4);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (418, 'jj', 'notused2', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (419, NULL, 'Healthy 3', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 3);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (420, NULL, 'unused', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (421, NULL, 'lunch meats', 'Ingredient', NULL, NULL, NULL, true, false, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (422, NULL, 'Healthy 2', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 2);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (423, NULL, 'Nuts, Grains and Dry Beans', 'Ingredient', NULL, NULL, NULL, true, true, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (424, NULL, 'Healthy 1', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 1);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (425, NULL, 'Quick To Table 4', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 4);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (426, NULL, 'Quick To Table 3', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 3);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (427, NULL, 'Quick To Table 2', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 2);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (428, NULL, 'Quick To Table 1', 'Rating', NULL, NULL, NULL, NULL, true, true, NULL, 1);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (429, NULL, 'Meat Type', 'TagType', NULL, NULL, NULL, true, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (430, NULL, 'Cereals', 'Ingredient', NULL, NULL, NULL, true, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (431, NULL, 'Canned Fish', 'Ingredient', NULL, NULL, NULL, true, false, false, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (432, 'dd', 'Side Dish', 'DishType', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (433, 'tt', 'Dessert', 'DishType', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (434, NULL, 'chicken', 'Ingredient', NULL, NULL, 3, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (435, NULL, 'ground beef', 'Ingredient', NULL, NULL, 2, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (436, NULL, 'elbow maccaroni', 'Ingredient', NULL, NULL, 1, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (437, NULL, 'chicken stock', 'Ingredient', NULL, NULL, 3, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (438, NULL, 'farfalla', 'Ingredient', NULL, NULL, 1, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (439, NULL, 'spaghetti', 'Ingredient', NULL, NULL, 1, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (440, NULL, 'pancetta', 'Ingredient', NULL, NULL, 6, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (441, NULL, 'bacon', 'Ingredient', NULL, NULL, 6, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (442, NULL, 'pork chops', 'Ingredient', NULL, NULL, 6, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (443, NULL, 'chicken thighs', 'Ingredient', NULL, NULL, 3, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (444, NULL, 'half and half', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (445, NULL, 'unused', 'TagType', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (446, NULL, 'raisin', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (447, NULL, 'jasmine rice', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (448, NULL, 'coconut milk', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (449, NULL, 'rice wine vinegar', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (450, NULL, 'fish sauce', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (451, NULL, 'shitake mushrooms', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (452, NULL, 'green onion', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (453, NULL, 'mozzarella', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (454, NULL, 'canned mixed vegetables', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (455, NULL, 'bulgur', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (456, NULL, 'green lentils', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (457, NULL, 'Worcestershire sauce', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (458, NULL, 'apple juice', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (459, NULL, 'canned salmon', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (460, NULL, 'cornstarch', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (461, NULL, 'beer', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (462, NULL, 'plain yogurt', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (463, NULL, 'lime', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (464, NULL, 'dried dill', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (465, NULL, 'cabbage', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (466, NULL, 'bowtie pasta', 'Ingredient', NULL, NULL, 1, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (467, NULL, 'cherry tomatoes', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (468, NULL, 'dinner in a bag', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (469, NULL, 'glue', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (470, NULL, 'soft cat food', 'NonEdible', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (471, NULL, 'pantry dish', 'TagType', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (472, NULL, 'sliced pepperoni', 'Ingredient', NULL, NULL, NULL, NULL, true, true, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, rating_family, tag_type_default, auto_tag_flag, is_parent_tag, assign_select, search_select, is_verified, power) VALUES (473, NULL, 'boneless pork chops', 'Ingredient', NULL, NULL, 6, NULL, true, true, NULL, NULL);


--
-- Data for Name: tag_relation; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (238, 126, 393);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (239, 163, 393);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (240, 20, 393);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (241, 67, 393);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (242, 70, 393);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (243, 230, 394);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (244, 340, 394);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (245, 74, 394);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (246, 89, 394);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (247, 395, 292);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (248, 396, 292);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (249, 397, 292);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (250, 398, 292);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (251, 399, 291);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (252, 400, 291);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (253, 401, 291);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (254, 402, 291);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (255, 403, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (256, 7, 407);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (257, 263, 410);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (258, 78, 423);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (259, 114, 423);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (260, 234, 423);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (261, 244, 423);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (262, 260, 423);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (263, 199, 429);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (264, 204, 429);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (265, 434, 368);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (266, 435, 372);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (267, 436, 376);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (268, 437, 368);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (269, 438, 376);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (270, 439, 376);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (271, 440, 375);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (272, 441, 375);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (273, 442, 375);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (274, 443, 368);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (275, 444, 378);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (276, 445, 369);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (277, 446, 423);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (278, 447, 14);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (279, 448, 370);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (280, 449, 379);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (281, 450, 379);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (282, 451, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (283, 452, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (284, 453, 381);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (285, 454, 386);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (286, 455, 423);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (287, 456, 423);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (288, 457, 379);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (289, 458, 392);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (290, 459, 431);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (291, 460, 382);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (292, 461, 392);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (293, 462, 378);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (294, 463, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (295, 464, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (296, 465, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (297, 466, 376);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (298, 467, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (299, 468, 403);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (300, 469, 384);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (301, 470, 367);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (302, 471, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (303, 472, 421);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (304, 473, 375);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (1, 30, 9);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (2, 35, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (3, 11, 35);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (4, 39, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (5, 40, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (6, 49, 14);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (7, 87, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (8, 31, 88);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (9, 123, 14);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (10, 157, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (11, 8, 157);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (12, 93, 216);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (13, 148, 216);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (14, 246, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (15, 197, 246);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (16, 236, 313);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (17, 265, 313);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (18, 275, 313);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (19, 283, 313);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (20, 94, 367);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (21, 235, 367);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (22, 115, 368);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (23, 369, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (24, 86, 369);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (25, 370, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (26, 72, 370);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (27, 85, 370);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (28, 136, 370);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (29, 137, 370);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (30, 158, 370);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (31, 196, 370);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (32, 198, 370);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (33, 280, 370);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (34, 281, 370);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (35, 4, 370);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (36, 88, 371);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (37, 9, 371);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (38, 90, 372);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (39, 124, 372);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (40, 251, 372);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (41, 257, 372);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (42, 258, 372);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (43, 38, 373);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (44, 46, 373);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (45, 159, 374);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (46, 28, 375);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (47, 73, 375);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (48, 162, 375);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (49, 233, 375);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (50, 311, 375);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (51, 61, 376);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (52, 91, 376);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (53, 105, 376);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (54, 117, 376);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (55, 121, 376);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (56, 134, 376);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (57, 161, 376);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (58, 201, 376);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (59, 47, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (60, 50, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (61, 54, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (62, 57, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (63, 112, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (64, 168, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (65, 169, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (66, 171, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (67, 173, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (68, 184, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (69, 185, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (70, 192, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (71, 193, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (72, 194, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (73, 232, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (74, 242, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (75, 243, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (76, 255, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (77, 259, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (78, 271, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (79, 272, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (80, 286, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (81, 308, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (82, 309, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (83, 69, 378);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (84, 106, 378);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (85, 146, 378);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (86, 170, 378);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (87, 203, 378);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (88, 29, 379);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (89, 129, 379);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (90, 133, 379);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (91, 183, 379);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (92, 250, 379);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (93, 256, 379);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (94, 261, 379);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (95, 195, 380);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (96, 3, 380);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (97, 5, 380);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (98, 36, 381);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (99, 37, 381);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (100, 51, 381);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (101, 71, 381);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (102, 122, 381);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (103, 125, 381);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (104, 131, 381);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (105, 202, 381);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (106, 248, 381);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (107, 277, 381);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (108, 279, 381);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (109, 128, 382);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (110, 175, 382);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (111, 176, 382);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (112, 177, 382);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (113, 178, 382);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (114, 179, 382);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (115, 181, 382);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (116, 182, 382);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (117, 274, 382);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (118, 293, 382);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (119, 294, 382);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (120, 295, 382);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (121, 350, 382);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (122, 361, 382);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (123, 80, 382);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (124, 84, 382);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (125, 383, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (126, 63, 383);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (127, 144, 383);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (128, 145, 383);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (129, 180, 383);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (130, 270, 383);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (131, 384, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (132, 149, 384);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (133, 150, 384);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (134, 276, 384);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (135, 385, 379);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (136, 77, 385);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (137, 318, 385);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (138, 336, 385);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (139, 347, 385);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (140, 386, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (141, 22, 386);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (142, 25, 386);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (143, 99, 386);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (144, 102, 386);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (145, 110, 386);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (146, 113, 386);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (147, 127, 386);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (148, 130, 386);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (149, 164, 386);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (150, 188, 386);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (151, 189, 386);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (152, 190, 386);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (153, 209, 386);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (154, 210, 386);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (155, 217, 386);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (156, 231, 386);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (157, 312, 386);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (158, 338, 386);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (159, 339, 386);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (160, 341, 386);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (161, 353, 386);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (162, 356, 386);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (163, 100, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (164, 101, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (165, 107, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (166, 108, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (167, 111, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (168, 116, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (169, 118, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (170, 119, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (171, 132, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (172, 151, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (173, 167, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (174, 187, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (175, 200, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (176, 208, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (177, 211, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (178, 213, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (179, 262, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (180, 287, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (181, 288, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (182, 349, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (183, 354, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (184, 357, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (185, 33, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (186, 34, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (187, 43, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (188, 48, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (189, 52, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (190, 58, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (191, 60, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (192, 141, 389);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (193, 214, 389);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (194, 225, 389);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (195, 226, 389);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (196, 390, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (197, 103, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (198, 104, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (199, 109, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (200, 120, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (201, 135, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (202, 191, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (203, 207, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (204, 212, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (205, 247, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (206, 249, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (207, 284, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (208, 289, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (209, 310, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (210, 355, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (211, 15, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (212, 16, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (213, 32, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (214, 45, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (215, 53, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (216, 55, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (217, 56, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (218, 59, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (219, 65, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (220, 75, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (221, 76, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (222, 81, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (223, 97, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (224, 98, 390);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (225, 391, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (226, 321, 391);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (227, 322, 391);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (228, 324, 391);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (229, 325, 391);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (230, 326, 391);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (231, 392, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (232, 221, 392);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (233, 252, 392);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (234, 273, 392);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (235, 278, 392);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (236, 23, 392);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (237, 393, 378);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (305, 331, 369);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (306, 245, 403);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (307, 307, 403);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (308, 267, 404);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (309, 268, 404);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (310, 165, 405);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (311, 186, 405);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (312, 264, 405);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (313, 406, 368);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (314, 24, 407);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (315, 142, 407);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (316, 44, 409);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (317, 62, 409);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (318, 138, 409);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (319, 147, 409);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (320, 206, 409);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (321, 238, 409);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (322, 282, 409);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (323, 41, 410);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (324, 172, 410);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (325, 285, 410);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (326, 42, 411);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (327, 139, 411);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (328, 237, 411);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (329, 92, 412);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (330, 166, 412);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (331, 95, 413);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (332, 96, 413);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (333, 140, 413);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (334, 205, 413);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (335, 253, 414);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (336, 27, 421);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (337, 269, 421);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (338, 14, 423);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (339, 26, 423);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (340, 389, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (341, 10, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (342, 83, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (343, 160, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (344, 216, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (345, 218, 160);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (346, 219, 160);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (347, 290, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (348, 291, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (349, 292, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (350, 298, 216);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (351, 302, 246);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (352, 303, 246);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (353, 305, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (354, 300, 305);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (355, 306, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (356, 313, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (357, 215, 313);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (358, 222, 313);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (359, 254, 313);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (360, 299, 313);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (361, 316, 160);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (362, 317, 160);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (363, 319, 292);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (364, 320, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (365, 323, 157);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (366, 327, 306);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (367, 328, 306);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (368, 329, 291);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (369, 330, 290);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (370, 332, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (371, 2, 332);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (372, 64, 332);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (373, 315, 332);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (374, 333, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (375, 155, 369);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (376, 344, 332);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (377, 363, 306);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (378, 364, 306);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (379, 365, 306);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (380, 367, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (381, 223, 367);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (382, 224, 367);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (383, 377, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (384, 174, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (385, 296, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (386, 297, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (387, 334, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (388, 335, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (389, 343, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (390, 351, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (391, 358, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (392, 360, 377);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (393, 378, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (394, 68, 378);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (395, 337, 378);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (396, 348, 378);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (397, 379, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (398, 352, 379);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (399, 359, 379);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (400, 380, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (401, 314, 380);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (402, 381, 378);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (403, 18, 381);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (404, 345, 381);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (405, 382, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (406, 79, 382);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (407, 371, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (408, 372, 371);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (409, 373, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (410, 374, 371);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (411, 6, 374);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (412, 368, 374);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (413, 375, 371);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (414, 228, 375);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (415, 376, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (416, 387, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (417, 388, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (418, 19, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (419, 21, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (420, 227, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (421, 342, 388);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (422, 304, 369);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (423, 404, 87);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (424, 405, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (425, 1, 405);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (426, 17, 405);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (427, 407, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (428, 301, 407);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (429, 408, 370);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (430, 409, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (431, 229, 410);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (432, 411, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (433, 412, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (434, 413, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (435, 12, 413);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (436, 220, 413);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (437, 414, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (438, 415, 332);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (439, 416, 160);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (440, 417, 305);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (441, 418, 370);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (442, 419, 305);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (443, 420, 370);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (444, 421, 371);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (445, 394, 421);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (446, 422, 305);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (447, 423, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (448, 13, 423);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (449, 82, 423);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (450, 410, 423);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (451, 424, 305);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (452, 425, 290);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (453, 426, 290);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (454, 427, 290);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (455, 428, 290);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (456, 429, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (457, 346, 429);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (458, 430, 423);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (459, 66, 430);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (460, 431, 373);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (461, 432, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (462, 433, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (463, 143, 197);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (464, 152, 197);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (465, 153, 197);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (466, 154, 197);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (467, 156, 197);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (468, 239, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (469, 240, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (470, 241, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id) VALUES (471, 266, NULL);


--
-- Name: tag_relation_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('tag_relation_sequence', 1000, false);


--
-- Data for Name: tag_search_group; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (1, 160, 160);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (2, 325, 325);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (3, 326, 321);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (4, 326, 322);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (5, 326, 324);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (6, 325, 326);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (7, 326, 326);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (8, 327, 327);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (9, 328, 327);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (10, 328, 328);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (11, 329, 329);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (12, 330, 330);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (13, 332, 332);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (14, 344, 2);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (15, 344, 64);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (16, 344, 344);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (17, 315, 344);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (18, 363, 327);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (19, 363, 328);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (20, 363, 363);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (21, 364, 327);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (22, 364, 328);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (23, 364, 363);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (24, 364, 364);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (25, 365, 327);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (26, 365, 328);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (27, 365, 363);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (28, 365, 364);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (29, 365, 365);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (30, 368, 115);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (31, 368, 368);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (32, 372, 90);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (33, 372, 124);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (34, 372, 251);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (35, 372, 257);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (36, 372, 258);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (37, 372, 372);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (38, 373, 38);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (39, 373, 46);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (40, 373, 373);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (41, 374, 6);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (42, 374, 115);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (43, 374, 159);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (44, 374, 368);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (45, 374, 374);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (46, 375, 28);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (47, 375, 73);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (48, 375, 162);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (49, 375, 228);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (50, 375, 233);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (51, 375, 311);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (52, 375, 375);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (53, 376, 61);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (54, 376, 91);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (55, 376, 105);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (56, 376, 117);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (57, 376, 121);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (58, 376, 134);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (59, 376, 161);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (60, 376, 201);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (61, 376, 376);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (62, 378, 18);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (63, 378, 36);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (64, 378, 37);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (65, 378, 51);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (66, 378, 68);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (67, 378, 69);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (68, 378, 71);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (69, 378, 106);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (70, 378, 122);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (71, 378, 125);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (72, 378, 131);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (73, 378, 146);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (74, 378, 170);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (75, 378, 202);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (76, 378, 203);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (77, 378, 248);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (78, 378, 250);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (79, 378, 277);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (80, 378, 279);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (81, 378, 337);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (82, 378, 345);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (83, 378, 348);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (84, 378, 378);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (85, 381, 18);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (86, 381, 36);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (87, 381, 37);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (88, 381, 51);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (89, 381, 71);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (90, 381, 122);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (91, 381, 125);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (92, 381, 131);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (93, 381, 202);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (94, 381, 248);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (95, 381, 277);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (96, 381, 279);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (97, 381, 345);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (98, 378, 381);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (99, 381, 381);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (100, 391, 391);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (101, 395, 319);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (102, 395, 395);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (103, 396, 319);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (104, 396, 395);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (105, 396, 396);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (106, 397, 319);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (107, 397, 395);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (108, 397, 396);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (109, 397, 397);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (110, 398, 319);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (111, 398, 395);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (112, 398, 396);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (113, 398, 397);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (114, 398, 398);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (115, 399, 329);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (116, 399, 399);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (117, 400, 329);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (118, 400, 399);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (119, 400, 400);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (120, 401, 329);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (121, 401, 399);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (122, 401, 400);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (123, 401, 401);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (124, 402, 329);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (125, 402, 399);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (126, 402, 400);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (127, 402, 401);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (128, 402, 402);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (129, 404, 267);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (130, 404, 268);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (131, 404, 404);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (132, 415, 2);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (133, 415, 64);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (134, 415, 315);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (135, 415, 344);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (136, 415, 415);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (137, 416, 218);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (138, 416, 219);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (139, 416, 316);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (140, 416, 317);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (141, 416, 416);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (142, 417, 300);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (143, 417, 417);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (144, 419, 300);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (145, 419, 417);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (146, 419, 419);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (147, 421, 27);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (148, 421, 269);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (149, 421, 421);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (150, 422, 300);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (151, 422, 417);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (152, 422, 419);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (153, 422, 422);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (154, 424, 300);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (155, 424, 417);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (156, 424, 419);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (157, 424, 422);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (158, 424, 424);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (159, 425, 330);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (160, 425, 425);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (161, 426, 330);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (162, 426, 425);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (163, 426, 426);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (164, 427, 330);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (165, 427, 425);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (166, 427, 426);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (167, 427, 427);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (168, 428, 330);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (169, 428, 425);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (170, 428, 426);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (171, 428, 427);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (172, 428, 428);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (173, 368, 434);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (174, 374, 434);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (175, 372, 435);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (176, 376, 436);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (177, 368, 437);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (178, 374, 437);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (179, 376, 438);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (180, 376, 439);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (181, 375, 440);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (182, 375, 441);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (183, 375, 442);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (184, 368, 443);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (185, 374, 443);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (186, 378, 444);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (187, 14, 447);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (188, 2, 2);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (189, 7, 7);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (190, 9, 9);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (191, 14, 14);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (192, 9, 30);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (193, 14, 49);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (194, 64, 2);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (195, 64, 64);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (196, 88, 31);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (197, 88, 88);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (198, 14, 123);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (199, 218, 218);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (200, 219, 218);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (201, 219, 219);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (202, 290, 290);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (203, 291, 291);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (204, 292, 292);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (205, 300, 300);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (206, 305, 305);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (207, 306, 306);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (208, 315, 2);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (209, 315, 64);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (210, 315, 315);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (211, 218, 316);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (212, 219, 316);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (213, 316, 316);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (214, 317, 316);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (215, 218, 317);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (216, 219, 317);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (217, 317, 317);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (218, 319, 319);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (219, 321, 321);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (220, 322, 321);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (221, 322, 322);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (222, 321, 324);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (223, 322, 324);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (224, 324, 324);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (225, 325, 321);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (226, 325, 322);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (227, 325, 324);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (228, 378, 453);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (229, 381, 453);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (230, 378, 462);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (231, 376, 466);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (232, 421, 472);
INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id) VALUES (233, 375, 473);


--
-- Name: tag_search_group_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('tag_search_group_sequence', 1, false);


--
-- Name: tag_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('tag_sequence', 1000, false);


--
-- Data for Name: target; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: target_proposal; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: target_proposal_dish; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Name: target_proposal_dish_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('target_proposal_dish_sequence', 1000, false);


--
-- Name: target_proposal_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('target_proposal_sequence', 1000, false);


--
-- Data for Name: target_proposal_slot; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Name: target_proposal_slot_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('target_proposal_slot_sequence', 1000, false);


--
-- Name: target_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('target_sequence', 1000, false);


--
-- Data for Name: target_slot; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: target_slots; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: target_tags; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Name: user_id_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('user_id_sequence', 1000, false);


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO users (user_id, email, enabled, last_password_reset_date, password, username) VALUES (1, NULL, true, NULL, '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'rufus');
INSERT INTO users (user_id, email, enabled, last_password_reset_date, password, username) VALUES (20, NULL, true, NULL, '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'me');
INSERT INTO users (user_id, email, enabled, last_password_reset_date, password, username) VALUES (23, NULL, true, NULL, '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'carrie');
INSERT INTO users (user_id, email, enabled, last_password_reset_date, password, username) VALUES (26, NULL, true, NULL, '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'mom');
INSERT INTO users (user_id, email, enabled, last_password_reset_date, password, username) VALUES (29, NULL, true, NULL, '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'michelle');
INSERT INTO users (user_id, email, enabled, last_password_reset_date, password, username) VALUES (2, NULL, NULL, NULL, 'password', 'testname');


--
-- Name: authority_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY authority
    ADD CONSTRAINT authority_pkey PRIMARY KEY (authority_id);


--
-- Name: auto_tag_instructions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY auto_tag_instructions
    ADD CONSTRAINT auto_tag_instructions_pkey PRIMARY KEY (instruction_id);


--
-- Name: dish_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY dish
    ADD CONSTRAINT dish_pkey PRIMARY KEY (dish_id);


--
-- Name: list_category_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY list_category
    ADD CONSTRAINT list_category_pkey PRIMARY KEY (category_id);


--
-- Name: list_item_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY list_item
    ADD CONSTRAINT list_item_pkey PRIMARY KEY (item_id);


--
-- Name: list_layout_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY list_layout
    ADD CONSTRAINT list_layout_pkey PRIMARY KEY (layout_id);


--
-- Name: list_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY list
    ADD CONSTRAINT list_pkey PRIMARY KEY (list_id);


--
-- Name: list_tag_stats_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY list_tag_stats
    ADD CONSTRAINT list_tag_stats_pkey PRIMARY KEY (list_tag_stat_id);


--
-- Name: meal_plan_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY meal_plan
    ADD CONSTRAINT meal_plan_pkey PRIMARY KEY (meal_plan_id);


--
-- Name: meal_plan_slot_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY meal_plan_slot
    ADD CONSTRAINT meal_plan_slot_pkey PRIMARY KEY (meal_plan_slot_id);


--
-- Name: proposal_context_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY proposal_context
    ADD CONSTRAINT proposal_context_pkey PRIMARY KEY (proposal_context_id);


--
-- Name: proposal_context_slot_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY proposal_context_slot
    ADD CONSTRAINT proposal_context_slot_pkey PRIMARY KEY (proposal_context_slot_id);


--
-- Name: shadow_tags_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY shadow_tags
    ADD CONSTRAINT shadow_tags_pkey PRIMARY KEY (shadow_tag_id);


--
-- Name: tag_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tag
    ADD CONSTRAINT tag_pkey PRIMARY KEY (tag_id);


--
-- Name: tag_relation_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tag_relation
    ADD CONSTRAINT tag_relation_pkey PRIMARY KEY (tag_relation_id);


--
-- Name: tag_search_group_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tag_search_group
    ADD CONSTRAINT tag_search_group_pkey PRIMARY KEY (tag_search_group_id);


--
-- Name: target_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY target
    ADD CONSTRAINT target_pkey PRIMARY KEY (target_id);


--
-- Name: target_proposal_dish_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY target_proposal_dish
    ADD CONSTRAINT target_proposal_dish_pkey PRIMARY KEY (proposal_dish_id);


--
-- Name: target_proposal_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY target_proposal
    ADD CONSTRAINT target_proposal_pkey PRIMARY KEY (proposal_id);


--
-- Name: target_proposal_slot_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY target_proposal_slot
    ADD CONSTRAINT target_proposal_slot_pkey PRIMARY KEY (slot_id);


--
-- Name: target_slot_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY target_slot
    ADD CONSTRAINT target_slot_pkey PRIMARY KEY (target_slot_id);


--
-- Name: uk_19cigef2k4i5c1ls7a1m2lndw; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY target_tags
    ADD CONSTRAINT uk_19cigef2k4i5c1ls7a1m2lndw UNIQUE (tags_target_slot_id);


--
-- Name: uk_r6ms6yd0im7k5olg94b35nuku; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY target_slots
    ADD CONSTRAINT uk_r6ms6yd0im7k5olg94b35nuku UNIQUE (slots_target_slot_id);


--
-- Name: users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- Name: fk1ddq3ct1ulogjn5ijs8ert7hw; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY list_item
    ADD CONSTRAINT fk1ddq3ct1ulogjn5ijs8ert7hw FOREIGN KEY (list_id) REFERENCES list(list_id);


--
-- Name: fk3vyajpbcb8wl8380yntahtgtf; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tag_relation
    ADD CONSTRAINT fk3vyajpbcb8wl8380yntahtgtf FOREIGN KEY (parent_tag_id) REFERENCES tag(tag_id);


--
-- Name: fk4cvbymf9m9quckcouehn0p414; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY dish
    ADD CONSTRAINT fk4cvbymf9m9quckcouehn0p414 FOREIGN KEY (user_id) REFERENCES users(user_id);


--
-- Name: fk6x8vvlp985udfs7g15uuxj42c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tag_relation
    ADD CONSTRAINT fk6x8vvlp985udfs7g15uuxj42c FOREIGN KEY (child_tag_id) REFERENCES tag(tag_id);


--
-- Name: fkbh371e2vv53a3arqea0hf3jkl; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY dish_tags
    ADD CONSTRAINT fkbh371e2vv53a3arqea0hf3jkl FOREIGN KEY (dish_id) REFERENCES dish(dish_id);


--
-- Name: fkclr8vrg8b1cwgwjsgcd5jtj6a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY category_tags
    ADD CONSTRAINT fkclr8vrg8b1cwgwjsgcd5jtj6a FOREIGN KEY (tag_id) REFERENCES tag(tag_id);


--
-- Name: fkcoy8wmmbb7og08ydch06gt42o; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY target_slots
    ADD CONSTRAINT fkcoy8wmmbb7og08ydch06gt42o FOREIGN KEY (slots_target_slot_id) REFERENCES target_slot(target_slot_id);


--
-- Name: fkd079bghbqgkxaa6ev4si0mh1x; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY proposal_context_slot
    ADD CONSTRAINT fkd079bghbqgkxaa6ev4si0mh1x FOREIGN KEY (proposal_context_id) REFERENCES proposal_context(proposal_context_id);


--
-- Name: fkdit15dhtc9j583c1pp21c8ss0; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY meal_plan_slot
    ADD CONSTRAINT fkdit15dhtc9j583c1pp21c8ss0 FOREIGN KEY (dish_dish_id) REFERENCES dish(dish_id);


--
-- Name: fkhgv828ap379mspi2sy46nhym2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY proposal_context_slot
    ADD CONSTRAINT fkhgv828ap379mspi2sy46nhym2 FOREIGN KEY (dish_dish_id) REFERENCES dish(dish_id);


--
-- Name: fkhhja2slk7gr34nhgcnlyw21ge; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY meal_plan_slot
    ADD CONSTRAINT fkhhja2slk7gr34nhgcnlyw21ge FOREIGN KEY (meal_plan_id) REFERENCES meal_plan(meal_plan_id);


--
-- Name: fkivtoroma7ffnyomikiqoxbkcf; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY target_slots
    ADD CONSTRAINT fkivtoroma7ffnyomikiqoxbkcf FOREIGN KEY (target_entity_target_id) REFERENCES target(target_id);


--
-- Name: fkka37hl6mopj61rfbe97si18p8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY authority
    ADD CONSTRAINT fkka37hl6mopj61rfbe97si18p8 FOREIGN KEY (user_id) REFERENCES users(user_id);


--
-- Name: fklcvoij9ynqfllhxgn9v6qpsh8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY list_item
    ADD CONSTRAINT fklcvoij9ynqfllhxgn9v6qpsh8 FOREIGN KEY (tag_id) REFERENCES tag(tag_id);


--
-- Name: fknru4508d44i3jxrbnlj9p4eer; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY target_proposal_dish
    ADD CONSTRAINT fknru4508d44i3jxrbnlj9p4eer FOREIGN KEY (target_proposal_slot_slot_id) REFERENCES target_proposal_slot(slot_id);


--
-- Name: fkns9s1sef980caqqamoee8srdw; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY category_tags
    ADD CONSTRAINT fkns9s1sef980caqqamoee8srdw FOREIGN KEY (category_id) REFERENCES list_category(category_id);


--
-- Name: fkpy8j9ypbt3d59bjs0hgl3wcct; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY dish_tags
    ADD CONSTRAINT fkpy8j9ypbt3d59bjs0hgl3wcct FOREIGN KEY (tag_id) REFERENCES tag(tag_id);


--
-- Name: fkrhcs3i2p15y79hn00y5ic41gn; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY list_category
    ADD CONSTRAINT fkrhcs3i2p15y79hn00y5ic41gn FOREIGN KEY (layout_id) REFERENCES list_layout(layout_id);


--
-- Name: fks91g1q59v13b5q04mlc7b9k3i; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY target_proposal_slot
    ADD CONSTRAINT fks91g1q59v13b5q04mlc7b9k3i FOREIGN KEY (target_proposal_proposal_id) REFERENCES target_proposal(proposal_id);


--
-- Name: public; Type: ACL; Schema: -; Owner: -
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- Name: selectabletags; Type: ACL; Schema: public; Owner: -
--

REVOKE ALL ON TABLE selectabletags FROM PUBLIC;
REVOKE ALL ON TABLE selectabletags FROM postgres;
GRANT ALL ON TABLE selectabletags TO postgres;
GRANT ALL ON TABLE selectabletags TO bankuser;


--
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: -; Owner: -
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres REVOKE ALL ON TABLES  FROM PUBLIC;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres REVOKE ALL ON TABLES  FROM postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres GRANT ALL ON TABLES  TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres GRANT ALL ON TABLES  TO bankuser;


--
-- PostgreSQL database dump complete
--

