package pl.glembin.magazyn.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Klasa pomocnicza do ładowania konfiguracji z pliku config.properties.
 */

public class Config {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("Nie znaleziono pliku config.properties!");
            } else {
                properties.load(input);
            }
        } catch (IOException e) {
            System.err.println("Błąd podczas ładowania konfiguracji: " + e.getMessage());
        }
    }

    public static String get(String klucz) {
        return properties.getProperty(klucz);
    }

    public static boolean getBoolean(String klucz) {
        return Boolean.parseBoolean(properties.getProperty(klucz));
    }
}
