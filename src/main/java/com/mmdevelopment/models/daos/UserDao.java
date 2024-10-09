package com.mmdevelopment.models.daos;

import com.mmdevelopment.models.entities.Profile;
import com.mmdevelopment.models.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.Optional;

public class UserDao extends DAOImpl<User>{
    private EntityManager entityManager;
    public UserDao(EntityManager entityManager) {
        super(entityManager, User.class);
        this.entityManager = entityManager;
    }

    public Optional<User> getUserByUserName(String userName) {
        Optional<User> user;
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();

        CriteriaQuery<User> cq = cb.createQuery(User.class);

        Root<User> root = cq.from(User.class);

        cq.select(root).where(cb.equal(root.get("userName"), userName));

        TypedQuery<User> query = this.entityManager.createQuery(cq);
        try {
            user = Optional.of(query.getSingleResult());
        } catch (NoResultException err){
            user = Optional.empty();
        }

        return user;
    }
}
