package com.mmdevelopment.databaselogic.seeder;

import com.mmdevelopment.databaselogic.JPAUtil;
import com.mmdevelopment.models.daos.PriceTypeDao;
import com.mmdevelopment.models.entities.PriceType;
import com.mmdevelopment.services.PriceTypeService;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

public class PriceTypeSeeder extends ModelSeeder{
    public PriceTypeSeeder(String code) {
        super(code);
    }

    @Override
    public void runSeeder(EntityManager em) {
        PriceTypeService priceTypeService = new PriceTypeService(new PriceTypeDao(JPAUtil.getSession()));
        List<PriceType> priceTypes = new ArrayList<>();
        priceTypes.add(new PriceType(
                "Precio de costo",
                "Este es el precio por el cual pagó por adquirir el producto",
                "costPrice"
        ));
        priceTypes.add(new PriceType(
                "Precio de venta al público",
                "Este es el precio al que vende el producto a los clientes",
                "retailPrice"
        ));
        priceTypeService.create(priceTypes);
    }
}
