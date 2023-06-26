CREATE VIEW public.calculated_stats AS
SELECT list_tag_stats.tag_id,
       list_tag_stats.user_id,
       c.frequent_threshold,
       ((((((list_tag_stats.removed_single * c.removed_single_factor) +
            (list_tag_stats.removed_dish * c.removed_dish_factor)) +
           (list_tag_stats.removed_list * c.removed_list_factor)) +
          (list_tag_stats.removed_starterlist * c.removed_starterlist_factor)))::numeric /
    (((((list_tag_stats.added_single * c.added_single_factor) + (list_tag_stats.added_dish * c.added_dish_factor)) +
       (list_tag_stats.added_list * c.added_list_factor)) +
      (list_tag_stats.added_starterlist * c.added_starterlist_factor)))::numeric) AS factored_frequency
FROM public.list_tag_stats,
     public.list_stat_configs c
WHERE (((((list_tag_stats.added_single * c.added_single_factor) + (list_tag_stats.added_dish * c.added_dish_factor)) +
         (list_tag_stats.added_list * c.added_list_factor)) +
        (list_tag_stats.added_starterlist * c.added_starterlist_factor)) > 0);

ALTER TABLE public.calculated_stats
    OWNER TO postgres;

CREATE VIEW public.tag_extended AS
WITH parent_ids AS (SELECT DISTINCT parent.parent_tag_id
                    FROM public.tag_relation parent
                    WHERE (parent.parent_tag_id IS NOT NULL))
SELECT t.tag_id,
       t.assign_select,
       t.category_updated_on,
       t.created_on,
       t.description,
       t.is_verified,
       t.name,
       t.power,
       t.removed_on,
       t.replacement_tag_id,
       t.search_select,
       t.tag_type,
       t.tag_type_default,
       t.to_delete,
       t.updated_on,
       r.parent_tag_id,
       (ip.parent_tag_id IS NOT NULL) AS is_parent
FROM ((public.tag t
    JOIN public.tag_relation r ON ((t.tag_id = r.child_tag_id)))
    LEFT JOIN parent_ids ip ON ((t.tag_id = ip.parent_tag_id)));


ALTER TABLE public.tag_extended
    OWNER TO postgres;
