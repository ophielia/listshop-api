CREATE SEQUENCE public.temp_conversion_seq
    START WITH 1000
              INCREMENT BY 1
              NO MINVALUE
              NO MAXVALUE
              CACHE 1;

update tmp_temp set conversion_id = nextval('temp_conversion_seq');

drop sequence temp_conversion_seq;

select * from food_conversions c join foods f on f.food_id = c.food_id;

select * from food_categories