package com.mmdevelopment.models.daos;

import com.mmdevelopment.models.entities.Stock;
import jakarta.persistence.EntityManager;

public class StockDao extends DAOImpl<Stock>{
    public StockDao(EntityManager entityManager) {
        super(entityManager, Stock.class);
    }

    public void setEnabled(Stock... stocks) {
        for (Stock stock: stocks) {
            stock.setEnabled(false);
            this.getEntityManager().merge(stock);
        }
    }
}
