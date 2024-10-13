package com.mmdevelopment.services;

import com.mmdevelopment.models.converters.Converter;
import com.mmdevelopment.models.daos.CategoryDao;
import com.mmdevelopment.models.entities.Category;
import com.mmdevelopment.utils.ServiceTransactionManager;
import com.mmdevelopment.utils.exceptions.NonexistentEntityException;
import jakarta.persistence.EntityManager;
import lombok.Getter;

import java.util.List;

public class CategoryService extends BaseService<Category> {

    @Getter
    private CategoryDao dao;

    public CategoryService(CategoryDao dao) {
        super(Category.class, dao);
        this.dao = dao;
    }

    public void create(List<Category> categories) {
        ServiceTransactionManager.executeInTransaction(
                new ServiceOperation<Category>() {
                    @Override
                    public Category execute(EntityManager em) throws NonexistentEntityException {
                        getDao().create(categories);
                        return null;
                    }
                }
        );
    }

}
