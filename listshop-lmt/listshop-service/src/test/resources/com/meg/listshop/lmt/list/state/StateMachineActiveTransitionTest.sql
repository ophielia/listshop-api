delete from user_properties where user_id = 34 and property_key = 'preferred_domain';
insert into user_properties (user_property_id, user_id, property_key, property_value, is_system)
values (nextval('user_properties_id_seq'), 34,
        'preferred_domain', 'METRIC', null);


delete from user_properties where user_id = 20 and property_key = 'preferred_domain';
insert into user_properties (user_property_id, user_id, property_key, property_value, is_system)
values (nextval('user_properties_id_seq'), 20,
        'preferred_domain', 'US', null);
