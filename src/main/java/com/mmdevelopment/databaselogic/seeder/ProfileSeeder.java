package com.mmdevelopment.databaselogic.seeder;

import com.mmdevelopment.models.daos.DAOImpl;
import com.mmdevelopment.models.entities.Profile;
import jakarta.persistence.EntityManager;

public class ProfileSeeder extends ModelSeeder {

    public ProfileSeeder(String code) {
        super(code);
    }

    @Override
    public void runSeeder(EntityManager em) {

        em.getTransaction().begin();
        DAOImpl<Profile> dao = new DAOImpl(em, Profile.class);
        Profile profile = new Profile();
        profile.setName("Administrador");
        profile.setPrefix("admin");
        dao.create(profile);
        em.getTransaction().commit();

    }
}
