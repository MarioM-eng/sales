package com.mmdevelopment.services;

import com.mmdevelopment.Utilities;
import com.mmdevelopment.databaselogic.JPAUtil;
import com.mmdevelopment.models.daos.UserDao;
import com.mmdevelopment.models.dtos.UserLoginDto;
import com.mmdevelopment.models.entities.User;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Auth {

    private EntityManager entityManager;
    public static User loggedInUser;

    public Auth() {
        this.entityManager = JPAUtil.getSession();
    }

    public void login(UserLoginDto userLoginDto) {
        userLoginDto.validate();

        UserDao userDao = new UserDao(this.entityManager);
        User user = userDao.getUserByUserName(userLoginDto.getUserName())
                .orElseThrow(()->new IllegalArgumentException("Usuario incorrecto"));

        if (!Utilities.checkPassword(userLoginDto.getPassword(), user.getPassword())){
            throw new IllegalArgumentException("Contraseña incorrecto");
        }
        loggedInUser = user;
    }

}
