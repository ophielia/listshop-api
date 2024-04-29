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

-- tomatoes
insert into factors (conversion_id, factor_id, factor, from_unit, to_unit, marker)
values  (127791, 127791, 20, 1022, 1013, 'sliced'),
        (127791, 1277912, 149, 1000, 1013, null),
        (127791, 1277914, 180, 1000, 1013, 'chopped'),
        (127791, 1277915, 62, 1011, 1013, null);