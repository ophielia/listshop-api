# Project Overview
This project is a Spring Boot application.


# Database

## General Principles

- PostgreSQL is the system of record.
- The database schema should accurately model the business domain.
- Favor normalized schemas unless there is a demonstrated performance benefit to denormalization.
- All schema changes must be implemented using Flyway migrations.
- Existing data should be preserved whenever possible.

## Schema Design

- Use descriptive, singular table names.
- Primary keys should be immutable.
- Define foreign key constraints for all relationships.
- Create indexes only when they support actual query patterns.
- Avoid storing derived or duplicate data unless explicitly required.

## Queries

- Prefer repository or data-access classes over embedding SQL throughout the application.
- Keep queries simple, readable, and efficient.
- Retrieve only the columns required by the caller.
- Avoid `SELECT *` in production code.
- Avoid N+1 query patterns.
- Use transactions whenever multiple updates must succeed or fail together.

