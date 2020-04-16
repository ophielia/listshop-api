-- add reference to db in dish

ALTER TABLE public.dish
    ADD COLUMN reference character varying(255);



-- rollback

-- alter table dish drop column reference;

