-- reinsert units and factors

delete from factors;
delete from units;



insert into public.units (unit_id, type, subtype, name, is_liquid, is_list_unit, is_dish_unit, is_weight, is_volume)
values  (1000, 'HYBRID', 'SOLID', 'cup', false, false, true, false, false),
        (1001, 'HYBRID', 'SOLID', 'tablespoon', false, false, true, false, false),
        (1002, 'HYBRID', 'SOLID', 'teaspoon', false, false, true, false, false),
        (1003, 'METRIC', 'VOLUME', 'liter', true, true, true, false, false),
        (1004, 'METRIC', 'VOLUME', 'milliliter', true, true, true, false, false),
        (1005, 'US', 'VOLUME', 'gallon', true, true, true, false, false),
        (1006, 'US', 'VOLUME', 'pint', true, false, true, false, false),
        (1007, 'US', 'VOLUME', 'fl oz', true, true, true, false, false),
        (1008, 'US', 'WEIGHT', 'lb', false, true, true, false, false),
        (1009, 'US', 'WEIGHT', 'oz', false, true, true, false, false),
        (1010, 'US', 'VOLUME', 'quart', true, true, true, false, false),
        (1011, 'UNIT', 'NONE', 'unit', false, true, true, false, false),
        (1013, 'METRIC', 'WEIGHT', 'gram', false, true, true, false, false),
        (1014, 'METRIC', 'WEIGHT', 'kilogram', false, true, true, false, false),
        (1015, 'METRIC', 'VOLUME', 'centiliter', true, false, true, false, false),
        (1016, 'METRIC', 'WEIGHT', 'milligram', false, true, true, false, false),
        (1017, 'US', 'VOLUME', 'cup (fluid)', true, false, true, false, false),
        (1019, 'HYBRID', 'LIQUID', 'teaspoon (fluid)', true, false, true, false, false),
        (1021, 'HYBRID', 'LIQUID', 'tablespoon (fluid)', true, false, true, false, false);

insert into public.factors (factor_id, factor, to_unit, from_unit)
values  (1336, 0.0625, 1005, 1017),
        (1337, 0.5, 1006, 1017),
        (1338, 0.25, 1010, 1017),
        (1339, 0.125, 1017, 1007),
        (1340, 0.0078125, 1005, 1007),
        (1341, 0.0625, 1006, 1007),
        (1342, 0.03125, 1010, 1007),
        (1343, 0.001, 1014, 1013),
        (1344, 0.0022, 1008, 1013),
        (1345, 0.0352733686067019, 1009, 1013),
        (1346, 2.20462262, 1008, 1014),
        (1347, 35.2733686067019, 1009, 1014),
        (1348, 100, 1015, 1003),
        (1349, 4.22675, 1017, 1003),
        (1350, 0.26417, 1005, 1003),
        (1351, 1000, 1004, 1003),
        (1352, 2.1133764, 1006, 1003),
        (1353, 1.05668821, 1010, 1003),
        (1354, 0.00422675, 1017, 1004),
        (1355, 0.00026417, 1005, 1004),
        (1356, 0.0021133764, 1006, 1004),
        (1357, 0.0010566882, 1010, 1004),
        (1358, 0.125, 1005, 1006),
        (1359, 0.5, 1010, 1006),
        (1360, 0.25, 1005, 1010),
        (1361, 0.0000022, 1008, 1016),
        (1362, 0.00003527, 1009, 1016),
        (1363, 0.0105668821, 1010, 1015),
        (1364, 0.0422675, 1017, 1015),
        (1365, 0.0026417205124156, 1005, 1015),
        (1366, 0.021133764, 1006, 1015),
        (1367, 0.001, 1003, 1004),
        (1368, 0.1, 1015, 1004),
        (1369, 0.01, 1003, 1015),
        (1370, 10, 1004, 1015),
        (1371, 0.0625, 1008, 1009),
        (1372, 16, 1009, 1008),
        (1373, 0.3333333333, 1021, 1019),
        (1374, 0.33333333333, 1001, 1002),
        (1375, 0.020833333333, 1000, 1002),
        (1376, 0.020833333333, 1017, 1019),
        (1377, 0.020833333333, 1000, 1002),
        (1378, 0.0625, 1000, 1001),
        (1379, 0.0625, 1017, 1021),
        (1380, 3, 1002, 1001),
        (1381, 3, 1019, 1021),
        (1382, 0.00390625, 1005, 1021),
        (1383, 0.03125, 1006, 1021),
        (1384, 0.5, 1007, 1021),
        (1385, 0.015625, 1010, 1021),
        (1386, 0.00130208333, 1005, 1019),
        (1387, 0.0104166666666, 1006, 1019),
        (1388, 0.1666666666, 1007, 1019),
        (1389, 0.00520833333333, 1010, 1019),
        (1390, 0.0078125, 1007, 1005),
        (1391, 16, 1007, 1006),
        (1392, 8, 1007, 1017),
        (1393, 33.8140227, 1007, 1003),
        (1394, 0.033814023, 1007, 1004),
        (1395, 0.33814203, 1007, 1015);

insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 1000 as factor from units f,units t where lower(f.name) = 'kilogram' and lower(t.name) = 'gram';


update tag t
set is_group = true
from test.public.tag_relation tr
where tr.parent_tag_id = t.tag_id;