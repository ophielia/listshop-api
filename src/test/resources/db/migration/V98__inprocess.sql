-- is display will be for default tag that deleted tags without substitute are assigned to.
ALTER TABLE users ADD COLUMN creation_date  timestamp;


-- replacing authority sequence
CREATE SEQUENCE authority_id_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


drop SEQUENCE authority_seq;

-- undo section
--ALTER TABLE users DROP COLUMN creation_date;
--drop sequence authority_id_seq;
-- CREATE SEQUENCE authority_seq
--    START WITH 1
--    INCREMENT BY 1
--    NO MINVALUE
--    NO MAXVALUE
--    CACHE 1;
