# List Item Conversion and Aggregation

This document provides a technical deep-dive into how ingredients are converted and aggregated when added to a shopping list in the "NextGen" (Tovo) system.

## Data Storage & Entities

The shopping list uses a one-to-many relationship to handle aggregation. A single visible item on the list acts as a container for multiple contributions (e.g., the same ingredient from two different dishes).

### ListItemEntity
`ListItemEntity` represents a unique tag (ingredient) on a specific shopping list. It is the "parent" entity that the user sees.

- **Relationship**: Holds a list of `ListItemDetailEntity` objects.
- **Aggregated Totals**:
    - `rawQuantity`: The raw sum of quantities from all details.
    - `roundedQuantity`: The final quantity displayed to the user, rounded according to unit rules.
    - `wholeQuantity` / `fractionalQuantity`: Split representation of the rounded quantity for display.
    - `unit`: The `UnitEntity` assigned to the aggregated item.
    - `amountText`: A generated string representing the full amount (e.g., "1 1/2 lbs").
- **Source Tracking**:
    - `rawDishSources`: A comma-separated string or indicator of which dishes contributed to this item.
    - `rawListSources`: A comma-separated string or indicator of which lists contributed to this item.

### ListItemDetailEntity
`ListItemDetailEntity` stores the specific contribution from a single source.

- **Relationship**: Linked to a parent `ListItemEntity` via `item_id`.
- **Source Tracking**:
    - `linkedDishId`: The ID of the dish if this item came from a recipe.
    - `linkedListId`: The ID of the source list if this item was moved from another list.
    - `rawEntry`: The original text input or ingredient description.
- **Amount Details**: Stores the specific quantity, unit, and unit size for this contribution.
- **Status Flags**:
    - `unspecified`: Indicates if this detail's amount could not be structurally converted or scaled.
    - `containsUnspecified`: Indicates if the source contained unspecified amounts.

## Addition Process & State Transitions

The addition of items to a shopping list is managed by the `ListItemStateMachine`, which orchestrates transitions based on events.

### ListItemStateMachine
When an item is added (from a dish, another list, or manual entry), the `handleEvent` method is called with `ListItemEvent.ADD_ITEM`. This event is mapped to the `ActiveTransition` component.

### ActiveTransition Flow
The `ActiveTransition` handles the logic for bringing an item into an "Active" state on the list. The process follows these high-level steps:

1.  **Lookup or Create**: The `getOrCreateItem` method checks the `ItemStateContext` for an existing `ListItemEntity`. If the list already contains an item with the same Tag ID, that item is retrieved. Otherwise, a new `ListItemEntity` is created and saved.
2.  **Determine Processing Type**: The transition identifies if the source is a `DISH`, a `LIST`, or a `SIMPLE_ITEM` (manual entry).
3.  **Process Addition**:
    - **Dish Item**: `processAddDishItem` maps the `DishItemEntity` to a `ListItemDetailEntity`.
    - **List Item**: `processAddListItem` iterates through the details of the source item and adds them as new details to the target item.
    - **Simple Item**: `processAddSimpleItem` handles direct tag additions.
4.  **Detail Lookup**: Within the processing methods, the system checks if a `ListItemDetailEntity` already exists for that specific source (e.g., the same Dish ID).
    - If it exists, the quantity is updated (`doAddToExisting`).
    - If not, a new `ListItemDetailEntity` is created (`addSpecifiedAmount...` or `addNonSpecifiedAmount`).
5.  **Re-summation**: After the details are updated, `ListConversionService.sumItemDetails` is called to recalculate the parent item's aggregated totals.

## Structural Mapping & Aggregation Logic

The `ListConversionService` is responsible for the structural transformation of ingredient data and the mathematical aggregation of multiple sources into a single result.

### Mapping to List Context
When an ingredient (e.g., from a dish) is added, it must be mapped to the list's domain.

- **`convertDishItemForList`**: This method takes a `DishItemEntity` and creates a `ConvertibleAmount` (specifically an `EntityConvertibleAmount`). It then calls `convertDetail`, which attempts to find a target unit (either from an existing detail or the parent item) and uses the `ConverterService` to perform the structural conversion.
- **Structural vs. Mathematical**: While this service handles the structural mapping (e.g., ensuring a "cup of flour" from a dish is correctly mapped to the "flour" item on the list), the actual mathematical conversion between units (e.g., grams to ounces) is handled by the `ConverterService`.

### Aggregation: `sumItemDetails`
The `sumItemDetails` method is the core of the aggregation logic. It consolidates all details in a `ListItemEntity` into a single aggregated amount.

1.  **Grouping**: Details are grouped into a `unitSummary` map by `Unit ID` and `Unit Size`. Details without units are gathered into an `unspecified` list.
2.  **Base Unit Selection**:
    - The system determines a "Base Key" (the unit that others will be converted to).
    - It prefers the unit already set on the `ListItemEntity`.
    - If no unit is set, it defaults to the first unit found in the summary.
3.  **Summation Loop**:
    - The system iterates through all other groups in the summary.
    - It uses `converterService.add` to add each group's total to the running summary.
    - Details that are successfully summed are marked as `unspecified = false`.
    - If a group cannot be converted to the base unit, its details remain marked as `unspecified`.
4.  **Final Scaling**: The aggregated total is scaled (using `converterService.scale`) to the most appropriate unit for display (e.g., converting 16 oz to 1 lb).
5.  **Setting Totals**: The final `ConvertibleAmount` is passed to `setInItem`, which:
    - Sets `rawQuantity`, `unit`, and `unitSize` on the `ListItemEntity`.
    - Rounds the quantity for display.
    - Generates the `amountText` using `AmountTextBuilder`.
    - Determines the `SpecificationType` (ALL, MIXED, or NONE) based on which details were successfully aggregated.
