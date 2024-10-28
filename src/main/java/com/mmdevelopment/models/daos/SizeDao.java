package com.mmdevelopment.models.daos;

import com.mmdevelopment.models.entities.Size;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Optional;

public class SizeDao extends DAOImpl<Size>{
    public SizeDao(EntityManager entityManager) {
        super(entityManager, Size.class);
    }

    public List<Size> searchRecordsByMatch(String value, boolean onlyEnabled) {
        String valueNoSpaces = value.replace(" ", "").toLowerCase();
        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();

        CriteriaQuery<Size> cq = cb.createQuery(Size.class);

        Root<Size> root = cq.from(Size.class);

        Predicate nameLike = cb.like(
                cb.function(
                        "LOWER",
                        String.class,
                        cb.function(
                                "REPLACE",
                                String.class,
                                root.get("name"), cb.literal(" "), cb.literal("")
                        )
                ),
                "%"+valueNoSpaces+"%"
        );



        if (onlyEnabled) {
            cq.select(root).where(
                    nameLike,
                    cb.equal(
                            root.get("enabled"), true
                    )
            );
        }else {
            cq.select(root).where(nameLike);
        }

        TypedQuery<Size> query = this.getEntityManager().createQuery(cq);

        return query.getResultList();
    }

    public Optional<Size> getByName(String name){
        String valueNoSpaces = name.replace(" ", "").toLowerCase();
        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();

        CriteriaQuery<Size> cq = cb.createQuery(Size.class);

        Root<Size> root = cq.from(Size.class);

        cq.select(root).where(
                cb.equal(
                        cb.function(
                                "LOWER",
                                String.class,
                                cb.function(
                                        "REPLACE",
                                        String.class,
                                        root.get("name"), cb.literal(" "), cb.literal("")
                                )
                        ),
                        valueNoSpaces
                )
        );

        TypedQuery<Size> query = this.getEntityManager().createQuery(cq);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Size> getEnabled() {
        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();

        CriteriaQuery<Size> cq = cb.createQuery(Size.class);

        Root<Size> root = cq.from(Size.class);

        cq.select(root).where(
                cb.equal(root.get("enabled"), true)
        );

        TypedQuery<Size> query = this.getEntityManager().createQuery(cq);

        return query.getResultList();
    }
}
