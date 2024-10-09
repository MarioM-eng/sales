package com.mmdevelopment.databaselogic.seeder;

import com.mmdevelopment.Utilities;
import com.mmdevelopment.models.daos.DAOImpl;
import com.mmdevelopment.models.daos.ProfileDao;
import com.mmdevelopment.models.entities.Profile;
import com.mmdevelopment.models.entities.User;
import jakarta.persistence.EntityManager;

public class UserSedder extends ModelSeeder {
    public UserSedder(String code) {
        super(code);
    }

    @Override
    public void runSeeder(EntityManager em) {
        em.getTransaction().begin();
        DAOImpl<User> dao = new DAOImpl(em, User.class);
        ProfileDao profileDao = new ProfileDao(em, Profile.class);
        Profile profile = new Profile();
        String hashedPassword = Utilities.hashPassword("admin1234*");

        User user = new User();
        user.setName("Principal");
        user.setUserName("admin");
        user.setPassword(hashedPassword);
        profile = profileDao.getProfileByTag("admin");
        if(profile.getId() == null) {
            throw new RuntimeException("Ocurrió un error al tratar de encontrar perfil de administrador");
        }
        user.setProfile(profile);
        dao.create(user);
        em.getTransaction().commit();
    }
}
