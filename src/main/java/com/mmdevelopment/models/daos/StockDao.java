package com.mmdevelopment.models.daos;

import com.mmdevelopment.models.entities.Stock;
import jakarta.persistence.EntityManager;

import static com.mmdevelopment.utils.ServiceTransactionManager.executeInTransaction;

public class StockDao extends DAOImpl<Stock>{
    public StockDao(EntityManager entityManager) {
        super(entityManager, Stock.class);
    }

    public void setEnabled(Stock... stocks) {
        for (int i = 0; i < stocks.length; i++) {
            stocks[i].setEnabled(false);
            this.getEntityManager().merge(stocks[i]);
        }
    }
}
