   ---
name: db-migrations
description: Guidelines for PostgreSQL schema changes and Flyway migrations
   ---
# Database Migration Guidelines
## Flyway Migrations

This project maintains two independent sets of Flyway migrations.

### Live Migrations

- Located in the main application resources.
- Versioned beginning with `V1`.
- Represent the production database schema.
- These migrations are considered immutable release artifacts.

### Test Migrations

- Located in the test resources.
- Versioned beginning with `V100`.
- Executed after the live migrations when integration tests run using Testcontainers.
- Used for:
    - Test data.
    - Development-only schema changes.
    - New columns, tables, indexes, constraints, and other schema changes under active development.

## Development Workflow

During normal development:

- **Never modify an existing live migration.**
- **Never add new schema changes to the live migration directory.**
- The test migration directory is the only location where schema evolution should occur during normal development.
- Every schema change must be implemented as a **new Flyway migration** in the test migration directory using the next available `V100+` version.
- Test data should also be added using new test migrations rather than modifying existing ones.

When running integration tests, Testcontainers first applies the live migrations and then applies the test migrations, producing the complete development schema.

During the release process, test migrations are manually reviewed and promoted into the live migration history. This promotion is intentionally performed by a developer and must never be automated.

## Migration Rules

- Database migrations are immutable.
- Never edit, rename, reorder, or delete an existing migration that has been committed.
- If a previous migration contains an error, create a new migration that corrects it.
- Migrations should be forward-only.
- Each migration should perform a single logical change whenever practical.
- Migration filenames and versions must remain sequential and follow the project's naming conventions.

## AI Agent Rules

When making database changes:

- Always check for existing migrations before creating a new one.
- Create a **new test migration** for every schema change.
- Never create new migrations in the live migration directory.
- Never modify, rename, delete, reorder, or regenerate existing migrations.
- Never move migrations between the test and live migration directories.
- Never attempt to promote test migrations into the live migration history.
- Never introduce a new persistence framework.
- Reuse existing repositories and data-access classes whenever possible.
- If uncertain whether a change requires a migration, ask before proceeding.
- Prefer extending the existing schema over redesigning it. 
