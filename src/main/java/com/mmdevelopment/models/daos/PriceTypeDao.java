package com.mmdevelopment.models.daos;

import com.mmdevelopment.models.entities.PriceType;
import jakarta.persistence.EntityManager;

public class PriceTypeDao extends DAOImpl<PriceType>{
    public PriceTypeDao(EntityManager entityManager) {
        super(entityManager, PriceType.class);
    }
}
