delete
from domain_unit;

select *
from domain_unit;
-- metric
insert into domain_unit
select nextval('domain_unit_sequence'), 'METRIC', unit_id
from units
where type in ('METRIC', 'UNIT','HYBRID');
-- us
insert into domain_unit
select nextval('domain_unit_sequence'), 'US', unit_id
from units
where type in ('US', 'UNIT','HYBRID');
--uk
insert into domain_unit
select nextval('domain_unit_sequence'),
       'UK',
       unit_id
from units
where type = 'UK';
insert into domain_unit
select nextval('domain_unit_sequence'),
       'UK',
       unit_id
from units
where type = 'METRIC'
  and is_liquid = false;
insert into domain_unit
select nextval('domain_unit_sequence'),
       'UK',
       unit_id
from units
where type in ('UNIT', 'HYBRID');

