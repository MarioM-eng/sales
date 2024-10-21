package com.mmdevelopment.services;

import com.mmdevelopment.models.daos.PriceTypeDao;
import com.mmdevelopment.models.entities.Category;
import com.mmdevelopment.models.entities.PriceType;
import com.mmdevelopment.utils.ServiceTransactionManager;
import com.mmdevelopment.utils.exceptions.NonexistentEntityException;
import jakarta.persistence.EntityManager;

import java.util.List;

public class PriceTypeService extends BaseService<PriceType>{
    public PriceTypeService(PriceTypeDao dao) {
        super(PriceType.class, dao);
    }

    public void create(List<PriceType> priceTypes) {
        ServiceTransactionManager.executeInTransaction(
                new ServiceOperation<PriceType>() {
                    @Override
                    public PriceType execute(EntityManager em) throws NonexistentEntityException {
                        getDao().create(priceTypes);
                        return null;
                    }
                }
        );
    }

}
