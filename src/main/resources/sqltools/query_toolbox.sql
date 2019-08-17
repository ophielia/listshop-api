
select * from auto_tag_instructions;

update auto_tag_instructions 
set search_terms = 'crockpot;crock-pot;slow cooker;slow-cooker;slowcooker;crock pot;slow cooker' 
where instruction_id = 1002;

select * from tag where tag_id in (9,88,368,372,374,375);

select tag_search_group_id, g.tag_id, g.name, m.tag_id, m.name
from tag_search_group sg
join tag g on g.tag_id = sg.group_id
join tag m on m.tag_id = sg.member_id
--where g.tag_id in (9,88,368,372,374,375)
order by g.name;


with oops as (select distinct tag_id 
from tag 
where assign_select = true and search_select = true)
select distinct tag_id from tag_search_group sg
join oops on oops.tag_id = sg.group_id


with oops as (select distinct tag_id 
from tag 
where assign_select = true and search_select = true)
select distinct tag_id 
from oops
left join tag_search_group sg on oops.tag_id = sg.group_id
where sg is null

with oops as (select distinct tag_id 
from tag 
where assign_select = true and search_select = true),
todel as (
select distinct tag_id 
from oops
left join tag_search_group sg on oops.tag_id = sg.group_id
where sg is null)
update tag
set search_select = false
from todel d
where tag.tag_id = d.tag_id;



--- clean up users in db
delete from authority where user_id > 20;
delete from users where user_id > 20;
delete from users where user_id = 2;
update users set email = username;