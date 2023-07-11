CREATE TABLE if not exists public.campaigns
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


CREATE SEQUENCE if not exists public.campaign_sequence
    INCREMENT 1
    START 1000
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;


/*
 rollback

 drop table campaigns;
 drop sequence if exists campaign_sequences;

delete from flyway_schema_history where installed_rank = 7
select * from  flyway_schema_history
 */
