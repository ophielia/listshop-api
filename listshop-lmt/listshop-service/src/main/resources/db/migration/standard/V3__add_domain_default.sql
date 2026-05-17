/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

ALTER TABLE units
    ADD COLUMN if not exists domain_default boolean DEFAULT false;

-- add bridge view
create or replace view bridge_factors
            (factor_id, factor, to_unit, from_unit, conversion_id, reference_id, marker, unit_size, unit_default,
             tag_id) as
SELECT DISTINCT tou.unit_id * 1000000 + fr.unit_id AS factor_id,
                t.factor / f.factor                AS factor,
                tou.unit_id                        AS to_unit,
                fr.unit_id                         AS from_unit,
                t.conversion_id                    AS conversion_id,
                NULL::bigint                       AS reference_id,
                t.marker                           AS marker,
                t.unit_size                        AS unit_size,
                t.unit_default                      AS unit_default,
                NULL::bigint                       AS tag_id
FROM factors f
         JOIN units fr ON fr.unit_id = f.to_unit
         JOIN units b ON b.unit_id = f.from_unit
         JOIN factors t ON b.unit_id = t.from_unit
         JOIN units tou ON t.to_unit = tou.unit_id;

create or replace view unit_factors
            (factor_id,factor, conversion_id, from_unit, from_name, from_marker, from_unit_size, from_unit_default, to_unit, to_name, to_marker, to_unit_size, to_unit_default)
as
select distinct tou.unit_id * 1000000 + fr.unit_id AS factor_id,
       f.factor / b.factor                as factor,
       b.conversion_id                    AS conversion_id,
       fr.unit_id                         AS from_unit,
       fr.name                            AS from_name,
       f.marker                           AS from_marker,
       f.unit_size                        AS from_unit_size,
       f.unit_default                     AS from_unit_default,
       tou.unit_id                        AS to_unit,
       tou.name                           AS to_name,
       b.marker                           AS to_marker,
       b.unit_size                        AS to_unit_size,
       b.unit_default                     AS to_unit_default
from factors f
         join units fr on fr.unit_id = f.from_unit
         JOIN factors b ON b.to_unit = f.to_unit and
                           ((b.conversion_id = f.conversion_id) or
                            (b.conversion_id is null and f.conversion_id is null))
         join units tou on tou.unit_id = b.from_unit
where b.conversion_id is not null;
