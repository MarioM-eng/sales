package com.mmdevelopment.databaselogic.seeder;

import jakarta.persistence.EntityManager;

public abstract class ModelSeeder {

    private String code;

    public ModelSeeder(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public abstract void runSeeder(EntityManager em);

}
