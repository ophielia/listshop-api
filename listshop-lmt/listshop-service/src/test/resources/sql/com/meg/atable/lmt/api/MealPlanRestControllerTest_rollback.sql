/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

-- meal_plan_slot
delete
from meal_plan_slot
where meal_plan_id = 50485;

--dish_tags
delete from dish_tags where dish_id in (select dish_dish_id from meal_plan_slot where meal_plan_id = 50485);
delete from dish_tags where dish_id in (50000001,13000011,88000011,56705001,70000121);

-- dishes
delete from dish where dish_id in (select dish_dish_id from meal_plan_slot where meal_plan_id = 50485);
delete from dish where dish_id in (50000001,13000011,88000011,56705001,70000121);

-- meal plan
delete from meal_plan where meal_plan_id = 50485;








