package com.mmdevelopment;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Locale;

import org.mindrot.jbcrypt.BCrypt;

import static com.mmdevelopment.Config.getOfConfiguration;

public class Utilities {

    private Utilities(){

    }

    public static boolean isEmptyOrNull(Object object){
        if (object == null) {
            return true;
        }
        if (object instanceof String string) {
            return string.isEmpty();
        }
        if (object instanceof Collection) {
            return ((Collection<?>) object).isEmpty();
        }
        return false;
    }

    // Method to hash a password
    public static String hashPassword(String plainPassword) {
        // Generate a salt and hash the password
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Method to verify a password
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        // Check if the plain password matches the hashed password
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    public static String getCurrencyFormat(double value) {
        BigDecimal amount = BigDecimal.valueOf(value);
        String language = getOfConfiguration("language");
        String country = getOfConfiguration("country");

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale(language, country));
        return formatter.format(amount);
    }

}
