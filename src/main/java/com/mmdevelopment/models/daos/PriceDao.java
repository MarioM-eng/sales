package com.mmdevelopment.models.daos;

import com.mmdevelopment.models.entities.Price;
import jakarta.persistence.EntityManager;

public class PriceDao extends DAOImpl<Price>{
    public PriceDao(EntityManager entityManager) {
        super(entityManager, Price.class);
    }
}
