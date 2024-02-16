with conversions as (
select distinct f.food_id from foods f join food_conversions c using (fdc_id))
update foods f
set has_factor = true
from conversions cc
where cc.food_id = f.food_id;

;
update foods set has_factor = false where has_factor is null;
select * from foods;

select count(*) from foods;

select distinct unit_name from food_conversions;

select * from units where name in ('cup', 'tablespoon', 'teaspoon')


select * from food_conversions where unit_id is not null;

select * from foods where food_id = 21376;
