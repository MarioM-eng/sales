package com.mmdevelopment.services;

import com.mmdevelopment.models.daos.ColorDao;
import com.mmdevelopment.models.entities.Color;
import com.mmdevelopment.utils.ServiceTransactionManager;
import com.mmdevelopment.utils.exceptions.NonexistentEntityException;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ColorService extends BaseService<Color>{
    public ColorService(ColorDao dao) {
        super(Color.class, dao);
    }
    public void create(List<Color> colors) {
        ServiceTransactionManager.executeInTransaction(
                new ServiceOperation<Color>() {
                    @Override
                    public Color execute(EntityManager em) throws NonexistentEntityException {
                        getDao().create(colors);
                        return null;
                    }
                }
        );
    }

}
