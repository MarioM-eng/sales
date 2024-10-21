package com.mmdevelopment.models.daos;

import com.mmdevelopment.models.entities.Size;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.List;

public class SizeDao extends DAOImpl<Size>{
    public SizeDao(EntityManager entityManager) {
        super(entityManager, Size.class);
    }
}
