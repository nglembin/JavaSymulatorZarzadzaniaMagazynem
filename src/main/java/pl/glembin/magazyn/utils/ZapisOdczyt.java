package pl.glembin.magazyn.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.glembin.magazyn.model.Produkt;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ZapisOdczyt {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final File plik = new File("produkty.json");

    public static void zapisz(List<Produkt> produkty) {
        try {
            mapper.writeValue(plik, produkty);
            System.out.println("Dane zapisane.");
        } catch (IOException e) {
            System.out.println("Błąd zapisu: " + e.getMessage());
        }
    }

    public static List<Produkt> wczytaj() {
        try {
            if (!plik.exists()) return List.of();
            return mapper.readValue(plik, new TypeReference<>() {});
        } catch (IOException e) {
            System.out.println("Błąd odczytu: " + e.getMessage());
            return List.of();
        }
    }
}
