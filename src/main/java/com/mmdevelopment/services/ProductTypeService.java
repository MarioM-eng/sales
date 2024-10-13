package com.mmdevelopment.services;

import com.mmdevelopment.models.daos.ProductTypeDao;
import com.mmdevelopment.models.entities.ProductType;

public class ProductTypeService extends BaseService<ProductType>{
    public ProductTypeService(ProductTypeDao dao) {
        super(ProductType.class, dao);
    }
}
