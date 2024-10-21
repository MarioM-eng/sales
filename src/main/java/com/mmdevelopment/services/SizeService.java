package com.mmdevelopment.services;

import com.mmdevelopment.models.daos.SizeDao;
import com.mmdevelopment.models.entities.Size;
import com.mmdevelopment.utils.ServiceTransactionManager;
import com.mmdevelopment.utils.exceptions.NonexistentEntityException;
import jakarta.persistence.EntityManager;

import java.util.List;

public class SizeService extends BaseService<Size>{
    public SizeService(SizeDao dao) {
        super(Size.class, dao);
    }

    public void create(List<Size> sizes) {
        ServiceTransactionManager.executeInTransaction(
                new ServiceOperation<Size>() {
                    @Override
                    public Size execute(EntityManager em) throws NonexistentEntityException {
                        getDao().create(sizes);
                        return null;
                    }
                }
        );
    }

}
