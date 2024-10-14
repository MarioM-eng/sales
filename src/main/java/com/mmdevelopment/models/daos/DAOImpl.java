package com.mmdevelopment.models.daos;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import com.mmdevelopment.utils.exceptions.NonexistentEntityException;

import java.util.List;

@Slf4j
public class DAOImpl<T> implements DAO<T>{

    private Class<T> entityClass;
    private EntityManager entityManager;

    public DAOImpl(EntityManager entityManager, Class<T> entityClass) {
        this.entityClass = entityClass;
        this.entityManager = entityManager;
    }

    protected EntityManager getEntityManager(){
        return entityManager;
    }

    public boolean isNew(T entity) {
        EntityManager em = getEntityManager();
        Object entityId = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        if (entityId == null) {
            throw new IllegalArgumentException("Entity tiene id nulo.");
        }
        return em.find(entityClass, entityId) == null;
    }

        public T save(T object) throws NonexistentEntityException {
            if (this.isNew(object)) {
                object = this.create(object);
            } else {
                this.update(object);
            }
            return object;
        }

    public T create(T object){
        EntityManager em = getEntityManager();
        em.persist(object);
        return object;

    }

    public void create(List<T> objects){
        EntityManager em = getEntityManager();
        objects.stream().forEach(em::persist);
    }


    public void update(T object) throws NonexistentEntityException {
        EntityManager em = null;
        em = getEntityManager();
        if (!em.contains(object)) {
            throw new NonexistentEntityException("El objeto no está gestionado por el EntityManager");
        }
        em.merge(object);
    }


    public T findById(int id) {
        EntityManager em = getEntityManager();
        return em.find(entityClass, id);
    }


    public void delete(T entity) throws NonexistentEntityException {
        EntityManager em = getEntityManager();
        if (!em.contains(entity)) {
            throw new NonexistentEntityException("El objeto no está gestionado por el EntityManager");
        }
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }


    public List<T> getAll(){
        return getAll(true, -1, -1);
    }


    public List<T> getAll(int maxResults, int firstResult){
        return getAll(false, maxResults, firstResult);
    }


    public List<T> getAll(boolean all, int maxResults, int firstResult){
        EntityManager em = getEntityManager();
        CriteriaQuery<T> cq = (CriteriaQuery<T>) em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();
    }

    protected List<T> findRecords(String column, String value) {

        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();

        CriteriaQuery<T> cq = cb.createQuery(this.entityClass);

        Root<T> root = cq.from(this.entityClass);

        cq.select(root).where(cb.equal(root.get(column), value));

        TypedQuery<T> query = this.entityManager.createQuery(cq);

        return query.getResultList();
    }
}
