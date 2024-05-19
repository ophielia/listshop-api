package com.meg.listshop.lmt.data.repository.impl;

import com.meg.listshop.lmt.data.pojos.DishItemDTO;
import com.meg.listshop.lmt.data.repository.CustomDishItemRepository;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.List;


@Repository
public class CustomDishItemRepositoryImpl implements CustomDishItemRepository {

    private static final Logger logger = LoggerFactory.getLogger(CustomDishItemRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    public List<DishItemDTO> getIngredientsForDish(Long dishId) {

        Session session = entityManager.unwrap(Session.class);
         Query query = session.createNamedQuery("IngredientsForDish", DishItemDTO.class);
        query.setParameter("dishId", dishId);
        return query.getResultList();
    }

}
