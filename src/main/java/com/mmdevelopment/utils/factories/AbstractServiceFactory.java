package com.mmdevelopment.utils.factories;

import com.mmdevelopment.models.daos.*;
import com.mmdevelopment.services.*;
import jakarta.persistence.EntityManager;

public abstract class AbstractServiceFactory {

    protected static EntityManager entityManager;

    public AbstractServiceFactory(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public CategoryService getCategoryService() {
        CategoryDao dao = new CategoryDao(entityManager);
        CategoryService service = new CategoryService(dao);
        return service;
    }

    public ColorService getColorService() {
        ColorDao dao = new ColorDao(entityManager);
        ColorService service = new ColorService(dao);
        return service;
    }

    public PriceService getPriceService() {
        PriceDao dao = new PriceDao(entityManager);
        PriceService service = new PriceService(dao);
        return service;
    }

    public PriceTypeService getPriceTypeService() {
        PriceTypeDao dao = new PriceTypeDao(entityManager);
        PriceTypeService service = new PriceTypeService(dao);
        return service;
    }

    public ProductService getProductService() {
        ProductDao dao = new ProductDao(entityManager);
        ProductService service = new ProductService(dao);
        return service;
    }

    public ProductTypeService getProductTypeService() {
        ProductTypeDao dao = new ProductTypeDao(entityManager);
        ProductTypeService service = new ProductTypeService(dao);
        return service;
    }

    public SizeService getSizeService() {
        SizeDao dao = new SizeDao(entityManager);
        SizeService service = new SizeService(dao);
        return service;
    }

    public StockService getStockService() {
        StockDao dao = new StockDao(entityManager);
        StockService service = new StockService(dao);
        return service;
    }

}
