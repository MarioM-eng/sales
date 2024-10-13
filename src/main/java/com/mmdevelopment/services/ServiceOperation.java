package com.mmdevelopment.services;

import jakarta.persistence.EntityManager;
import com.mmdevelopment.utils.exceptions.NonexistentEntityException;
import com.mmdevelopment.utils.factories.DAOFactory;

@FunctionalInterface
public interface ServiceOperation<T> {
    T execute(EntityManager em) throws NonexistentEntityException;
}
