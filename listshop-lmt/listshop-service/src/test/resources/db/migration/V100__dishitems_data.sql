alter table factors add column tag_id BIGINT;

alter table units add column is_multidomain BOOL;



-- alter table factors drop column tag_id