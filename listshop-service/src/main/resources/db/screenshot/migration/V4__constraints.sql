alter table public.dish
    add constraint fk_dish__user_id
        foreign key (user_id) references public.users
            on delete cascade;

alter table public.list
    add constraint fk_list__user_id
        foreign key (user_id) references public.users
            on delete cascade;

alter table public.meal_plan
    add constraint fk_meal_plan__user_id
        foreign key (user_id) references public.users
            on delete cascade;
alter table public.authority
    add constraint fk_authority__user_id
        foreign key (user_id) references public.users
            on delete cascade;


alter table public.list_tag_stats
    add constraint fk_stats__user_id
        foreign key (user_id) references public.users
            on delete cascade;

alter table public.dish_tags
    add constraint fk_dish__dish_tags
        foreign key (dish_id) references public.dish
            on delete cascade;

alter table public.category_tags
    add constraint fkns9s1sef980caqqamoee8srdw
        foreign key (category_id) references public.list_category;
alter table public.list_category
    add constraint fkrhcs3i2p15y79hn00y5ic41gn
        foreign key (layout_id) references public.list_layout;
alter table public.list_item
    add constraint fk_list__list_id
        foreign key (list_id) references public.list
            on delete cascade;
alter table public.meal_plan_slot
    add constraint fk_meal_plan__meal_plan_slot
        foreign key (meal_plan_id) references public.meal_plan
            on delete cascade;

alter table public.meal_plan_slot
    add constraint fkdit15dhtc9j583c1pp21c8ss0
        foreign key (dish_dish_id) references public.dish;

alter table public.proposal
    add constraint fk_proposal__user_id
        foreign key (user_id) references public.users
            on delete cascade;
alter table public.proposal_approach
    add constraint fk_proposal_approach__proposal_context
        foreign key (proposal_context_id) references public.proposal_context
            on delete cascade;

alter table public.proposal_context
    add constraint fk_proposal_context__proposal
        foreign key (proposal_id) references public.proposal
            on delete cascade;
alter table public.proposal_dish
    add constraint fk_proposal_dish__proposal_slot
        foreign key (slot_id) references public.proposal_slot
            on delete cascade;

alter table public.proposal_slot
    add constraint fk_proposal_slot__proposal
        foreign key (proposal_id) references public.proposal
            on delete cascade;
alter table public.category_tags
    add constraint fkclr8vrg8b1cwgwjsgcd5jtj6a
        foreign key (tag_id) references public.tag;

alter table public.dish_tags
    add constraint fkpy8j9ypbt3d59bjs0hgl3wcct
        foreign key (tag_id) references public.tag;

alter table public.list_item
    add constraint fklcvoij9ynqfllhxgn9v6qpsh8
        foreign key (tag_id) references public.tag;

alter table public.tag_relation
    add constraint fk3vyajpbcb8wl8380yntahtgtf
        foreign key (parent_tag_id) references public.tag;

alter table public.tag_relation
    add constraint fk6x8vvlp985udfs7g15uuxj42c
        foreign key (child_tag_id) references public.tag;
alter table public.target
    add constraint fk_target__user_id
        foreign key (user_id) references public.users
            on delete cascade;

alter table public.target_slot
    add constraint fk_target__target_slot
        foreign key (target_id) references public.target
            on delete cascade;

alter table public.user_devices
    add constraint fk_user_devices__user_id
        foreign key (user_id) references public.users
            on delete cascade;
