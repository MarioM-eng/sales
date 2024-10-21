package com.mmdevelopment.utils.factories;

import com.mmdevelopment.databaselogic.JPAUtil;


public class H2ServiceFactory extends AbstractServiceFactory {

    private static final H2ServiceFactory singleton = new H2ServiceFactory();

    private H2ServiceFactory(){
        super(JPAUtil.getSession());
    }

    public static H2ServiceFactory getInstance() {
        return  singleton;
    }

}
