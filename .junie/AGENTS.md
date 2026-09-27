# Agent Guidelines for List Shop API

This document provides context and instructions for AI agents (like Junie, Claude, or others) working on the List Shop API project.

## Project Overview
The List Shop API is a backend service for managing shopping lists, meal plans, and recipe-to-list conversions. It handles complex categorization and unit conversion logic to ensure shopping lists are organized and accurate.

## Tech Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.5.7
- **Build System**: Maven (multi-module)
- **Database**: PostgreSQL (Migrations via Flyway)
- **Core Libraries**: Spring Data JPA, Spring Security, Jakarta Persistence.

## Project Structure
- `listshop-api`: Main API entry points and controllers.
- `listshop-lmt`: (List Management Tools) Core logic for lists, layouts, and tags.
    - `listshop-service`: Business logic (Services).
    - `listshop-data`: Persistence layer (Entities, Repositories).
- `listshop-conversion`: Specialized service for unit conversions (e.g., volume to weight).
- `common`: Shared utilities and DTOs.
- `postoffice`: Email and notification service.

## Core Domain Concepts

### 1. Tags and Categories
- **Tags**: Represent items (e.g., "Carrot", "Milk"). Tags can be hierarchical.
- **Categories**: Used for list organization (e.g., "Produce", "Dairy").
- **Mapping**: Tags are mapped to Categories via `CategoryTagMapping` (a Java record).

### 2. List Layouts
The project is currently transitioning from a legacy layout system to a newer version (V2).
- **LegacyLayoutServiceImpl**: The older implementation.
- **V2LayoutServiceImpl**: The new implementation focusing on more flexible mappings and DTO-based returns.
- **BaseLayoutServiceImpl**: Contains shared logic for both implementations.

### 3. Conversion Service
Used to convert between different units or domains.
- **ConverterProcessor**: Base interface for conversion logic.
- **TagConverterProcessor / DomainConverterProcessor**: Specialized processors.

## V2 services / endpoints
- A V2 has recently been implemented.  It's made up of new endpoints in V2 controllers calling new V2 services.
- The two goals of V2 are
  - provide amounts for shopping lists and dishes
  - clean out unnecessary fields from json returned from endpoints
- V2 has been implemented, but can expect changes as clients are actually implementing V2 endpoints and finding bugs
- legacy endpoints are still used by clients and can not yet be removed 

## Key Files for Navigation
- `listshop-conversion/conversion-service/src/main/java/com/meg/listshop/conversion/service/ConverterServiceImpl.java`: Central conversion logic.

## Guidelines for Agents
1. **Prefer V2 Services**: When working on list layouts, prefer `V2LayoutServiceImpl` unless specifically told otherwise.
2. **Database Changes**: All schema changes must be done via Flyway migrations in the `db` or module-specific migration folders.
3. **Data Records**: Use Java Records (like `CategoryTagMapping`) for immutable data carriers where appropriate.
4. **Context Window**: Be mindful of the large `test.json` file; it contains sample layout data but can be heavy.

## Typical Tasks
- Migrating legacy layout logic to V2.
- Adding new conversion factors or processors.
- Updating tag/category mapping logic.
- Refining the Shopping List REST API.
