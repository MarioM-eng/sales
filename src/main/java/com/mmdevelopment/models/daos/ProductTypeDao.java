package com.mmdevelopment.models.daos;

import com.mmdevelopment.models.entities.ProductType;
import com.mmdevelopment.services.ServiceOperation;
import com.mmdevelopment.utils.ServiceTransactionManager;
import com.mmdevelopment.utils.exceptions.NonexistentEntityException;
import com.mmdevelopment.utils.factories.DAOFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ProductTypeDao extends DAOImpl<ProductType>{

    EntityManager entityManager;

    public ProductTypeDao(EntityManager entityManager) {
        super(entityManager, ProductType.class);
        this.entityManager = entityManager;
    }

    public void create(List<ProductType> objects){
        EntityManager em = this.entityManager;
        List<String> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("" +
                "INSERT INTO product_types(name)" +
                " VALUES "
        );

        int i = 0;
        for (ProductType productType: objects) {
            sql.append("(?),");
            params.add(productType.getName());
            i++;
        }
        String finalSql = sql.substring(0, sql.length() - 1);
        Query query = em.createNativeQuery(finalSql);

        i = 1;
        for (String param: params) {
            query.setParameter(i, param);
            i++;
        }

        ServiceTransactionManager.executeInTransaction(
                new ServiceOperation<ProductType>() {
                    @Override
                    public ProductType execute(EntityManager em) throws NonexistentEntityException {
                        query.executeUpdate();
                        return null;
                    }
                }
        );
    }
}
