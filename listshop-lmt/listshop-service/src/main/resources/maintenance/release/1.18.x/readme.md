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
