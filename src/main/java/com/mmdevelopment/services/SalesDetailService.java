package com.mmdevelopment.services;

import com.mmdevelopment.models.daos.DAOImpl;
import com.mmdevelopment.models.daos.SalesDetailDao;
import com.mmdevelopment.models.entities.SalesDetail;

import java.util.List;

public class SalesDetailService extends BaseService<SalesDetail>{
    public SalesDetailService(SalesDetailDao dao) {
        super(SalesDetail.class, dao);
    }

    public double getTotalSaleDetails(List<SalesDetail> salesDetails) {
        return salesDetails
                .stream()
                .map(
                        salesDetail -> salesDetail.getStock().getPrices()
                                .stream()
                                .filter(price -> price.getPriceType().getPrefix().equals("retailPrice"))
                                .findFirst().get().getValue() * salesDetail.getQuantity()
                ).mapToDouble(Double::doubleValue).sum();
    }
}
