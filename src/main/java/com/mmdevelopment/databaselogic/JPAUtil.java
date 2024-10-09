package com.mmdevelopment.databaselogic;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import com.mmdevelopment.utils.exceptions.ServiceExecutionException;

public class JPAUtil {
    private static final String PERSISTENCE_UNIT_NAME = "com.mmdevelopment.databaselogic.myPersistenceUnit";
    private static EntityManagerFactory factory;
    private static EntityManager session;

    private JPAUtil(){
        throw new IllegalStateException("Utility class");
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (factory == null) {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        return factory;
    }

    private static EntityManager getEntityManager(){
        return getEntityManagerFactory().createEntityManager();
    }

    public static EntityManager getSession(){
        if (session == null || !session.isOpen()){
            session = getEntityManagerFactory().createEntityManager();
        }
        return session;
    }

    public static void closeSession(){
        if (getSession().isOpen()) {
            getSession().close();
        }
    }

    public static void shutdown() {
        if (factory != null) {
            factory.close();
        }
    }

    public static <R> R executeInSesion(EntityOperation<R> operation) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            R result = operation.execute(em);
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new ServiceExecutionException(
                    "Error ejecutando operación dentro de un contexto de sesión de base de datos",
                    e
            );
        } finally {
            em.close();
        }
    }

}
