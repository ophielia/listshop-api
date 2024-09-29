Steps
1. run "garlic_clove" script
2. run scripts to add owner
2. run scripts to update ownership
3. delete flyway_schema_history table (export first, maybe, as inserts)
4. from maven, run maven baseline (mvn clean flyway:baseline )
