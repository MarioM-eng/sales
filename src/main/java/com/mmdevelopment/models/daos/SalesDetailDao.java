package com.mmdevelopment.models.daos;

import com.mmdevelopment.models.entities.SalesDetail;
import jakarta.persistence.EntityManager;

public class SalesDetailDao extends DAOImpl<SalesDetail>{
    public SalesDetailDao(EntityManager entityManager) {
        super(entityManager, SalesDetail.class);
    }
}
