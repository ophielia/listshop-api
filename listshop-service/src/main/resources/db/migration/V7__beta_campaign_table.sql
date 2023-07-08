CREATE TABLE public.campaigns
(
    campaign_id bigint NOT NULL,
    created_on  timestamp without time zone,
    email       character varying(255) COLLATE pg_catalog."default",
    campaign    character varying(255) COLLATE pg_catalog."default",
    user_id     bigint,
    CONSTRAINT campaign_pkey PRIMARY KEY (campaign_id)
)
    WITH (
        OIDS = FALSE
        );


CREATE SEQUENCE public.campaign_sequence
    INCREMENT 1
    START 1000
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;


/*
 rollback

 drop table campaigns;
 drop sequence campaign_sequences;
 */
