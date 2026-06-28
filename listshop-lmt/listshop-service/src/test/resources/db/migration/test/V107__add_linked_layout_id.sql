/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

ALTER TABLE list_layout ADD COLUMN linked_layout_id bigint;
ALTER TABLE list_category ADD COLUMN linked_category_id bigint;
