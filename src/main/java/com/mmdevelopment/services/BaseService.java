package com.mmdevelopment.services;

import jakarta.persistence.EntityNotFoundException;
import com.mmdevelopment.models.converters.Converter;

import java.util.List;

import static com.mmdevelopment.utils.ServiceTransactionManager.executeInTransaction;

public class BaseService<M,D> implements Service{

    private final Class<M> entityClass;
    private final Converter<M,D> converter;

    protected BaseService(Class<M> entityClass, Converter<M,D> converter) {
        this.entityClass = entityClass;
        this.converter = converter;
    }

    protected Converter<M, D> getConverter() {
        return converter;
    }

    public List<D> findAll() {
        return executeInTransaction((entityManager, daoFactory) -> {
            var dao = daoFactory.createDAO(entityClass);
            return dao.getAll().stream()
                    .map(converter::entityToDto)
                    .toList();
        });
    }

    public D findById(int id) {
        return executeInTransaction((entityManager, daoFactory) -> {
            var dao = daoFactory.createDAO(entityClass);
            M entity = dao.findById(id);
            if (entity == null) {
                throw new EntityNotFoundException("Registro con id " + id + " no fue encontrado.");
            }
            return converter.entityToDto(dao.findById(id));
        });
    }

    public D save(D dto) {
        return executeInTransaction((entityManager, daoFactory) -> {
            var dao = daoFactory.createDAO(entityClass);
            return converter.entityToDto(dao.save(converter.dtoToEntity(dto)));
        });
    }

    public D create(D entity) {
        return executeInTransaction((entityManager, daoFactory) -> {
            var dao = daoFactory.createDAO(entityClass);
            return converter.entityToDto(dao.create(converter.dtoToEntity(entity)));
        });
    }

    public void update(D entity) {
        executeInTransaction((entityManager, daoFactory) -> {
            var dao = daoFactory.createDAO(entityClass);
            dao.update(converter.dtoToEntity(entity));
            return null;
        });
    }

    public void delete(D entity) {
        executeInTransaction((entityManager, daoFactory) -> {
            var dao = daoFactory.createDAO(entityClass);
            dao.delete(converter.dtoToEntity(entity));
            return null;
        });
    }

}
