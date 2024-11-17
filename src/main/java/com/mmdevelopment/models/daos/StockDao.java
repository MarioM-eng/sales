package com.mmdevelopment.models.daos;

import com.mmdevelopment.models.entities.Stock;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

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

    public List<Stock> getProductsBelowMinStock() {
        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();

        CriteriaQuery<Stock> cq = cb.createQuery(Stock.class);

        Root<Stock> root = cq.from(Stock.class);

        cq.select(root).where(
                cb.greaterThan(root.get("reorderPoint"), root.get("quantityOnHand"))
        );

        TypedQuery<Stock> query = this.getEntityManager().createQuery(cq);

        return query.getResultList();
    }
}
