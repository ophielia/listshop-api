/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

delete from list_item_details where item_id in (select item_id from list_item where list_id in (33333,88888,77777,66666,55555));
delete from list_item where list_id in (33333,88888,77777,66666,55555);
delete from list where list_id in (33333, 88888, 77777, 66666,55555);
