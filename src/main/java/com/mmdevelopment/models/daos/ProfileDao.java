package com.mmdevelopment.models.daos;

import com.mmdevelopment.models.entities.Profile;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class ProfileDao extends DAOImpl<Profile>{

    EntityManager entityManager;

    public ProfileDao(EntityManager entityManager, Class<Profile> entityClass) {
        super(entityManager, entityClass);
        this.entityManager = entityManager;
    }

    public Profile getProfileByTag(String prefix){
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();

        CriteriaQuery<Profile> cq = cb.createQuery(Profile.class);

        Root<Profile> root = cq.from(Profile.class);

        cq.select(root).where(cb.equal(root.get("prefix"), prefix));

        TypedQuery<Profile> query = this.entityManager.createQuery(cq);
        return query.getSingleResult();
    }
}
