package com.mmdevelopment.services;

import com.mmdevelopment.models.daos.DAOImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;

import java.util.List;

import static com.mmdevelopment.utils.ServiceTransactionManager.executeInTransaction;

public class BaseService<M> implements Service{

    private final Class<M> entityClass;
    @Getter
    private final DAOImpl<M> dao;

    public BaseService(Class<M> entityClass, DAOImpl<M> dao) {
        this.entityClass = entityClass;
        this.dao = dao;
    }

    public List<M> findAll() {
        return this.dao.getAll();
    }

    public M findById(int id) {
        return executeInTransaction(entityManager -> {
            M entity = this.dao.findById(id);
            if (entity == null) {
                throw new EntityNotFoundException("Registro con id " + id + " no fue encontrado.");
            }
            return this.dao.findById(id);
        });
    }

    public M save(M entity) {
        return executeInTransaction(entityManager -> this.dao.save(entity));
    }

    public M create(M entity) {
        return executeInTransaction(entityManager -> this.dao.create(entity));
    }

    public void update(M entity) {
        executeInTransaction(entityManager -> {
            this.dao.update(entity);
            return null;
        });
    }

    public void delete(M... entity) {
        executeInTransaction(entityManager -> {
            for (int i = 0; i < entity.length; i++) {
                this.dao.delete(entity[i]);
            }
            return null;
        });
    }

}
