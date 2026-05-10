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
