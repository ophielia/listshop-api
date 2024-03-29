-- now the data
insert into dish_items (dish_item_id, dish_id, tag_id)
select nextval('dish_item_sequence'), dish_id, tag_id
from dish_tags;


insert into public.units (unit_id, type, subtype, name, is_liquid, is_list_unit, is_dish_unit, is_weight, is_volume)
values (1000, 'US', 'VOLUME', 'cup', false, false, true, false, false),
       (1001, 'HYBRID', 'NONE', 'tablespoon', false, false, true, false, false),
       (1002, 'HYBRID', 'NONE', 'teaspoon', false, false, true, false, false),
       (1003, 'METRIC', 'VOLUME', 'liter', false, true, true, false, false),
       (1004, 'METRIC', 'VOLUME', 'milliliter', false, true, true, false, false),
       (1005, 'US', 'VOLUME', 'gallon', false, true, false, false, false),
       (1006, 'US', 'VOLUME', 'pint', false, false, true, false, false),
       (1007, 'US', 'VOLUME', 'fl oz', false, true, false, false, false),
       (1008, 'US', 'WEIGHT', 'lb', false, true, true, false, false),
       (1009, 'US', 'WEIGHT', 'oz', false, true, true, false, false),
       (1010, 'US', 'VOLUME', 'quart', false, true, true, false, false),
       (1011, 'UNIT', 'NONE', 'unit', false, true, true, false, false),
       (1013, 'METRIC', 'WEIGHT', 'gram', false, true, true, false, false),
       (1014, 'METRIC', 'WEIGHT', 'kilogram', false, true, true, false, false),
       (1015, 'METRIC', 'VOLUME', 'centiliter', false, false, true, false, false),
       (1016, 'METRIC', 'WEIGHT', 'milligram', false, true, true, false, false);

insert into public.factors (factor_id, factor, to_unit, from_unit)
values (1927, 0.0625, 1005, 1000),
       (1928, 0.5, 1006, 1000),
       (1929, 0.25, 1010, 1000),
       (1930, 0.125, 1000, 1007),
       (1931, 0.0078125, 1005, 1007),
       (1932, 0.0625, 1006, 1007),
       (1933, 0.03125, 1010, 1007),
       (1934, 0.001, 1014, 1013),
       (1935, 0.0022, 1008, 1013),
       (1936, 0.0352733686067019, 1009, 1013),
       (1937, 2.20462262, 1008, 1014),
       (1938, 35.2733686067019, 1009, 1014),
       (1939, 100, 1015, 1003),
       (1940, 4.22675, 1000, 1003),
       (1941, 0.26417, 1005, 1003),
       (1942, 1000, 1004, 1003),
       (1943, 2.1133764, 1006, 1003),
       (1944, 1.05668821, 1010, 1003),
       (1945, 0.00422675, 1000, 1004),
       (1946, 0.00026417, 1005, 1004),
       (1947, 0.0021133764, 1006, 1004),
       (1948, 0.0010566882, 1010, 1004),
       (1949, 0.125, 1005, 1006),
       (1950, 0.5, 1010, 1006),
       (1951, 0.25, 1005, 1010),
       (1952, 0.0000022, 1008, 1016),
       (1953, 0.00003527, 1009, 1016),
       (1954, 0.0105668821, 1010, 1015),
       (1955, 0.0422675, 1000, 1015),
       (1956, 0.0026417205124156, 1005, 1015),
       (1957, 0.021133764, 1006, 1015),
       (1958, 0.001, 1003, 1004),
       (1959, 0.1, 1015, 1004),
       (1960, 0.01, 1003, 1015),
       (1961, 10, 1004, 1015),
       (1962, 0.0625, 1008, 1009),
       (1963, 16, 1009, 1008);
