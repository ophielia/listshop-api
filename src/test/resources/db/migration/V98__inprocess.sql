-- changed to login with email
update users set email = username;

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

ALTER TABLE list_item
    ADD COLUMN removed_on timestamp with time zone ;


ALTER TABLE list_item
    ADD COLUMN updated_on timestamp with time zone ;

-- users now keyed off of user email
update users set email = username;

------------------------
-- undo for dev
------------------------
--ALTER TABLE list_item
--    DROP COLUMN removed_on;

--ALTER TABLE list_item
--    DROP COLUMN updated_on;

-- ALTER TABLE users drop COLUMN creation_date;

--drop SEQUENCE authority_id_seq;

------------------------
-- these are all the timestamp to with timezone changes
------------------------
-- dish
alter table dish add column last_added_temp timestamp;
update dish set last_added_temp = last_added;
ALTER TABLE dish
    ALTER COLUMN last_added TYPE timestamp with time zone ;
update dish
set last_added = last_added_temp at time zone 'utc' at time zone 'utc'
where last_added_temp is not null;
alter table dish drop column last_added_temp;

alter table dish add column created_on_temp timestamp;
update dish set created_on_temp = created_on;
ALTER TABLE dish
    ALTER COLUMN created_on TYPE timestamp with time zone ;
update dish
set created_on = created_on_temp at time zone 'utc' at time zone 'utc'
where created_on_temp is not null;
alter table dish drop column created_on_temp;

-- item
alter table list_item add column added_on_temp timestamp;
update list_item  set added_on_temp = added_on;
ALTER TABLE list_item
    ALTER COLUMN added_on TYPE timestamp with time zone ;
update list_item
set added_on = added_on_temp at time zone 'utc' at time zone 'utc'
where added_on_temp is not null;
alter table list_item drop column added_on_temp;


alter table list_item add column crossed_off_temp timestamp;
update list_item  set crossed_off_temp = crossed_off;
ALTER TABLE list_item
    ALTER COLUMN crossed_off TYPE timestamp with time zone ;
update list_item
set crossed_off = crossed_off_temp at time zone 'utc' at time zone 'utc'
where crossed_off_temp is not null;
alter table list_item drop column crossed_off_temp;


-- meal plan

alter table meal_plan add column created_temp timestamp;
update meal_plan  set created_temp = created;
ALTER TABLE meal_plan
    ALTER COLUMN created TYPE timestamp with time zone ;
update meal_plan
set created = created_temp at time zone 'utc' at time zone 'utc'
where created_temp is not null;
alter table meal_plan drop column created_temp;


-- proposal


alter table proposal add column created_temp timestamp;
update proposal  set created_temp = created;
ALTER TABLE proposal
    ALTER COLUMN created TYPE timestamp with time zone ;
update proposal
set created = created_temp at time zone 'utc' at time zone 'utc'
where created_temp is not null;
alter table proposal drop column created_temp;


-- list


alter table list add column created_on_temp timestamp;
update list  set created_on_temp = created_on;
ALTER TABLE list
    ALTER COLUMN created_on TYPE timestamp with time zone ;
update list
set created_on = created_on_temp at time zone 'utc' at time zone 'utc'
where created_on_temp is not null;
alter table list drop column created_on_temp;


-- target


alter table target add column created_temp timestamp;
update target  set created_temp = created;
ALTER TABLE target
    ALTER COLUMN created TYPE timestamp with time zone ;
update target
set created = created_temp at time zone 'utc' at time zone 'utc'
where created_temp is not null;
alter table target drop column created_temp;


alter table target add column last_updated_temp timestamp;
update target  set last_updated_temp = last_updated;
ALTER TABLE target
    ALTER COLUMN last_updated TYPE timestamp with time zone ;
update target
set last_updated = last_updated_temp at time zone 'utc' at time zone 'utc'
where last_updated_temp is not null;
alter table target drop column last_updated_temp;


alter table target add column expires_temp timestamp;
update target  set expires_temp = expires;
ALTER TABLE target
    ALTER COLUMN expires TYPE timestamp with time zone ;
update target
set expires = expires_temp at time zone 'utc' at time zone 'utc'
where expires_temp is not null;
alter table target drop column expires_temp;




-- undo section
--ALTER TABLE users DROP COLUMN creation_date;
--drop sequence authority_id_seq;
-- CREATE SEQUENCE authority_seq
--    START WITH 1
--    INCREMENT BY 1
--    NO MINVALUE
--    NO MAXVALUE
--    CACHE 1;
