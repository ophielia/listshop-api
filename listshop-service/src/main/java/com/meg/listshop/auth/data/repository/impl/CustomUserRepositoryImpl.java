package com.meg.listshop.auth.data.repository.impl;

import com.meg.listshop.auth.data.repository.CustomUserRepository;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Transactional
public class CustomUserRepositoryImpl implements CustomUserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void deleteUser(Long userId) {
        var query = entityManager.createNativeQuery("delete from users where user_id = " + userId);
        query.setFlushMode(FlushModeType.COMMIT);
        query.executeUpdate();
    }
}
