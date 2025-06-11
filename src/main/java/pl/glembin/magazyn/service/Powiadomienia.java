package pl.glembin.magazyn.service;

import pl.glembin.magazyn.model.Produkt;

import java.util.List;

public class Powiadomienia {
    public static void sprawdzStany(List<Produkt> produkty, double progCenowy) {
        System.out.println("\n📢 Produkty poniżej ceny " + progCenowy + " zł:");
        boolean znaleziono = false;

        for (Produkt p : produkty) {
            if (p.getCena() < progCenowy) {
                System.out.println(p);
                znaleziono = true;
            }
        }

        if (!znaleziono) {
            System.out.println("Brak produktów poniżej podanej wartości.");
        }
    }
}