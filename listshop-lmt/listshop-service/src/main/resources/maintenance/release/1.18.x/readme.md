Steps
1. run "garlic_clove" script
2. run "handling_unit_sizes" script
3. run "unit_default"
4. run "add_new_column" script
5. run scripts to add owner
6. run scripts to update ownership
7. delete flyway_schema_history table (export first, maybe, as inserts)
8. from maven, run maven baseline (mvn clean flyway:baseline )
