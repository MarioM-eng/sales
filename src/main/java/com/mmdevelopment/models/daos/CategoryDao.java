package com.mmdevelopment.models.daos;

import com.mmdevelopment.models.entities.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.List;

public class CategoryDao extends DAOImpl<Category>{

    public CategoryDao(EntityManager entityManager) {
        super(entityManager, Category.class);
    }

    public void create(List<Category> objects){
        EntityManager em = this.getEntityManager();
        List<String> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("" +
                "INSERT INTO categories(name)" +
                " VALUES "
        );

        int i = 0;
        for (Category category: objects) {
            sql.append("(?),");
            params.add(category.getName());
            i++;
        }
        String finalSql = sql.substring(0, sql.length() - 1);
        Query query = em.createNativeQuery(finalSql);

        i = 1;
        for (String param: params) {
            query.setParameter(i, param);
            i++;
        }

        query.executeUpdate();
    }
}
