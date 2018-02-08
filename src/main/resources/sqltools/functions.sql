
CREATE FUNCTION copy_dishes(integer, integer) RETURNS integer
    LANGUAGE plpgsql
    AS $_$   DECLARE
      pOrigUser ALIAS for $1;
      pNewUser ALIAS for $2;
      pDish	record;
      nDish int;
   BEGIN
      FOR pDish IN select * from Dish o 
      				left join dish n on o.dish_name = n.dish_name and n.user_id = pNewUser
					where o.user_id = pOrigUser
					and n.dish_id is null LOOP
         insert into dish (dish_id,description, dish_name, user_id, last_added)
				select nextval('hibernate_sequence'),description, dish_name, pNewUser,last_added  
				from dish where user_id = pOrigUser and dish_id = pDish.dish_id 
         returning dish_id into nDish;
         RAISE NOTICE 'dish created(new:%, old:%)',nDish,pDish.dish_id;
         insert into dish_tags (dish_id, tag_id)
			select nDish, tag_id from dish_tags where dish_id = pDish.dish_id;
      END LOOP;
      return 1;
   END;$_$;


ALTER FUNCTION public.copy_dishes(integer, integer) OWNER TO postgres;


CREATE FUNCTION deleteallperishable() RETURNS integer
    LANGUAGE plpgsql
    AS $$   BEGIN
execute 'delete    from target_proposal_slot';
execute 'delete    from target_proposal';

execute 'delete    from target_tags';
execute 'delete    from target_slots';
execute 'delete    from target_slot';
execute 'delete    from target';

execute 'delete    from meal_plan_slot';
execute 'delete   from meal_plan';

execute 'delete    from proposal_context_slot';
execute 'delete    from proposal_context';

execute 'delete    from list_item';
execute 'delete    from list';

execute 'delete    from auto_tag_instructions';
execute 'delete    from shadow_tags';

   return 1;
   END;
$$;


ALTER FUNCTION public.deleteallperishable() OWNER TO postgres;


CREATE FUNCTION movedish("pFirstId" integer) RETURNS integer
    LANGUAGE plpgsql
    AS $_$   DECLARE
   pFirstId ALIAS for $1;
   pDish record;
      newId	int;
      oldId int;
      i int;
   BEGIN
   oldId = 11;
   newId = 1100;
   i=0;

ALTER TABLE ONLY dish_tags
    DROP CONSTRAINT fkbh371e2vv53a3arqea0hf3jkl;
    
      FOR pDish IN select * from Dish o LOOP
      oldId = pDish.dish_id;
      newId = pFirstId + i;
-- update dish_tags
update dish_tags set dish_id = newId where dish_id = oldId;      

-- and now - update the dish itself
update dish set dish_id = newId where dish_id = oldId;
i=i+1;
END LOOP;
ALTER TABLE ONLY dish_tags
    ADD CONSTRAINT fkbh371e2vv53a3arqea0hf3jkl FOREIGN KEY (dish_id) REFERENCES dish(dish_id);

   return 1;
   END;
$_$;


ALTER FUNCTION public.movedish("pFirstId" integer) OWNER TO postgres;


CREATE FUNCTION movelistcategory("pFirstId" integer) RETURNS integer
    LANGUAGE plpgsql
    AS $_$   DECLARE
   pFirstId ALIAS for $1;
   pCat record;
      newId	int;
      oldId int;
      i int;
   BEGIN
   oldId = 11;
   newId = 1100;
   i=0;
ALTER TABLE ONLY category_tags
    DROP CONSTRAINT fkns9s1sef980caqqamoee8srdw;
      FOR pCat IN select * from list_category o LOOP
      oldId = pCat.category_id;
      newId = pFirstId + i;
-- update dish_tags
update category_tags set category_id = newId where category_id = oldId;      

-- and now - update the dish itself
update list_category set category_id = newId where category_id = oldId;      
i=i+1;
END LOOP;
ALTER TABLE ONLY category_tags
    ADD CONSTRAINT fkns9s1sef980caqqamoee8srdw FOREIGN KEY (category_id) REFERENCES list_category(category_id);

   return 1;
   END;
$_$;


ALTER FUNCTION public.movelistcategory("pFirstId" integer) OWNER TO postgres;


CREATE FUNCTION movelistlayout("pFirstId" integer) RETURNS integer
    LANGUAGE plpgsql
    AS $_$   DECLARE
   pFirstId ALIAS for $1;
   pRec record;
      newId	int;
      oldId int;
      i int;
   BEGIN
   oldId = 11;
   newId = 1100;
   i=0;
   
   ALTER TABLE ONLY list_category
    DROP CONSTRAINT fkrhcs3i2p15y79hn00y5ic41gn;
    
      FOR pRec IN select * from list_category o LOOP
      oldId = pRec.layout_id;
      newId = pFirstId + i;
-- update dish_tags
update list_category set layout_id = newId where layout_id = oldId;      

-- and now - update the dish itself
update list_layout set layout_id = newId where layout_id = oldId;      
i=i+1;
END LOOP;
ALTER TABLE ONLY list_category
    ADD CONSTRAINT fkrhcs3i2p15y79hn00y5ic41gn FOREIGN KEY (layout_id) REFERENCES list_layout(layout_id);

   return 1;
   END;
$_$;


ALTER FUNCTION public.movelistlayout("pFirstId" integer) OWNER TO postgres;


CREATE FUNCTION movetag("pFirstInt" integer DEFAULT 100000) RETURNS integer
    LANGUAGE plpgsql
    AS $_$   DECLARE
   pFirstId ALIAS for $1;
   pTag record;
      newId	int;
      oldId int;
      i int;
   BEGIN
   oldId = 11;
   newId = 1100;
   i=0;
ALTER TABLE ONLY tag_relation
    drop CONSTRAINT fk6x8vvlp985udfs7g15uuxj42c ;
ALTER TABLE ONLY dish_tags
    drop CONSTRAINT fkpy8j9ypbt3d59bjs0hgl3wcct;

ALTER TABLE ONLY category_tags
    drop CONSTRAINT fkclr8vrg8b1cwgwjsgcd5jtj6a;    

ALTER TABLE ONLY tag_relation
    drop CONSTRAINT fk3vyajpbcb8wl8380yntahtgtf;
 FOR pTag IN select * from Tag o LOOP
      oldId = pTag.tag_id;
      newId = pFirstId + i;
-- update dish_tags
update dish_tags set tag_id = newId where tag_id = oldId;      

-- update tag_relation
update tag_relation set child_tag_id = newId where child_tag_id = oldId;
update tag_relation set parent_tag_id = newId where parent_tag_id = oldId;

-- update tag_search_group
update tag_search_group set group_id = newId where group_id = oldId;
update tag_search_group set member_id = newId where member_id = oldId;

-- update category_tags
update category_tags set tag_id = newId where tag_id = oldId;

-- update list_tag_stats
update list_tag_stats set tag_id = newId where tag_id = oldId;

-- and now - update the tag itself
update tag set tag_id = newId where tag_id = oldId;
i=i+1;
END LOOP;
ALTER TABLE ONLY tag_relation
    ADD CONSTRAINT fk6x8vvlp985udfs7g15uuxj42c FOREIGN KEY (child_tag_id) REFERENCES tag(tag_id);
ALTER TABLE ONLY dish_tags
    ADD CONSTRAINT fkpy8j9ypbt3d59bjs0hgl3wcct FOREIGN KEY (tag_id) REFERENCES tag(tag_id);

ALTER TABLE ONLY tag_relation
    ADD CONSTRAINT fk3vyajpbcb8wl8380yntahtgtf FOREIGN KEY (parent_tag_id) REFERENCES tag(tag_id);
ALTER TABLE ONLY category_tags
    ADD CONSTRAINT fkclr8vrg8b1cwgwjsgcd5jtj6a FOREIGN KEY (tag_id) REFERENCES tag(tag_id);
   return 1;
   END;$_$;


ALTER FUNCTION public.movetag("pFirstInt" integer) OWNER TO postgres;


CREATE FUNCTION movetagsattelites("pFirstId" integer) RETURNS integer
    LANGUAGE plpgsql
    AS $_$
   DECLARE
   pFirstId ALIAS for $1;
   pRec record;
      newId	int;
      oldId int;
      i int;
   BEGIN
   i=0;
      FOR pRec IN select * from tag_relation o LOOP
      oldId = pRec.tag_relation_id;
      newId = pFirstId + i;

-- and now - update the dish itself
update tag_relation set tag_relation_id = newId where tag_relation_id = oldId;      
i=i+1;
END LOOP;


   i=0;
      FOR pRec IN select * from tag_search_group o LOOP
      oldId = pRec.tag_search_group_id;
      newId = pFirstId + i;

-- and now - update the dish itself
update tag_search_group set tag_search_group_id = newId where tag_search_group_id = oldId;      
i=i+1;
END LOOP;


   i=0;
      FOR pRec IN select * from list_tag_stats o LOOP
      oldId = pRec.list_tag_stat_id;
      newId = pFirstId + i;

-- and now - update the dish itself
update list_tag_stats set list_tag_stat_id = newId where list_tag_stat_id = oldId;      
i=i+1;
END LOOP;

   return 1;
   END;

$_$;


ALTER FUNCTION public.movetagsattelites("pFirstId" integer) OWNER TO postgres;
