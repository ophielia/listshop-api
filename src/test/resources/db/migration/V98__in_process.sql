-- change list statistic


alter table list_tag_stats
  alter column added_count
    set default 0;

alter table list_tag_stats
  alter column removed_count
    set default 0;

update list_tag_stats
set added_count = 0
where added_count is null;

update list_tag_stats
set removed_count = 0
where removed_count is null;