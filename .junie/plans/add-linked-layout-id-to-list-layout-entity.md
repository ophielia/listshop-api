---
sessionId: session-260628-175802-1uqx
---

# Requirements

### Overview & Goals
The goal of this task is to add a new field `linkedCategoryId` (type `Long`) to the `ListLayoutCategoryEntity` class, similar to the `linkedLayoutId` field added to `ListLayoutEntity`. This change includes updating the entity, the `LayoutCategoryDTO`, and the database schema.

### Scope
- **In Scope**:
    - Adding `linkedCategoryId` to `ListLayoutCategoryEntity`.
    - Adding `linkedCategoryId` to `LayoutCategoryDTO`.
    - Updating `LayoutCategoryDTO` constructors and `toString` method.
    - Updating `V2LayoutServiceImplMockTest` to reflect DTO constructor changes.
    - Updating the Flyway SQL migration script `V107__add_linked_layout_id.sql` to include the new column.
- **Out of Scope**:
    - Updating API models (`ListLayoutCategory` V1/V2).
    - Updating model mappers.
    - Updating repository native queries.
    - Implementing logic that uses this field.

# Technical Design

### Current Implementation
`ListLayoutCategoryEntity` maps to the `list_category` table. `LayoutCategoryDTO` is used to transfer category data and is populated either from the entity or via native queries in `ListLayoutCategoryRepository`.

### Proposed Changes

#### Entity & Data Layer
- **ListLayoutCategoryEntity.java**: 
    - Add `Long linkedCategoryId` field.
    - Annotate with `@Column(name = "linked_category_id")`.
    - Add getter and setter.
- **LayoutCategoryDTO.java**: 
    - Add `Long linkedCategoryId` field.
    - Update both constructors to include/populate `linkedCategoryId`.
    - Update `toString` to include the new field.

#### Database
- **V107__add_linked_layout_id.sql**:
    - Append the following SQL:
      ```sql
      ALTER TABLE list_category ADD COLUMN linked_category_id bigint;
      ```

#### Tests
- **V2LayoutServiceImplMockTest.java**: Update `new LayoutCategoryDTO(...)` calls to include a `null` (or appropriate) value for the new `linkedCategoryId` parameter.

### File Structure
- `listshop-lmt/listshop-data/src/main/java/com/meg/listshop/lmt/data/entity/ListLayoutCategoryEntity.java` (Modified)
- `listshop-lmt/listshop-data/src/main/java/com/meg/listshop/lmt/data/pojos/LayoutCategoryDTO.java` (Modified)
- `listshop-lmt/listshop-service/src/test/resources/db/migration/test/V107__add_linked_layout_id.sql` (Modified)
- `listshop-lmt/listshop-service/src/test/java/com/meg/listshop/lmt/service/impl/V2LayoutServiceImplMockTest.java` (Modified)

# Delivery Steps

### ✓ Step 1: Update Entity and DTO
Add `linkedCategoryId` to `ListLayoutCategoryEntity` and `LayoutCategoryDTO`.
- Update `ListLayoutCategoryEntity` with the new field and getter/setter.
- Update `LayoutCategoryDTO` with the new field, constructors, and `toString`.

### ✓ Step 2: Update Flyway Migration
Append the `ALTER TABLE` statement for `list_category` to `V107__add_linked_layout_id.sql`.

### ✓ Step 3: Update Tests and Verify
Update `V2LayoutServiceImplMockTest` and verify the project compiles.