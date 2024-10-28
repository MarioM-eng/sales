package com.mmdevelopment.models.daos;

import com.mmdevelopment.models.entities.Color;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Optional;

public class ColorDao extends DAOImpl<Color>{
    public ColorDao(EntityManager entityManager) {
        super(entityManager, Color.class);
    }

    public List<Color> searchRecordsByMatch(String value, boolean onlyEnabled) {
        String valueNoSpaces = value.replace(" ", "").toLowerCase();
        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();

        CriteriaQuery<Color> cq = cb.createQuery(Color.class);

        Root<Color> root = cq.from(Color.class);

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

        TypedQuery<Color> query = this.getEntityManager().createQuery(cq);

        return query.getResultList();
    }

    public Optional<Color> getColorByName(String name){
        String valueNoSpaces = name.replace(" ", "").toLowerCase();
        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();

        CriteriaQuery<Color> cq = cb.createQuery(Color.class);

        Root<Color> root = cq.from(Color.class);

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

        TypedQuery<Color> query = this.getEntityManager().createQuery(cq);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Color> getEnabledColors() {
        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();

        CriteriaQuery<Color> cq = cb.createQuery(Color.class);

        Root<Color> root = cq.from(Color.class);

        cq.select(root).where(
                cb.equal(root.get("enabled"), true)
        );

        TypedQuery<Color> query = this.getEntityManager().createQuery(cq);

        return query.getResultList();
    }
}
