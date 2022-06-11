CREATE TABLE public.user_properties
(
    user_property_id bigint                 NOT NULL,
    user_id          bigint,
    property_key     character varying(150) NOT NULL,
    property_value character varying(150) NOT NULL
);


GRANT
ALL
ON public.user_properties TO postgres;

--
-- Name: user_properties_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.user_properties_id_seq
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;


-- test undo
--drop table user_properties;
--drop sequence user_properties_id_seq;


