
alter table foods add column has_factor bool;

-- reset sequence for factors to start from 1000
SELECT setval('factor_sequence', 999, true);

