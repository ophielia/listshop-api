package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.DishItemEntity;
import com.meg.listshop.lmt.data.pojos.RatingInfoDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DishItemRepository extends JpaRepository<DishItemEntity, Long>, CustomDishItemRepository {

    @Query("select i FROM DishItemEntity i where i.dish.userId = ?1 and i.dish.dish_id in (?2) ")
    List<DishItemEntity> findByDishAndUser(Long userId, List<Long> dishIds);

    @Query(value = """
select p.name, p.tag_id, t.power, p.power
from dish_items d
         join tag t on t.tag_id = d.tag_id
         join tag_relation r on r.child_tag_id = t.tag_id
         join tag p on p.tag_id = r.parent_tag_id
where dish_id = ?1
  and t.tag_type = 'Rating'
order by p.name
""", nativeQuery = true)
    List<RatingInfoDTO> getRatingsForDish(Long dishId);
}
