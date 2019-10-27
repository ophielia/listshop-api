-- new field is_starter_list in list
alter table list
  add column is_starter_list boolean default false;

update list
set is_starter_list = true
where list_types = 'BaseList';


alter table list
  add column name character varying(255);


WITH list_name
       AS (SELECT user_id,
                  list_id,
                  Concat('Shopping List ', Rank()
                      OVER (
                        partition BY user_id
                        ORDER BY created_on)) AS newname
           FROM list)
UPDATE list l
SET NAME = n.NEWNAME
FROM list_name n
WHERE n.list_id = l.list_id;

ALTER TABLE public.list
  ALTER COLUMN name SET NOT NULL;



---- rollback

-- new field is_starter_list in list
--  alter table list
--    drop column is_starter_list;
--
--  alter table list
--    drop column name;


