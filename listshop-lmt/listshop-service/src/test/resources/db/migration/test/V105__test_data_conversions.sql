insert into public.units (unit_id, type, subtype, name, is_liquid, is_list_unit, is_dish_unit, is_weight, is_volume, is_tag_specific, excluded_domains, one_way_conversion)
values  (1000, 'HYBRID', 'SOLID', 'cup', false, false, true, false, false, false, 'METRIC', false),
        (1001, 'HYBRID', 'SOLID', 'tablespoon', false, false, true, false, false, false, '', false),
        (1002, 'HYBRID', 'SOLID', 'teaspoon', false, false, true, false, false, false, '', false),
        (1003, 'METRIC', 'VOLUME', 'liter', true, true, true, false, false, false, '', false),
        (1004, 'METRIC', 'VOLUME', 'milliliter', true, true, true, false, false, false, '', false),
        (1005, 'US', 'VOLUME', 'gallon', true, true, true, false, false, false, '', false),
        (1006, 'US', 'VOLUME', 'pint', true, false, true, false, false, false, '', false),
        (1007, 'US', 'VOLUME', 'fl oz', true, true, true, false, false, false, '', false),
        (1008, 'US', 'WEIGHT', 'lb', false, true, true, false, false, false, '', false),
        (1009, 'US', 'WEIGHT', 'oz', false, true, true, false, false, false, '', false),
        (1010, 'US', 'VOLUME', 'quart', true, true, true, false, false, false, '', false),
        (1013, 'METRIC', 'WEIGHT', 'gram', false, true, true, false, false, false, '', false),
        (1014, 'METRIC', 'WEIGHT', 'kilogram', false, true, true, false, false, false, '', false),
        (1015, 'METRIC', 'VOLUME', 'centiliter', true, false, true, false, false, false, '', false),
        (1016, 'METRIC', 'WEIGHT', 'milligram', false, true, true, false, false, false, '', false),
        (1017, 'US', 'VOLUME', 'cup (fluid)', true, false, true, false, false, false, '', false),
        (1019, 'US', 'LIQUID', 'teaspoon (fluid)', true, false, true, false, false, false, '', false),
        (1021, 'US', 'LIQUID', 'tablespoon (fluid)', true, false, true, false, false, false, '', false),
        (1022, 'HYBRID', 'SOLID', 'slice', false, false, true, false, false, true, '', false),
        (1023, 'HYBRID', 'SOLID', 'stick', false, false, true, false, false, true, '', false),
        (1024, 'UK', 'VOLUME', 'gallon (UK)', true, true, true, false, false, false, '', false),
        (1025, 'UK', 'VOLUME', 'pint (UK)', true, false, true, false, false, false, '', false),
        (1026, 'UK', 'VOLUME', 'fl oz (UK)', true, true, true, false, false, false, '', false),
        (1027, 'UK', 'VOLUME', 'quart (UK)', true, true, true, false, false, false, '', false),
        (1028, 'UK', 'VOLUME', 'cup (fluid) (UK)', true, false, true, false, false, false, '', false),
        (1029, 'HYBRID', 'VOLUME', 'can', false, true, true, false, false, false, '', true),
        (1030, 'HYBRID', 'VOLUME', 'large can', false, true, true, false, false, false, '', true),
        (1031, 'HYBRID', 'VOLUME', 'small can', false, true, true, false, false, false, '', true),
        (1032, 'US', 'VOLUME', '#2 can', false, false, true, false, false, false, '', false),
        (1033, 'US', 'VOLUME', '14.5 oz can', false, false, true, false, false, false, '', false),
        (1034, 'US', 'VOLUME', '#2.5 can', false, false, true, false, false, false, '', false),
        (1035, 'US', 'VOLUME', '#3 can', false, false, true, false, false, false, '', false),
        (1036, 'US', 'VOLUME', '29 oz can', false, false, true, false, false, false, '', false),
        (1040, 'HYBRID', 'WEIGHT', 'leaf', false, false, true, false, false, true, '', false),
        (1041, 'HYBRID', 'WEIGHT', 'package', false, true, true, false, false, true, '', false),
        (1042, 'HYBRID', 'WEIGHT', 'packet', false, true, true, false, false, true, '', false),
        (1043, 'HYBRID', 'WEIGHT', 'pod', false, false, true, false, false, true, '', false),
        (1044, 'HYBRID', 'WEIGHT', 'ring', false, false, true, false, false, true, '', false),
        (1045, 'HYBRID', 'WEIGHT', 'sheet', false, false, true, false, false, true, '', false),
        (1046, 'HYBRID', 'WEIGHT', 'spear', false, false, true, false, false, true, '', false),
        (1047, 'HYBRID', 'WEIGHT', 'sprig', false, false, true, false, false, true, '', false),
        (1048, 'HYBRID', 'WEIGHT', 'stalk', false, false, true, false, false, true, '', false),
        (1049, 'HYBRID', 'WEIGHT', 'butter stick', false, false, true, false, false, true, 'METRIC,UK', false),
        (1050, 'HYBRID', 'WEIGHT', 'wedge', false, false, true, false, false, true, '', false),
        (1051, 'UK', 'VOLUME', 'teaspoon (fluid) (UK)', true, false, true, false, false, false, '', false),
        (1052, 'UK', 'VOLUME', 'tablespoon (fluid) (UK)', true, false, true, false, false, false, '', false),
        (1053, 'HYBRID', 'SOLID', 'pinch', false, false, true, false, false, false, '', false),
        (1054, 'HYBRID', 'VOLUME', 'jar', false, true, true, false, false, false, '', false),
        (1055, 'HYBRID', 'SOLID', 'clove', false, false, true, false, false, true, '', false),
        (1011, 'UNIT', 'NONE', 'unit', false, true, true, false, false, false, '', false),
        (1037, 'UNIT', 'NONE', 'bulb', false, false, true, false, false, true, '', false),
        (1038, 'UNIT', 'NONE', 'ear', false, true, true, false, false, true, '', false),
        (1039, 'UNIT', 'NONE', 'head', false, true, true, false, false, true, '', false);


update tag set conversion_id = 225108 where tag_id = 50597;
update tag set conversion_id = 226957 where tag_id = 50894;
update tag set conversion_id = 226657 where tag_id = 361;
update tag set conversion_id = 225108 where tag_id = 50778;
update tag set conversion_id = 226221 where tag_id = 116;
update tag set conversion_id = 228025 where tag_id = 51886;
update tag set conversion_id = 224668 where tag_id = 79;
update tag set conversion_id = 227535 where tag_id = 51648;
update tag set conversion_id = 225108 where tag_id = 50594;
update tag set conversion_id = 226974 where tag_id = 51222;
update tag set conversion_id = 227151 where tag_id = 42;
update tag set conversion_id = 225425 where tag_id = 178;
update tag set conversion_id = 226974 where tag_id = 50657;
update tag set conversion_id = 227300 where tag_id = 51071;
update tag set conversion_id = 225733 where tag_id = 350;
update tag set conversion_id = 226974 where tag_id = 51289;
update tag set conversion_id = 227535 where tag_id = 51637;
update tag set conversion_id = 226562 where tag_id = 237;
update tag set conversion_id = 224636 where tag_id = 51659;
update tag set conversion_id = 224636 where tag_id = 348;
update tag set conversion_id = 227300 where tag_id = 51084;
update tag set conversion_id = 225387 where tag_id = 41;
update tag set conversion_id = 227553 where tag_id = 51253;
update tag set conversion_id = 224830 where tag_id = 51781;
update tag set conversion_id = 228026 where tag_id = 51805;
update tag set conversion_id = 226873 where tag_id = 293;
update tag set conversion_id = 226974 where tag_id = 352;
update tag set conversion_id = 227637 where tag_id = 51962;
update tag set conversion_id = 225964 where tag_id = 50573;
update tag set conversion_id = 226657 where tag_id = 128;
update tag set conversion_id = 225386 where tag_id = 51176;
update tag set conversion_id = 225042 where tag_id = 50596;
update tag set conversion_id = 227151 where tag_id = 50830;
update tag set conversion_id = 226974 where tag_id = 51166;
update tag set conversion_id = 225489 where tag_id = 51764;
update tag set conversion_id = 225387 where tag_id = 51707;
update tag set conversion_id = 227955 where tag_id = 50700;
update tag set conversion_id = 225480 where tag_id = 51911;
update tag set conversion_id = 227726 where tag_id = 50634;
update tag set conversion_id = 224636 where tag_id = 146;
update tag set conversion_id = 225806 where tag_id = 51458;
update tag set conversion_id = 226459 where tag_id = 50501;
update tag set conversion_id = 228025 where tag_id = 256;
update tag set conversion_id = 225042 where tag_id = 176;
update tag set conversion_id = 224830 where tag_id = 51188;
update tag set conversion_id = 225425 where tag_id = 294;
update tag set conversion_id = 226974 where tag_id = 50995;
update tag set conversion_id = 225387 where tag_id = 51803;
update tag set conversion_id = 226974 where tag_id = 51120;
update tag set conversion_id = 224830 where tag_id = 51828;
update tag set conversion_id = 227637 where tag_id = 51888;
update tag set conversion_id = 226974 where tag_id = 51763;
update tag set conversion_id = 227637 where tag_id = 51961;
update tag set conversion_id = 226974 where tag_id = 50982;
update tag set conversion_id = 227637 where tag_id = 51244;
update tag set conversion_id = 227637 where tag_id = 52090;
update tag set conversion_id = 226957 where tag_id = 50553;
update tag set conversion_id = 227151 where tag_id = 51096;
update tag set conversion_id = 227041 where tag_id = 50776;
update tag set conversion_id = 226459 where tag_id = 52143;
update tag set conversion_id = 225489 where tag_id = 51580;
update tag set conversion_id = 227210 where tag_id = 52139;
update tag set conversion_id = 225108 where tag_id = 181;
update tag set conversion_id = 226399 where tag_id = 51826;
update tag set conversion_id = 227637 where tag_id = 19;

delete from factors;

insert into public.factors (factor_id, factor, to_unit, from_unit, conversion_id, reference_id, marker, unit_size, unit_default)
values  (1000, 113, 1013, 1049, 224636, 229563, null, null, false),
        (1001, 14.2, 1013, 1001, 224636, 229566, null, null, false),
        (1002, 227, 1013, 1000, 224636, 230168, null, null, false),
        (1003, 431, 1013, 1011, 226221, 228726, null, null, true),
        (1004, 140, 1013, 1000, 226221, 233529, 'cubed', null, true),
        (1005, 245, 1013, 1000, 227955, 229737, null, null, null),
        (1006, 245, 1013, 1000, 227955, 231392, null, null, false),
        (1007, 258, 1013, 1000, 226562, 232027, null, null, false),
        (1008, 16, 1013, 1001, 226562, 234461, null, null, false),
        (1018, 14.9, 1013, 1001, 224830, 4710, null, 'medium', false),
        (1019, 239, 1013, 1000, 224830, 4711, null, 'medium', false),
        (1020, 5, 1013, 1002, 224830, 4712, null, 'medium', false),
        (1024, 144, 1013, 1000, 225387, 5197, null, 'medium', false),
        (1025, 144, 1013, 1000, 225387, 5448, null, 'medium', false),
        (1030, 111, 1013, 1000, 225733, 1149, null, 'medium', false),
        (1031, 111, 1013, 1000, 225733, 5556, null, 'medium', false),
        (1036, 148, 1013, 1000, 226399, 2700, null, 'medium', false),
        (1037, 148, 1013, 1000, 226399, 5791, null, 'medium', false),
        (1038, 4.6, 1013, 1002, 226657, 2931, 'packed', 'medium', false),
        (1039, 220, 1013, 1000, 226657, 2932, 'packed', 'medium', false),
        (1040, 3, 1013, 1002, 226657, 2933, 'unpacked', 'medium', false),
        (1041, 145, 1013, 1000, 226657, 2934, 'unpacked', 'medium', false),
        (1046, 45, 1013, 1031, 227535, 4967, null, 'medium', false),
        (1047, 4, 1013, 1011, 227535, 7640, null, 'medium', true),
        (1050, 4.6, 1013, 1002, 224668, 7376, null, 'medium', false),
        (1052, 132, 1013, 1000, 225042, 2773, 'grated', 'medium', false),
        (1053, 132, 1013, 1000, 225042, 2774, 'grated', 'medium', false),
        (1054, 112, 1013, 1000, 225386, 1195, 'shredded', 'medium', false),
        (1055, 28.333333333333332, 1013, 1022, 225386, 1201, null, 'medium', false),
        (1058, 170, 1013, 1011, 228026, 4014, null, 'small', false),
        (1059, 150, 1013, 1000, 228026, 2288, 'diced', 'medium', false),
        (1060, 18, 1013, 1001, 228025, 4988, null, 'medium', false),
        (1061, 6, 1013, 1002, 228025, 4989, null, 'medium', false),
        (1062, 249, 1013, 1000, 226974, 4351, null, 'medium', false),
        (1063, 249, 1013, 1000, 226974, 6031, null, 'medium', false),
        (1064, 2, 1013, 1002, 225480, 1215, null, 'medium', false),
        (1065, 6.3, 1013, 1001, 225480, 1216, null, 'medium', false),
        (1066, 0.8, 1013, 1002, 227210, 7318, null, 'medium', false),
        (1067, 19, 1013, 1001, 227151, 3575, null, 'medium', false),
        (1068, 19, 1013, 1001, 227151, 6113, null, 'medium', false),
        (1069, 35, 1013, 1011, 227726, 3942, null, 'medium', true),
        (1070, 160, 1013, 1000, 227726, 2633, 'chopped', 'medium', true),
        (1071, 160, 1013, 1000, 227726, 2634, 'diced', 'medium', true),
        (1072, 170, 1013, 1000, 227726, 2635, 'sliced', 'medium', true),
        (1073, 6, 1013, 1022, 227726, 2657, null, 'medium', true),
        (1, 1, 1029, 1032, null, null, null, null, null),
        (2, 1, 1030, 1034, null, null, null, null, null),
        (3, 1, 1030, 1035, null, null, null, null, null),
        (4, 1, 1029, 1033, null, null, null, null, null),
        (5, 1, 1030, 1036, null, null, null, null, null),
        (6, 0.0422675, 1017, 1015, null, null, null, null, null),
        (7, 0.035195079727854, 1028, 1015, null, null, null, null, null),
        (8, 0.33814203, 1007, 1015, null, null, null, null, null),
        (9, 0.35195079727854, 1026, 1015, null, null, null, null, null),
        (10, 0.0026417205124156, 1005, 1015, null, null, null, null, null),
        (11, 0.0021996924829909, 1024, 1015, null, null, null, null, null),
        (12, 0.01, 1003, 1015, null, null, null, null, null),
        (13, 10, 1004, 1015, null, null, null, null, null),
        (14, 0.021133764, 1006, 1015, null, null, null, null, null),
        (15, 0.017597539863927, 1025, 1015, null, null, null, null, null),
        (16, 0.0105668821, 1010, 1015, null, null, null, null, null),
        (17, 0.0087987699319635, 1027, 1015, null, null, null, null, null),
        (18, 0.56312127564566, 1052, 1015, null, null, null, null, null),
        (19, 1.689363826937, 1051, 1015, null, null, null, null, null),
        (20, 16, 1001, 1000, null, null, null, null, null),
        (21, 48, 1002, 1000, null, null, null, null, null),
        (22, 8, 1007, 1017, null, null, null, null, null),
        (23, 0.0625, 1005, 1017, null, null, null, null, null),
        (24, 0.5, 1006, 1017, null, null, null, null, null),
        (25, 0.25, 1010, 1017, null, null, null, null, null),
        (26, 10, 1026, 1028, null, null, null, null, null),
        (27, 0.0625, 1024, 1028, null, null, null, null, null),
        (28, 0.5, 1025, 1028, null, null, null, null, null),
        (29, 0.25, 1027, 1028, null, null, null, null, null),
        (30, 0.125, 1017, 1007, null, null, null, null, null),
        (31, 0.0078125, 1005, 1007, null, null, null, null, null),
        (32, 0.0625, 1006, 1007, null, null, null, null, null),
        (33, 0.03125, 1010, 1007, null, null, null, null, null),
        (34, 128, 1007, 1005, null, null, null, null, null),
        (35, 160, 1026, 1024, null, null, null, null, null),
        (36, 1, 1013, 1013, null, null, null, null, null),
        (37, 0.001, 1014, 1013, null, null, null, null, null),
        (38, 0.0022, 1008, 1013, null, null, null, null, null),
        (39, 0.0352733686067019, 1009, 1013, null, null, null, null, null),
        (40, 1000, 1013, 1014, null, null, null, null, null),
        (41, 1, 1014, 1014, null, null, null, null, null),
        (42, 2.20462262, 1008, 1014, null, null, null, null, null),
        (43, 35.2733686067019, 1009, 1014, null, null, null, null, null),
        (44, 16, 1009, 1008, null, null, null, null, null),
        (45, 100, 1015, 1003, null, null, null, null, null),
        (46, 4.22675, 1017, 1003, null, null, null, null, null),
        (47, 3.5195079727854, 1028, 1003, null, null, null, null, null),
        (48, 33.8140227, 1007, 1003, null, null, null, null, null),
        (49, 35.195079727854, 1026, 1003, null, null, null, null, null),
        (50, 0.26417, 1005, 1003, null, null, null, null, null),
        (51, 0.21996924829909, 1024, 1003, null, null, null, null, null),
        (52, 1000, 1004, 1003, null, null, null, null, null),
        (53, 2.1133764, 1006, 1003, null, null, null, null, null),
        (54, 1.7597539863927, 1025, 1003, null, null, null, null, null),
        (55, 1.05668821, 1010, 1003, null, null, null, null, null),
        (56, 0.87987699319635, 1027, 1003, null, null, null, null, null),
        (57, 56.312127564566, 1052, 1003, null, null, null, null, null),
        (58, 168.9363826937, 1051, 1003, null, null, null, null, null),
        (59, 0.001, 1013, 1016, null, null, null, null, null),
        (60, 0.000001, 1014, 1016, null, null, null, null, null),
        (61, 0.0000022, 1008, 1016, null, null, null, null, null),
        (62, 1, 1016, 1016, null, null, null, null, null),
        (63, 0.00003527, 1009, 1016, null, null, null, null, null),
        (64, 0.1, 1015, 1004, null, null, null, null, null),
        (65, 0.00422675, 1017, 1004, null, null, null, null, null),
        (66, 0.0035195079727854, 1028, 1004, null, null, null, null, null),
        (67, 0.033814023, 1007, 1004, null, null, null, null, null),
        (68, 0.035195079727854, 1026, 1004, null, null, null, null, null),
        (69, 0.00026417, 1005, 1004, null, null, null, null, null),
        (70, 0.00021996924829909, 1024, 1004, null, null, null, null, null),
        (71, 0.001, 1003, 1004, null, null, null, null, null),
        (72, 0.0021133764, 1006, 1004, null, null, null, null, null),
        (73, 0.0017597539863927, 1025, 1004, null, null, null, null, null),
        (74, 0.0010566882, 1010, 1004, null, null, null, null, null),
        (75, 0.00087987699319635, 1027, 1004, null, null, null, null, null),
        (76, 0.056312127564567, 1052, 1004, null, null, null, null, null),
        (77, 0.1689363826937, 1051, 1004, null, null, null, null, null),
        (78, 0.0625, 1008, 1009, null, null, null, null, null),
        (79, 16, 1007, 1006, null, null, null, null, null),
        (80, 0.125, 1005, 1006, null, null, null, null, null),
        (81, 0.5, 1010, 1006, null, null, null, null, null),
        (82, 20, 1026, 1025, null, null, null, null, null),
        (83, 0.125, 1024, 1025, null, null, null, null, null),
        (84, 0.25, 1005, 1010, null, null, null, null, null),
        (85, 0.25, 1024, 1027, null, null, null, null, null),
        (86, 0.0625, 1000, 1001, null, null, null, null, null),
        (87, 3, 1002, 1001, null, null, null, null, null),
        (88, 0.0625, 1017, 1021, null, null, null, null, null),
        (89, 0.5, 1007, 1021, null, null, null, null, null),
        (90, 0.00390625, 1005, 1021, null, null, null, null, null),
        (91, 0.03125, 1006, 1021, null, null, null, null, null),
        (92, 0.015625, 1010, 1021, null, null, null, null, null),
        (93, 3, 1019, 1021, null, null, null, null, null),
        (94, 0.0625, 1028, 1052, null, null, null, null, null),
        (95, 0.625, 1026, 1052, null, null, null, null, null),
        (96, 0.003125, 1024, 1052, null, null, null, null, null),
        (97, 0.02500001, 1025, 1052, null, null, null, null, null),
        (98, 0.0125, 1027, 1052, null, null, null, null, null),
        (99, 0.020833333333, 1000, 1002, null, null, null, null, null),
        (100, 0.020833333333, 1000, 1002, null, null, null, null, null),
        (101, 0.33333333333, 1001, 1002, null, null, null, null, null),
        (102, 0.020833333333, 1017, 1019, null, null, null, null, null),
        (103, 0.1666666666, 1007, 1019, null, null, null, null, null),
        (104, 0.00130208333, 1005, 1019, null, null, null, null, null),
        (105, 0.0104166666666, 1006, 1019, null, null, null, null, null),
        (106, 0.00520833333333, 1010, 1019, null, null, null, null, null),
        (107, 0.3333333333, 1021, 1019, null, null, null, null, null),
        (108, 0.020833333333333, 1028, 1051, null, null, null, null, null),
        (109, 0.208333, 1026, 1051, null, null, null, null, null),
        (110, 0.00104167, 1024, 1051, null, null, null, null, null),
        (111, 0.00833334, 1025, 1051, null, null, null, null, null),
        (112, 0.00520833333333, 1027, 1051, null, null, null, null, null),
        (113, 0.125, 1002, 1053, null, null, null, null, null),
        (114, 0.04166666667, 1001, 1053, null, null, null, null, null),
        (115, 0.00260416667, 1000, 1053, null, null, null, null, null),
        (1009, 16, 1013, 1001, 227553, 228101, null, null, false),
        (1010, 250, 1013, 1000, 227553, 228102, null, null, false),
        (1014, 107, 1013, 1000, 225964, 2854, 'chopped', 'medium', false),
        (1015, 588, 1013, 1039, 225964, 3529, null, 'medium', false),
        (1016, 265, 1013, 1039, 225964, 3585, null, 'small', false),
        (1017, 840, 1013, 1039, 225964, 3586, null, 'large', false),
        (1021, 4.2, 1013, 1002, 225108, 3443, null, 'medium', false),
        (1022, 200, 1013, 1000, 225108, 3444, null, 'medium', false),
        (1023, 2.9, 1013, 1002, 226459, 2358, null, 'medium', false),
        (1026, 5, 1013, 1011, 225489, 4004, null, 'small', false),
        (1027, 15, 1013, 1011, 225489, 4009, null, 'medium', true),
        (1028, 6, 1013, 1001, 225489, 2809, 'chopped', 'medium', false),
        (1029, 100, 1013, 1000, 225489, 2810, 'chopped', 'medium', false),
        (1032, 125, 1013, 1000, 225806, 3298, null, 'medium', false),
        (1033, 125, 1013, 1000, 225806, 5585, null, 'medium', false),
        (1034, 125, 1013, 1000, 227041, 3476, null, 'medium', false),
        (1035, 125, 1013, 1000, 227041, 6061, null, 'medium', false),
        (1042, 100, 1013, 1000, 225425, 2206, 'sifted', 'medium', false),
        (1043, 8, 1013, 1001, 225425, 2207, 'unsifted', 'medium', false),
        (1044, 120, 1013, 1000, 225425, 2208, 'unsifted', 'medium', false),
        (1045, 2.5, 1013, 1002, 225425, 3445, null, 'medium', false),
        (1048, 16, 1013, 1001, 227300, 3495, null, 'medium', false),
        (1049, 16, 1013, 1001, 227300, 6174, null, 'medium', false),
        (1051, 4.6, 1013, 1002, 226873, 1385, null, 'medium', false),
        (1056, 369, 1013, 1011, 228026, 3951, null, 'large', false),
        (1057, 213, 1013, 1011, 228026, 3952, null, 'medium', true),
        (1074, 3, 1013, 1055, 227637, 1002, null, 'medium', false),
        (1075, 36, 1013, 1039, 227637, 1005, null, 'medium', true),
        (1076, 3, 1013, 1055, 227637, 234817, null, null, null),
        (1077, 136, 1013, 1000, 227637, 1725, null, 'medium', true),
        (1078, 2.8, 1013, 1002, 227637, 1726, null, 'medium', true),
        (1079, 3.1, 1013, 1002, 226957, 1029, null, 'medium', false),
        (1080, 9.7, 1013, 1001, 226957, 1030, null, 'medium', false);

-- food conversions, tomatoes
insert into public.food_conversions (conversion_id, food_id, fdc_id, amount, unit_name, gram_weight, unit_id, food_conversion_id, integral, marker, sub_amount, info, unit_size, unit_default)
values  (225744, 105738, 170457, 1, 'unit', 182, 1011, 3891, null, null, null, 'whole 3"" dia', 'large', false),
        (225744, 105738, 170457, 1, 'unit', 123, 1011, 3892, null, null, null, '2-3/5" dia', 'small', false),
        (225744, 105738, 170457, 1, 'unit', 148, 1011, 3960, null, null, null, null, 'medium', true),
        (225744, 105738, 170457, 1, 'unit', 91, 1011, 3976, null, null, null, '2-2/5" dia', 'small', false),
        (225744, 104748, 170457, 1, 'cup', 180, 1000, 1163, null, 'chopped', null, null, 'medium', false),
        (225744, 105738, 170457, 1, 'slice', 27, 1022, 2544, null, null, null, '1/2" thick', 'medium', false),
        (225744, 104539, 170457, 1, 'wedge', 31, 1050, 2835, null, 'wedge', null, '1/4 tomato', 'medium', false),
        (225744, 105738, 170457, 1, 'slice', 20, 1022, 3606, null, null, null, '1/4" thick', 'medium', false);

-- factors, conversions
insert into public.factors (factor_id, factor, to_unit, from_unit, conversion_id, reference_id, marker, unit_size, unit_default, tag_id)
values  (1081, 182, 1013, 1011, 225744, 3891, null, 'large', false, null),
        (1082, 123, 1013, 1011, 225744, 3892, null, 'small', false, null),
        (1083, 148, 1013, 1011, 225744, 3960, null, 'medium', true, null),
        (1084, 91, 1013, 1011, 225744, 3976, null, 'small', false, null),
        (1085, 180, 1013, 1000, 225744, 1163, 'chopped', 'medium', false, null),
        (1086, 27, 1013, 1022, 225744, 2544, null, 'medium', false, null),
        (1087, 31, 1013, 1050, 225744, 2835, 'wedge', 'medium', false, null),
        (1088, 20, 1013, 1022, 225744, 3606, null, 'medium', false, null);

-- add conversion to tomato
update tag set conversion_id = 225744 where tag_id = 33;
