package com.mmdevelopment.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import com.mmdevelopment.databaselogic.JPAUtil;
import com.mmdevelopment.services.ServiceOperation;
import com.mmdevelopment.utils.exceptions.ServiceExecutionException;
import com.mmdevelopment.utils.factories.DAOFactory;

@Slf4j
public class ServiceTransactionManager {

    private ServiceTransactionManager() {}

    public static <R> R executeInTransaction(ServiceOperation<R> operation) {
        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        DAOFactory daoFactory = new DAOFactory(entityManager);
        try {
            transaction.begin();
            R result = operation.execute(entityManager, daoFactory);
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Error ejecutando operación del servicio: {}", e);
            throw new ServiceExecutionException(
                    "Error ejecutando operación del servicio",
                    e
            );
        } finally {
            entityManager.close();
        }
    }

}
