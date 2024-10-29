package com.mmdevelopment.models.daos;

import com.mmdevelopment.models.entities.Invoice;
import jakarta.persistence.EntityManager;

public class InvoiceDao extends DAOImpl<Invoice>{
    public InvoiceDao(EntityManager entityManager) {
        super(entityManager, Invoice.class);
    }
}
