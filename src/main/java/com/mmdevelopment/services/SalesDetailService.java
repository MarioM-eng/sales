package com.mmdevelopment.services;

import com.mmdevelopment.models.daos.DAOImpl;
import com.mmdevelopment.models.daos.SalesDetailDao;
import com.mmdevelopment.models.entities.SalesDetail;

public class SalesDetailService extends BaseService<SalesDetail>{
    public SalesDetailService(SalesDetailDao dao) {
        super(SalesDetail.class, dao);
    }
}
