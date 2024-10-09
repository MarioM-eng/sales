package com.mmdevelopment.databaselogic;

import jakarta.persistence.EntityManager;

public interface EntityOperation<T> {
    T execute(EntityManager em);
}
