package com.mmdevelopment.databaselogic.seeder;

import com.mmdevelopment.models.daos.DAOImpl;
import com.mmdevelopment.models.daos.ProductTypeDao;
import com.mmdevelopment.models.entities.ProductType;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

public class ProductTypeSeeder extends ModelSeeder{
    public ProductTypeSeeder(String code) {
        super(code);
    }

    @Override
    public void runSeeder(EntityManager em) {
        ProductTypeDao productTypeDao = new ProductTypeDao(em);
        List<ProductType> productTypes = new ArrayList<>();
        ProductType productType;

        productType = new ProductType("Jeans");
        productTypes.add(productType);

        productType = new ProductType("Blusa");
        productTypes.add(productType);

        productType = new ProductType("Vestidos");
        productTypes.add(productType);

        productTypeDao.create(productTypes);
    }
}
