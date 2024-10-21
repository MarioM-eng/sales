package com.mmdevelopment.models.daos;

import com.mmdevelopment.models.entities.Color;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.List;

public class ColorDao extends DAOImpl<Color>{
    public ColorDao(EntityManager entityManager) {
        super(entityManager, Color.class);
    }
}
