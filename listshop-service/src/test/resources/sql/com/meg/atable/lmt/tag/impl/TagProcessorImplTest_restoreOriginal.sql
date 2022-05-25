delete
from public.auto_tag_instructions;
insert into public.auto_tag_instructions (instruction_type, instruction_id, assign_tag_id, is_invert, search_terms,
                                          invert_filter)
values ('TAG', 1000, 346, false, '9;88;368;372;374;375', null),
       ('TEXT', 1, 301, false, 'Soup', 'false'),
       ('TEXT', 2, 323, false, 'Crock-pot;Crockpot;Crock pot', 'false'),
       ('TAG', 3, 346, false, '371', null),
       ('TAG', 4, 199, true, '371', '433');