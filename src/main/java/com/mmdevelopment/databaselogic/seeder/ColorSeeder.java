package com.mmdevelopment.databaselogic.seeder;

import com.mmdevelopment.models.daos.ColorDao;
import com.mmdevelopment.models.entities.Color;
import com.mmdevelopment.services.ColorService;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ColorSeeder extends ModelSeeder{
    public ColorSeeder(String code) {
        super(code);
    }

    @Override
    public void runSeeder(EntityManager em) {
        ColorService colorService = new ColorService(new ColorDao(em));
        colorService.create(List.of(
                new Color("Amarillo"),
                new Color("Azul"),
                new Color("Rojo"),
                new Color("Blanco"),
                new Color("Negro"),
                new Color("Verde"),
                new Color("Naranja"),
                new Color("Morado")
        ));
    }
}
