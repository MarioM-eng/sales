package com.mmdevelopment.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import static com.mmdevelopment.Config.getOfConfiguration;
import static com.mmdevelopment.Config.setOfConfiguration;
import static com.mmdevelopment.Utilities.checkPassword;
import static com.mmdevelopment.Utilities.hashPassword;

@Slf4j
public class LicenseValidator {

    private static final String LICENSE_KEY_PROPERTY = "app.license.key";

    private boolean isFirstRun() {
        String licenseKey = getOfConfiguration(LICENSE_KEY_PROPERTY);
        return (licenseKey == null || licenseKey.isEmpty());
    }

    private void setLicenseKey() throws IllegalAccessException {
        String macAddress = hashPassword(getMacAddress());
        if (macAddress != null) {
            setOfConfiguration(LICENSE_KEY_PROPERTY, macAddress);
            log.error("Licencia registrada con éxito.");
        } else {
            log.error("No se pudo obtener la dirección MAC.");
            throw new IllegalAccessException("No se pudo obtener la dirección MAC.");
        }
    }

    private String getMacAddress() {
        try {
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            while (networks.hasMoreElements()) {
                NetworkInterface network = networks.nextElement();

                // Verifica que sea un adaptador Ethernet físico
                if (network.isUp() && !network.isVirtual() && !network.isLoopback() &&
                        (network.getDisplayName().contains("Ethernet") || network.getName().contains("eth"))) {

                    byte[] macBytes = network.getHardwareAddress();
                    if (macBytes != null) {
                        StringBuilder macAddress = new StringBuilder();
                        for (byte b : macBytes) {
                            macAddress.append(String.format("%02X:", b));
                        }
                        return macAddress.substring(0, macAddress.length() - 1);
                    }
                }
            }
            return null; // Si no encuentra un adaptador Ethernet
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isLicenseValid() {
        String registeredMac = getOfConfiguration(LICENSE_KEY_PROPERTY);
        String currentMac = getMacAddress();
        return registeredMac != null && checkPassword(currentMac, registeredMac);
    }

    public void validate() throws IllegalAccessException {
        if (isFirstRun()) {
            log.info("Primera vez que se ejecuta la aplicación. Configurando licencia...");
            setLicenseKey();
        } else {
            if (isLicenseValid()) {
                log.info("Licencia válida. Bienvenido.");
            } else {
                log.error("Licencia inválida. Aplicación no autorizada.");
                throw new IllegalAccessException("Licencia inválida. Aplicación no autorizada.");
            }
        }
    }

}
