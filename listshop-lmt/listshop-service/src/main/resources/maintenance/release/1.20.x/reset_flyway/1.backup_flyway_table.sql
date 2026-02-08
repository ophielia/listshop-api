drop table if exists oow_flyway_schema_history;

create table oow_flyway_schema_history as
    select * from flyway_schema_history;

drop table if exists flyway_schema_history;


