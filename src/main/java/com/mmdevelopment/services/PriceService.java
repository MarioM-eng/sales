package com.mmdevelopment.services;

import com.mmdevelopment.models.daos.PriceDao;
import com.mmdevelopment.models.entities.Price;

public class PriceService extends BaseService<Price> {
    public PriceService(PriceDao dao) {
        super(Price.class, dao);
    }
}
