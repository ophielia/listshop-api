-- meal_plan_slot
delete
from meal_plan_slot
where meal_plan_id = 50485;

--dish_tags
delete
from dish_tags
where dish_id in (select dish_dish_id from meal_plan_slot where meal_plan_id = 50485);

-- dishes
delete
from dish
where dish_id in (select dish_dish_id from meal_plan_slot where meal_plan_id = 50485);

-- meal plan
delete
from meal_plan
where meal_plan_id = 50485;







