Steps
1. run "garlic_clove" script
2. run "fix_layout_dups" script
3. run "handling_unit_sizes" script
4. run "unit_default"
5. run "add_new_column" script
6. run scripts to add owner
7. run scripts to update ownership
8. delete flyway_schema_history table (export first, maybe, as inserts)
9. from maven, run maven baseline (mvn clean flyway:baseline )


insert into public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) values (1, '1', 'baseline migration', 'BASELINE', 'baseline migration', null, 'bankuser', '2021-06-07 10:01:18.132379', 0, true);
insert into public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) values (2, '2', 'add tokens', 'SQL', 'V2__add_tokens.sql', 726801213, 'bankuser', '2022-02-23 05:27:05.695307', 72, true);
insert into public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) values (3, '3', 'add constraints', 'SQL', 'V3__add_constraints.sql', -402899688, 'bankuser', '2022-02-23 05:27:05.818716', 157, true);
insert into public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) values (4, '4', 'tag rework', 'SQL', 'V4__tag_rework.sql', 1036124140, 'bankuser', '2022-05-25 05:53:46.671387', 110, true);
insert into public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) values (5, '5', 'user properties', 'SQL', 'V5__user_properties.sql', 94724182, 'bankuser', '2022-06-24 17:14:21.343773', 48, true);
insert into public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) values (6, '6', 'list layout changes', 'SQL', 'V6__list_layout_changes.sql', 728658502, 'bankuser', '2022-11-19 14:17:32.657397', 132, true);
insert into public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) values (7, '7', 'beta campaign table', 'SQL', 'V7__beta_campaign_table.sql', 113520698, 'bankuser', '2023-07-12 04:46:04.766868', 42, true);
insert into public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) values (8, '8', 'dishitems', 'SQL', 'V98__dishitems.sql', 225504286, 'bankuser', '2023-12-15 17:41:39.491782', 43, true);
insert into public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) values (9, '9', 'dishitems data', 'SQL', 'V99__dishitems_data.sql', 1762110755, 'bankuser', '2023-12-15 17:41:39.556695', 69, true);
insert into public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) values (10, '10', 'food tables', 'SQL', 'V10__food_tables.sql', -726295785, 'bankuser', '2024-03-05 05:35:34.526802', 84, true);
