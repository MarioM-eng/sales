package com.mmdevelopment.models.daos;

import com.mmdevelopment.models.entities.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public class ProductDao extends DAOImpl<Product>{

    public ProductDao(EntityManager entityManager) {
        super(entityManager, Product.class);
    }

    public List<Product> getByName(String name) {
        return this.findRecords("name", name);
    }

    public Optional<Product> getProductByCode(String code) {
        return this.findRecords("code", code).stream().findFirst();
    }

    public List<Product> searchRecordsByMatch(String value) {
        String valueNoSpaces = value.replace(" ", "").toLowerCase();
        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();

        CriteriaQuery<Product> cq = cb.createQuery(Product.class);

        Root<Product> root = cq.from(Product.class);

        cq.select(root).where(
                cb.or(
                        cb.like(
                                cb.function("LOWER", String.class, cb.function("REPLACE", String.class, root.get("name"), cb.literal(" "), cb.literal(""))),
                                "%"+valueNoSpaces+"%"
                        ),
                        cb.like(
                                cb.function("LOWER", String.class, cb.function("REPLACE", String.class, root.get("code"), cb.literal(" "), cb.literal(""))),
                                "%"+valueNoSpaces+"%"
                        )
                )
        );

        TypedQuery<Product> query = this.getEntityManager().createQuery(cq);

        return query.getResultList();
    }

}
