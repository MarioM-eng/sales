package com.mmdevelopment.databaselogic.seeder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import com.mmdevelopment.databaselogic.JPAUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static com.mmdevelopment.databaselogic.ConnectionDB.getConnection;

@Slf4j
public class Seeder {

    private static final List<ModelSeeder> SEEDERLIST = Arrays.asList(
            new ProfileSeeder("1728195367"),
            new UserSedder("1728236119")
    );

    public static void seed() {

        EntityManager em = JPAUtil.getSession();

        try {
            boolean SeedersTableExists = checkExistenceOfSeedersTable(em);

            if (!SeedersTableExists) {
                createSeedersTable(em);
            }

            for(ModelSeeder modelSeeder: SEEDERLIST){
                if(!checkCodeExists(modelSeeder.getCode())){
                    modelSeeder.runSeeder(em);
                    insertCode(em, modelSeeder.getCode());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Cierra el EntityManager y la EntityManagerFactory al finalizar
            em.close();
        }
    }

    private static boolean checkExistenceOfSeedersTable(EntityManager em) {
        Query query = em.createNativeQuery("SELECT TABLE_NAME\n" +
                "FROM INFORMATION_SCHEMA.TABLES\n" +
                "WHERE TABLE_NAME LIKE 'SEEDERS';"
        );
        List<?> result = query.getResultList();
        return !result.isEmpty();
    }

    private static void createSeedersTable(EntityManager em) {
        em.getTransaction().begin();
        em.createNativeQuery("CREATE TABLE seeders (id INT AUTO_INCREMENT PRIMARY KEY, code VARCHAR(255))").executeUpdate();
        em.getTransaction().commit();
    }

    private static boolean checkCodeExists(String code) {
        int count = -1;
        String query = "SELECT COUNT(*) FROM SEEDERS WHERE code = ?";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            // Establecer el valor del parámetro vinculado
            statement.setString(1, code);

            // Ejecutar la consulta
            try (ResultSet resultSet = statement.executeQuery()) {
                // Procesar los resultados
                if (resultSet.next()) {
                    count = resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return count > 0;
    }

    private static void insertCode(EntityManager em, String code) {
        // Ejecuta una consulta nativa para insertar el código en la tabla de seeders
        em.getTransaction().begin();
        Query query = em.createNativeQuery("INSERT INTO seeders (code) VALUES (?)");
        query.setParameter(1, code);
        query.executeUpdate();
        em.getTransaction().commit();
    }
}
