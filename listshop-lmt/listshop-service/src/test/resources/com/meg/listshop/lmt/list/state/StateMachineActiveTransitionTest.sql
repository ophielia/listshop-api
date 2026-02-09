delete from user_properties where user_id = 34 and property_key = 'preferred_domain';
insert into user_properties (user_property_id, user_id, property_key, property_value, is_system)
values (nextval('user_properties_id_seq'), 34,
        'preferred_domain', 'METRIC', null);


delete from user_properties where user_id = 20 and property_key = 'preferred_domain';
insert into user_properties (user_property_id, user_id, property_key, property_value, is_system)
values (nextval('user_properties_id_seq'), 20,
        'preferred_domain', 'US', null);

delete from factors where conversion_id = 112225744;
insert into public.factors (factor_id, factor, to_unit, from_unit, conversion_id, reference_id, marker, unit_size, unit_default, tag_id)
values  (1121081, 182, 1013, 1011, 112225744, 3891, null, 'large', false, null),
        (1121082, 123, 1013, 1011, 112225744, 3892, null, 'small', false, null),
        (1121083, 148, 1013, 1011, 112225744, 3960, null, 'medium', true, null),
        (1121084, 91, 1013, 1011, 112225744, 3976, null, 'small', false, null),
        (1121085, 180, 1013, 1000, 112225744, 1163, 'chopped', 'medium', false, null),
        (1121086, 27, 1013, 1022, 112225744, 2544, null, 'medium', false, null),
        (1121087, 31, 1013, 1050, 112225744, 2835, 'wedge', 'medium', false, null),
        (1121088, 20, 1013, 1022, 112225744, 3606, null, 'medium', false, null);

-- add conversion to tomato
update tag set conversion_id = 112225744 where tag_id = 33;
