package com.mmdevelopment.utils.factories;

import jakarta.persistence.EntityManager;
import com.mmdevelopment.models.daos.DAOImpl;

public class DAOFactory {

    private final EntityManager entityManager;

    public DAOFactory(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public <T> DAOImpl<T> createDAO(Class<T> entityClass) {
        return new DAOImpl<>(entityManager, entityClass);
    }

}
