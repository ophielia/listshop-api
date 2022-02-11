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

-- rollback

-- drop table tokens;
-- drop sequence token_sequence;

