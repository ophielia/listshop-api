/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

insert into factors (factor_id, from_unit, to_unit, factor) select 116, f.unit_id, t.unit_id, 1 as factor from units f,units t where lower(f.name) = lower('gram') and f.type = 'METRIC' and lower(t.name) = lower('gram' and t.type = 'UK');
insert into factors (factor_id, from_unit, to_unit, factor) select 117, f.unit_id, t.unit_id,  as factor from units f,units t where lower(f.name) = lower('kilogram') and f.type = 'METRIC' and lower(t.name) = lower('kilogram' and t.type = 'UK');
insert into factors (factor_id, from_unit, to_unit, factor) select 118, f.unit_id, t.unit_id,  as factor from units f,units t where lower(f.name) = lower('milligram') and f.type = 'METRIC' and lower(t.name) = lower('milligram' and t.type = 'UK');
