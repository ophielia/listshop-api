---
sessionId: session-260919-121415-1nqm
---

# Requirements

### Overview & Goals
The goal is to migrate existing meal plan tests to a legacy version and implement new tests for the v2 meal plan controller. The v2 implementation differs from the legacy version by returning unwrapped data models instead of HATEOAS `Resource` objects.

### Scope
- **Legacy Migration**: Rename existing `MealPlanRestControllerTest` and its SQL files to `LegacyMealPlanRestControllerTest`.
- **V2 Implementation Fixes**: Update the v2 controller mapping to `/v2/mealplan` and remove `Resource` wrappers from the implementation to ensure tests can pass.
- **V2 Testing**: Create `NewMealPlanRestControllerTest` and associated SQL files, updating all assertions to match the new unwrapped JSON structure.

### User Stories
- As a developer, I want the legacy tests to remain functional after renaming so that I can ensure backward compatibility.
- As a developer, I want the new v2 tests to verify the simplified JSON structure so that the API remains clean and efficient.

### Functional Requirements
- Legacy tests must continue to hit the `/mealplan` endpoint.
- New tests must hit the `/v2/mealplan` endpoint.
- New tests must assert direct access to properties (e.g., `meal_plan_id` instead of `meal_plan.meal_plan_id`).
- The v2 controller must be updated to return direct models and correct location headers.

# Technical Design

### Current Implementation
- **Legacy Controller**: `LegacyMealPlanRestController` implements `LegacyMealPlanRestControllerApi`, mapped to `/mealplan`, returning `MealPlanResource`.
- **V2 Controller**: `MealPlanRestController` implements `MealPlanRestControllerApi`, currently mapped to `/mealplan`, inconsistently returning both `MealPlan` and `MealPlanResource`.
- **Existing Test**: `MealPlanRestControllerTest` asserts properties wrapped in `meal_plan` or `_embedded`.

### Key Decisions
- **URL Mapping**: V2 will use `/v2/mealplan` to avoid conflicts with legacy endpoints, following the project's V2 convention.
- **Data Format**: V2 will strictly return `MealPlan` and `MealPlanList` without `Resource` wrappers.
- **Separate SQL Files**: `NewMealPlanRestControllerTest` will have its own copy of SQL data files to allow independent evolution of tests.

### Proposed Changes

#### Controller Fixes
- **`MealPlanRestControllerApi`**: Change `@RequestMapping` to `/v2/mealplan`.
- **`MealPlanRestController`**: 
    - Replace `ResponseEntity<Object>` (wrapping `MealPlanResource`) with `ResponseEntity<MealPlan>` or `ResponseEntity<Void>` with location header.
    - Fix `createMealPlan` to return 201 Created with a URI built from the request path.

#### Test Changes
- **Rename Legacy**:
    - `MealPlanRestControllerTest.java` -> `LegacyMealPlanRestControllerTest.java`
    - `MealPlanRestControllerTest.sql` -> `LegacyMealPlanRestControllerTest.sql`
- **New V2 Test**:
    - Base URL: `/v2/mealplan`
    - Path updates:
        - `meal_plan.meal_plan_id` -> `meal_plan_id`
        - `_embedded.mealPlanResourceList` -> `meal_plan_list`
        - `ratingUpdateInfo.dish_ratings` -> `dish_ratings`

### File Structure
- `listshop-lmt/listshop-service/src/test/java/com/meg/listshop/lmt/api/`
    - `LegacyMealPlanRestControllerTest.java` (Renamed)
    - `NewMealPlanRestControllerTest.java` (New)
- `listshop-lmt/listshop-service/src/test/resources/sql/com/meg/atable/lmt/api/`
    - `LegacyMealPlanRestControllerTest.sql` (Renamed)
    - `NewMealPlanRestControllerTest.sql` (New)

# Testing

### Validation Approach
Verification will be performed by ensuring the new tests pass against the updated V2 controller.

### Key Scenarios
- **Single Meal Plan**: `GET /v2/mealplan/{id}` returns `MealPlan` object directly.
- **Meal Plan List**: `GET /v2/mealplan` returns `MealPlanList` with `meal_plan_list` array.
- **Creation**: `POST /v2/mealplan` returns 201 Created with `Location` header pointing to `/v2/mealplan/{id}`.
- **Ratings**: `GET /v2/mealplan/{id}/ratings` returns `RatingUpdateInfo` with `dish_ratings` property directly.

### Edge Cases
- **Not Found**: `GET /v2/mealplan/{invalidId}` returns 404.
- **Unauthorized**: Requests without valid token return 401/403.
- **Incorrect User**: Accessing another user's meal plan returns 403.

# Delivery Steps

### ✓ Step 1: Migrate Legacy Meal Plan Tests
Rename `MealPlanRestControllerTest.java` and its associated SQL files to include the "Legacy" prefix.

- Rename `listshop-lmt/listshop-service/src/test/java/com/meg/listshop/lmt/api/MealPlanRestControllerTest.java` to `LegacyMealPlanRestControllerTest.java`.
- Rename `listshop-lmt/listshop-service/src/test/resources/sql/com/meg/atable/lmt/api/MealPlanRestControllerTest.sql` to `LegacyMealPlanRestControllerTest.sql`.
- Rename `listshop-lmt/listshop-service/src/test/resources/sql/com/meg/atable/lmt/api/MealPlanRestControllerTest_rollback.sql` to `LegacyMealPlanRestControllerTest_rollback.sql`.
- Update `LegacyMealPlanRestControllerTest.java` class name and all `@Sql` annotation paths to point to the renamed legacy SQL files.
- Verify that the legacy test still points to the `/mealplan` endpoint.

### ✓ Step 2: Fix V2 Controller Implementation
Align the v2 meal plan controller with the project's v2 patterns and the "no Resource wrapper" requirement.

- Modify `listshop-api/src/main/java/com/meg/listshop/lmt/api/controller/MealPlanRestControllerApi.java` to change the `@RequestMapping` from `/mealplan` to `/v2/mealplan`.
- Update `listshop-lmt/listshop-service/src/main/java/com/meg/listshop/lmt/api/web/controller/MealPlanRestController.java` to replace all usage of `MealPlanResource` and `MealPlanListResource` with direct `MealPlan` and `MealPlanList` objects.
- In `createMealPlan` (and other POST methods), use `ServletUriComponentsBuilder` or similar to generate location headers instead of relying on HATEOAS `Resource` links.
- Ensure all controller methods return the unwrapped models to match the new v2 requirement.

### ✓ Step 3: Implement and Fix New V2 Meal Plan Tests
Create and adapt the new v2 tests to verify the simplified JSON structure and new endpoint.

- Create `NewMealPlanRestControllerTest.java` as a copy of the legacy test.
- Create `NewMealPlanRestControllerTest.sql` and `NewMealPlanRestControllerTest_rollback.sql` (copies of legacy ones).
- Update `NewMealPlanRestControllerTest.java` to point to the new SQL files.
- Change the base URL in all test methods from `/mealplan` to `/v2/mealplan`.
- Update JSON path assertions to remove wrapper prefixes:
    - Change `meal_plan.meal_plan_id` to `meal_plan_id`.
    - Change `_embedded.mealPlanResourceList` to `meal_plan_list`.
    - Change `ratingUpdateInfo.dish_ratings` to `dish_ratings`.
- Adjust `createMealPlan` test to check for the correct response structure without the `meal_plan` wrapper.