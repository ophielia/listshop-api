ALTER TABLE list_item_details
    ADD COLUMN contains_unspecified boolean DEFAULT false,
    ADD COLUMN is_user_size boolean DEFAULT false,
    ADD COLUMN nonspecified boolean DEFAULT true;

ALTER TABLE list_item_details
    DROP COLUMN orig_whole_quantity,
    DROP COLUMN orig_fractional_quantity,
    DROP COLUMN orig_quantity,
    DROP COLUMN orig_unit_id;

ALTER TABLE list_item
    ADD COLUMN quantity            double precision,
    ADD COLUMN raw_quantity         double precision,
    ADD COLUMN whole_quantity      integer,
    ADD COLUMN fractional_quantity character varying(56),
    ADD COLUMN specification_type  character varying(56),
    ADD COLUMN unit_id             bigint,
    ADD COLUMN unit_size           varchar(256),
    ADD COLUMN amount_text         varchar(256);

ALTER TABLE list_item
    DROP COLUMN free_text;

-----  for dropping during test / dev

--ALTER TABLE list_item_details
--    DROP COLUMN nonspecified;
--
--ALTER TABLE list_item
--    DROP COLUMN quantity,
--    DROP COLUMN whole_quantity,
--    DROP COLUMN fractional_quantity,
--    DROP COLUMN unit_id,
--    DROP COLUMN unit_size,
--    DROP COLUMN amount_text;
