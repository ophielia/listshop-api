-- now the data
insert into dish_items (dish_item_id, dish_id, tag_id)
select nextval('dish_item_sequence'), dish_id, tag_id
from dish_tags;



insert into units (unit_id, type, name, is_liquid, is_list_unit, is_dish_unit, is_weight, is_volume)
values (1001, 'Hybrid', 'tablespoon', false, false, true, false, false),
       (1002, 'Hybrid', 'teaspoon', false, false, true, false, false),
       (1009, 'Imperial', 'oz', false, false, false, true, false),
       (1008, 'Imperial', 'lb', false, false, false, true, false),
       (1006, 'Imperial', 'pint', true, false, true, false, true),
       (1005, 'Imperial', 'gallon', true, false, false, false, true),
       (1007, 'Imperial', 'fl oz', true, false, true, false, true),
       (1010, 'Imperial', 'quart', true, true, true, false, true),
       (1000, 'Imperial', 'cup', false, false, true, false, true),
       (1016, 'Metric', 'milligram', false, true, true, true, false),
       (1013, 'Metric', 'gram', false, false, true, true, false),
       (1014, 'Metric', 'kilogram', false, true, true, true, false),
       (1004, 'Metric', 'milliliter', false, false, true, false, true),
       (1003, 'Metric', 'liter', true, true, true, false, true),
       (1015, 'Metric', 'centiliter', false, false, true, false, true),
       (1011, 'Unit', 'unit', false, false, false, false, false);


insert into factors (factor_id, factor, to_unit, from_unit)
values (1220, 16, 1005, 1000),
       (1221, 2, 1006, 1000),
       (1222, 4, 1010, 1000),
       (1223, 8, 1000, 1007),
       (1224, 128, 1005, 1007),
       (1225, 16, 1006, 1007),
       (1226, 32, 1010, 1007),
       (1227, 1000, 1014, 1013),
       (1228, 0.0022, 1008, 1013),
       (1229, 0.0352733686067019, 1009, 1013),
       (1230, 0.0353, 1009, 1013),
       (1231, 2.20462262, 1008, 1014),
       (1232, 35.2733686067019, 1009, 1014),
       (1233, 0.01, 1015, 1003),
       (1234, 4.22675284, 1000, 1003),
       (1235, 0.21996915, 1005, 1003),
       (1236, 0.001, 1004, 1003),
       (1237, 0.475, 1006, 1003),
       (1238, 0.8798, 1010, 1003),
       (1239, 0.00422675, 1000, 1004),
       (1240, 3800, 1005, 1004),
       (1241, 475, 1006, 1004),
       (1242, 950, 1010, 1004),
       (1243, 28.35, 1013, 1009),
       (1244, 8, 1005, 1006),
       (1245, 2, 1010, 1006),
       (1246, 4, 1005, 1010),
       (1247, 0.0000022, 1008, 1016),
       (1248, 0.00003527, 1009, 1016),
       (1249, 0.0087987, 1010, 1015);