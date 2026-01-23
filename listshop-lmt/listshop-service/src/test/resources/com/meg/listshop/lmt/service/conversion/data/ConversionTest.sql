insert into public.factors (factor_id, factor, to_unit, from_unit,conversion_id)
values  (1111207, 226.8, 1013, 1000, 348);

-- onions
insert into public.factors (conversion_id, factor_id, factor, from_unit, to_unit,marker)
values  (56630,566300, 38, 1011, 1013,  'sliced'        )    ,
         (56630,566301, 160, 1000,1013, 'chopped'   )   ,
          (56630,566302, 115, 1000,1013, 'sliced'   )  ,
           (56630,566303, 10, 1001, 1013, 'chopped' ) ,
            (56630,566304, 9,  1022, 1013, 'sliced' ),
             (56630,566305, 110, 1011,1013,  null   )     ;

-- butter
insert into factors (conversion_id, factor_id, factor, from_unit, to_unit, marker)
values  (87209, 87209, 14.2, 1001, 1013, null),
        (87209, 87210, 227, 1000, 1013, null),
        (87209, 87111, 113, 1049, 1013, null);

-- cheddar cheese
insert into factors (conversion_id, factor_id, factor, from_unit, to_unit)
values  (95915, 95915, 224, 1000, 1013),
        (95915, 959151, 21, 1022, 1013);

-- tomatoes - deleting and readding
delete from factors where conversion_id = 225744;
delete from food_conversions where conversion_id = 225744;
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

-- chicken drumstick
insert into factors (factor_id, factor, to_unit, from_unit, conversion_id, reference_id, marker, unit_size, unit_default)
values (1064222, 88, 1013, 1011, 227959, 106417, null, null, false);
