package com.mmdevelopment.databaselogic.seeder;

import com.mmdevelopment.models.daos.CategoryDao;
import com.mmdevelopment.models.entities.Category;
import com.mmdevelopment.services.CategoryService;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

public class CategorySeeder extends ModelSeeder{
    public CategorySeeder(String code) {
        super(code);
    }

    @Override
    public void runSeeder(EntityManager em) {

        CategoryService categoryService = new CategoryService(new CategoryDao(em));
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Hombre"));
        categories.add(new Category("Mujer"));
        categories.add(new Category("Niño"));
        categories.add(new Category("Niña"));
        categories.add(new Category("Unisex"));
        categories.add(new Category("Unisex - niños"));
        categoryService.create(categories);

    }
}
