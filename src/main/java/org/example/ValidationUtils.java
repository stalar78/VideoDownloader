package org.example;

public class ValidationUtils {
    public static boolean isValidUrl(String url) {
        // Простейшая проверка корректности URL (можно усложнить при необходимости)
        return url != null && url.matches("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$");
    }

    public static boolean areFieldsFilled(String... fields) {
        for (String field : fields) {
            if (field == null || field.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}