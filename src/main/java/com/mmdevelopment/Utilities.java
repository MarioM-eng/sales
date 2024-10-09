package com.mmdevelopment;

import java.util.Collection;
import org.mindrot.jbcrypt.BCrypt;

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

}
