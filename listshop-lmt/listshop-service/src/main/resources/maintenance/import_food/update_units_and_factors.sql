delete from factors;
delete from units;



insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1000, 'cup', 'HYBRID', 'SOLID',FALSE, TRUE,FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1001, 'tablespoon', 'HYBRID', 'SOLID',FALSE, TRUE,FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1002, 'teaspoon', 'HYBRID', 'SOLID',FALSE, TRUE,FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1003, 'liter', 'METRIC', 'VOLUME',TRUE, TRUE,TRUE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1004, 'milliliter', 'METRIC', 'VOLUME',TRUE, TRUE,TRUE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1005, 'gallon', 'US', 'VOLUME',TRUE, TRUE,TRUE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1006, 'pint', 'US', 'VOLUME',FALSE, TRUE,TRUE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1007, 'fl oz', 'US', 'VOLUME',TRUE, TRUE,TRUE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1008, 'lb', 'US', 'WEIGHT',TRUE, TRUE,FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1009, 'oz', 'US', 'WEIGHT',TRUE, TRUE,FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1010, 'quart', 'US', 'VOLUME',TRUE, TRUE,TRUE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1011, 'unit', 'UNIT', 'NONE',TRUE, TRUE,FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1013, 'gram', 'METRIC', 'WEIGHT',TRUE, TRUE,FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1014, 'kilogram', 'METRIC', 'WEIGHT',TRUE, TRUE,FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1015, 'centiliter', 'METRIC', 'VOLUME',FALSE, TRUE,TRUE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1016, 'milligram', 'METRIC', 'WEIGHT',TRUE, TRUE,FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1017, 'cup (fluid)', 'US', 'VOLUME',FALSE, TRUE,TRUE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1019, 'teaspoon (fluid)', 'HYBRID', 'LIQUID',FALSE, TRUE,TRUE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1021, 'tablespoon (fluid)', 'HYBRID', 'LIQUID',FALSE, TRUE,TRUE);


insert into public.factors (factor_id, factor, to_unit, from_unit, tag_id)
values  (1, 0.0625, 1005, 1017, null),
        (2, 0.5, 1006, 1017, null),
        (3, 0.25, 1010, 1017, null),
        (4, 0.125, 1017, 1007, null),
        (5, 0.0078125, 1005, 1007, null),
        (6, 0.0625, 1006, 1007, null),
        (7, 0.03125, 1010, 1007, null),
        (8, 0.001, 1014, 1013, null),
        (9, 0.0022, 1008, 1013, null),
        (10, 0.0352733686067019, 1009, 1013, null),
        (11, 2.20462262, 1008, 1014, null),
        (12, 35.2733686067019, 1009, 1014, null),
        (13, 100, 1015, 1003, null),
        (14, 4.22675, 1017, 1003, null),
        (15, 0.26417, 1005, 1003, null),
        (16, 1000, 1004, 1003, null),
        (17, 2.1133764, 1006, 1003, null),
        (18, 1.05668821, 1010, 1003, null),
        (19, 0.00422675, 1017, 1004, null),
        (20, 0.00026417, 1005, 1004, null),
        (21, 0.0021133764, 1006, 1004, null),
        (22, 0.0010566882, 1010, 1004, null),
        (23, 0.125, 1005, 1006, null),
        (24, 0.5, 1010, 1006, null),
        (25, 0.25, 1005, 1010, null),
        (26, 0.0000022, 1008, 1016, null),
        (27, 0.00003527, 1009, 1016, null),
        (28, 0.0105668821, 1010, 1015, null),
        (29, 0.0422675, 1017, 1015, null),
        (30, 0.0026417205124156, 1005, 1015, null),
        (31, 0.021133764, 1006, 1015, null),
        (32, 0.001, 1003, 1004, null),
        (33, 0.1, 1015, 1004, null),
        (34, 0.01, 1003, 1015, null),
        (35, 10, 1004, 1015, null),
        (36, 0.0625, 1008, 1009, null),
        (37, 16, 1009, 1008, null),
        (38, 0.3333333333, 1021, 1019, null),
        (39, 0.33333333333, 1001, 1002, null),
        (40, 0.020833333333, 1000, 1002, null),
        (41, 0.020833333333, 1017, 1019, null),
        (42, 0.020833333333, 1000, 1002, null),
        (43, 0.0625, 1000, 1001, null),
        (44, 0.0625, 1017, 1021, null),
        (45, 3, 1002, 1001, null),
        (46, 3, 1019, 1021, null),
        (47, 0.00390625, 1005, 1021, null),
        (48, 0.03125, 1006, 1021, null),
        (49, 0.5, 1007, 1021, null),
        (50, 0.015625, 1010, 1021, null),
        (51, 0.00130208333, 1005, 1019, null),
        (52, 0.0104166666666, 1006, 1019, null),
        (53, 0.1666666666, 1007, 1019, null),
        (54, 0.00520833333333, 1010, 1019, null),
        (55, 0.0078125, 1007, 1005, null),
        (56, 16, 1007, 1006, null),
        (57, 8, 1007, 1017, null),
        (58, 33.8140227, 1007, 1003, null),
        (59, 0.033814023, 1007, 1004, null),
        (60, 0.33814203, 1007, 1015, null),
        (61, 1000, 1013, 1014, null),
        (62, 16, 1001, 1000, null),
        (63, 48, 1002, 1000, null);