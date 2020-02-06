-- fix statistics

ALTER TABLE LIST_TAG_STATS
    ADD COLUMN ADDED_SINGLE        BIGINT DEFAULT 0,
    ADD COLUMN ADDED_DISH          BIGINT DEFAULT 0,
    ADD COLUMN ADDED_LIST          BIGINT DEFAULT 0,
    ADD COLUMN ADDED_STARTERLIST   BIGINT DEFAULT 0,
    ADD COLUMN REMOVED_SINGLE      BIGINT DEFAULT 0,
    ADD COLUMN REMOVED_DISH        BIGINT DEFAULT 0,
    ADD COLUMN REMOVED_LIST        BIGINT DEFAULT 0,
    ADD COLUMN REMOVED_STARTERLIST BIGINT DEFAULT 0;

-- config table for view

create table list_stat_configs
(
    added_dish_factor          int,
    added_single_factor        int,
    added_list_factor          int,
    added_starterlist_factor   int,
    removed_dish_factor        int,
    removed_single_factor      int,
    removed_list_factor        int,
    removed_starterlist_factor int,
    frequent_threshold         float
);

-- configuration of statistics
INSERT INTO list_stat_configs
(added_dish_factor,
 added_single_factor,
 added_list_factor,
 added_starterlist_factor,
 removed_dish_factor,
 removed_single_factor,
 removed_list_factor,
 removed_starterlist_factor,
 frequent_threshold)
VALUES (25,
        25,
        25,
        25,
        20,
        40,
        20,
        20,
        .80);

-- create view with calculated statistics
CREATE OR REPLACE VIEW calculated_stats AS
SELECT tag_id,
       user_id,
       c.frequent_threshold,
       Cast(((removed_single * removed_single_factor) + (
           removed_dish * removed_dish_factor) + (
                 removed_list * removed_list_factor) + (
                 removed_starterlist * removed_starterlist_factor)) AS
           DECIMAL) /
       Cast((
               (added_single * added_single_factor) +
               (added_dish * added_dish_factor)
               +
               (
                   added_list * added_list_factor) + (
                   added_starterlist * added_starterlist_factor)) AS DECIMAL) as factored_frequency
FROM list_tag_stats,
     list_stat_configs c
where (
              (added_single * added_single_factor) +
              (added_dish * added_dish_factor)
              +
              (
                  added_list * added_list_factor) + (
                  added_starterlist * added_starterlist_factor)) > 0;

-- ROLLBACK
-- ALTER TABLE LIST_TAG_STATS
--     DROP COLUMN ADDED_SINGLE ,
--     DROP COLUMN ADDED_DISH ,
--     DROP COLUMN ADDED_LIST ,
--     DROP COLUMN ADDED_STARTERLIST ,
--     DROP COLUMN REMOVED_SINGLE ,
--     DROP COLUMN REMOVED_DISH ,
--     DROP COLUMN REMOVED_LIST ,
--     DROP COLUMN REMOVED_STARTERLIST ;

-- drop table list_stat_configs;