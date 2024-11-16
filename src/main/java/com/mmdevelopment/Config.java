package com.mmdevelopment;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

@Slf4j
public class Config {
    private static final URL PATH_FILE_CONFIGURATION = Config.class.getResource("/config.properties");
    private static Properties properties = new Properties();

    private Config(){}

    public static boolean runSeeders() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(PATH_FILE_CONFIGURATION.getPath()));
            String valor = properties.getProperty("run_seeders");
            return "true".equals(valor);
        } catch (IOException e) {
            log.error("No se pudo leer el archivo de configuración.");
            e.printStackTrace();
            return false;
        } catch (NullPointerException e) {
            log.error("No se pudo leer el archivo de configuración: verificar ruta");
            e.printStackTrace();
            return false;
        }
    }

    public static String getOfConfiguration(String property) {
        String result = "";
        try {
            properties.load(new FileInputStream(PATH_FILE_CONFIGURATION.getPath()));
            result = properties.getProperty(property);
        } catch (IOException e) {
            log.error("No se pudo leer el archivo de configuración.");
            e.printStackTrace();
        } catch (NullPointerException e) {
            log.error("No se pudo leer el archivo de configuración: verificar ruta");
            e.printStackTrace();
        }
        return result;
    }

    public static String setOfConfiguration(String property, String value) {
        String result = "";
        try {
            properties.load(new FileInputStream(PATH_FILE_CONFIGURATION.getPath()));
            properties.setProperty(property, value);
            properties.store(
                    new FileOutputStream(PATH_FILE_CONFIGURATION.getPath()),
                    "Archivo de configuración de la aplicación"
            );
        } catch (IOException e) {
            log.error("No se pudo leer el archivo de configuración.");
            e.printStackTrace();
        } catch (NullPointerException e) {
            log.error("No se pudo leer el archivo de configuración: verificar ruta");
            e.printStackTrace();
        }
        return result;
    }

}