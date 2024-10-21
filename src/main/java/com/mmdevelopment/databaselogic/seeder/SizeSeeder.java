package com.mmdevelopment.databaselogic.seeder;

import com.mmdevelopment.models.daos.SizeDao;
import com.mmdevelopment.models.entities.Size;
import com.mmdevelopment.services.SizeService;
import jakarta.persistence.EntityManager;

import java.util.List;

public class SizeSeeder extends ModelSeeder{
    public SizeSeeder(String code) {
        super(code);
    }

    @Override
    public void runSeeder(EntityManager em) {
        SizeService sizeService = new SizeService(new SizeDao(em));
        sizeService.create(List.of(
                new Size("XS"),
                new Size("S"),
                new Size("M"),
                new Size("L"),
                new Size("XL")
        ));
    }
}
