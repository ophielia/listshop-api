insert into public.factors (factor_id, factor, to_unit, from_unit,conversion_id)
values  (1111207, 226.8, 1013, 1000, 348);


insert into public.factors (conversion_id, factor_id, factor, from_unit, to_unit,marker)
values  (56630,566300, 38, 1011, 1013,  'sliced'        )    ,
         (56630,566301, 160, 1000,1013, 'chopped'   )   ,
          (56630,566302, 115, 1000,1013, 'sliced'   )  ,
           (56630,566303, 10, 1001, 1013, 'chopped' ) ,
            (56630,566304, 9,  1022, 1013, 'sliced' ),
             (56630,566305, 110, 1011,1013,  null   )     ;


