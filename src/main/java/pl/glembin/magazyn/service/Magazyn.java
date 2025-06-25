package pl.glembin.magazyn.service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.glembin.magazyn.model.*;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/**
 * Klasa Magazyn odpowiada za logikę zarządzania produktami w systemie.
 * Umożliwia dodawanie, usuwanie, wyszukiwanie, sortowanie i zapisywanie danych,
 * a także obsługę transakcji magazynowych i generowanie raportów.
 */

public class Magazyn {
    private static final Logger logger = LoggerFactory.getLogger(Magazyn.class);
    private final List<Produkt> produkty = new ArrayList<>();
    private final List<Transakcja> historia = new ArrayList<>();
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Dodaje nowy produkt do magazynu wraz z danymi dostawcy i minimalną ilością.
     */

    public boolean dodajProdukt(Produkt produkt) {
        for (Produkt istniejący : produkty) {
            if (istniejący.getKod().equalsIgnoreCase(produkt.getKod())) {
                return false; // produkt już istnieje
            }
        }
        produkty.add(produkt);
        zapiszDoPliku("produkty.json");
        wykonajKopieZapasowa();
        return true; // dodano
    }

    /**
     * Wyświetla wszystkie produkty znajdujące się w magazynie.
     */

    public List<Produkt> getProdukty() {
        return new ArrayList<>(produkty);
    }

    /**
     * Usuwa produkt z magazynu na podstawie jego kodu.
     */

    public boolean usunProdukt(String kod) {
        boolean usunieto = produkty.removeIf(p -> p.getKod().equalsIgnoreCase(kod));
        if (usunieto) {
            zapiszDoPliku("produkty.json");
            wykonajKopieZapasowa(); // <- dodaj backup
        }
        return usunieto;
    }

    /**
     * Zapisuje listę produktów do pliku.
     */

    public boolean zapiszDoPliku(String sciezka) {
        try {
            mapper.writeValue(new File(sciezka), produkty);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Wczytuje produkty z pliku JSON i aktualizuje stan magazynu.
     */

    public boolean wczytajZPliku(String sciezka) {
        File plik = new File(sciezka);
        if (!plik.exists()) {
            return false;
        }
        try {
            List<Produkt> wczytane = mapper.readValue(plik, new TypeReference<>() {});
            produkty.clear();
            produkty.addAll(wczytane);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Wyszukuje produkty pasujące do podanej frazy (nazwa, kod, opis, dostawca). Do wyszukiwania tolowerCase, a nie equalsIgnoreCase żeby jak wpiszemy "lap" wyszukało Laptop
     */

    public List<Produkt> wyszukaj(String fraza, String pole) {
        String szukana = fraza.toLowerCase();

        return produkty.stream()
                .filter(p -> {
                    return switch (pole.toLowerCase()) {
                        case "nazwa" -> p.getNazwa().toLowerCase().contains(szukana);
                        case "kod" -> p.getKod().toLowerCase().contains(szukana);
                        case "opis" -> p.getOpis().toLowerCase().contains(szukana);
                        case "dostawca" -> p.getDostawca() != null &&
                                p.getDostawca().getNazwa().toLowerCase().contains(szukana);
                        default -> false;
                    };
                })
                .toList();
    }


    /**
     * Sortuje listę produktów według wybranego kryterium.
     */

    public void sortujProdukty(Comparator<Produkt> komparator) {
        produkty.sort(komparator);
    }

    /**
     * Wyświetla listę produktów, których ilość jest mniejsza niż określone minimum.
     */

    public List<Produkt> znajdzNiskieStany() {
        return produkty.stream()
                .filter(Produkt::czyPonizejMinimum)
                .toList(); // Java 16+ albo użyj .collect(Collectors.toList()) jeśli masz starszą
    }

    /**
     * Generuje raport zawierający wszystkie produkty i historię transakcji.
     */

    public boolean generujRaportDoPliku(String nazwaPliku) {
        try (FileWriter writer = new FileWriter(nazwaPliku)) {
            writer.write("=== RAPORT PRODUKTÓW ===\n");
            for (Produkt p : produkty) {
                writer.write(p + "\n\n");
            }
            writer.write("=== HISTORIA TRANSAKCJI ===\n");
            for (Transakcja t : historia) {
                writer.write(t + "\n");
            }
            return true; // sukces
        } catch (IOException e) {
            return false; // błąd
        }
    }

    /**
     * Przyjmuje dostawę danego produktu, aktualizując jego ilość i zapisując transakcję.
     */

    public boolean przyjmijDostawe(String kodProduktu, int ilosc, String dostawca) {
        Produkt produkt = znajdzProdukt(kodProduktu);
        if (produkt == null || ilosc <= 0) return false;

        produkt.przyjmij(ilosc);
        historia.add(new Przyjecie(LocalDate.now(), kodProduktu, ilosc, dostawca));
        zapiszDoPliku("produkty.json");
        wykonajKopieZapasowa();
        return true;
    }

    /**
     * Wydaje towar z magazynu, aktualizując stan i zapisując transakcję.
     * Zwraca:
     *   0 – nie znaleziono produktu
     *   1 – za mało na stanie
     *   2 – sukces
     */

    public int wydajTowar(String kodProduktu, int ilosc, String odbiorca) {
        Produkt produkt = znajdzProdukt(kodProduktu);
        if (produkt == null) return 0;
        if (!produkt.wydaj(ilosc)) return 1;

        historia.add(new Wydanie(LocalDate.now(), kodProduktu, ilosc, odbiorca));
        zapiszDoPliku("produkty.json");
        wykonajKopieZapasowa();
        return produkt.czyPonizejMinimum() ? 3 : 2;
    }


    /**
     * Wyszukuje produkt po jego kodzie.
     */

    public Produkt znajdzProdukt(String kod) {
        return produkty.stream()
                .filter(p -> p.getKod().equalsIgnoreCase(kod))
                .findFirst()
                .orElse(null);
    }

    /**
     * Wyświetla powiadomienia o produktach poniżej minimalnego stanu magazynowego.
     */

    public List<Produkt> znajdzProduktyPonizejMinimum() {
        return produkty.stream()
                .filter(Produkt::czyPonizejMinimum)
                .toList();
    }

    /**
     * Edytuj produkt
     */

    public boolean edytujProdukt(String kod, Produkt nowyStan) {
        for (int i = 0; i < produkty.size(); i++) {
            Produkt p = produkty.get(i);
            if (p.getKod().equalsIgnoreCase(kod)) {
                p.setNazwa(nowyStan.getNazwa());
                p.setCena(nowyStan.getCena());
                p.setJednostka(nowyStan.getJednostka());
                p.setOpis(nowyStan.getOpis());
                p.setKategoria(nowyStan.getKategoria());
                p.setMinimum(nowyStan.getMinimum());
                p.setIlosc(nowyStan.getIlosc());
                p.setDostawca(nowyStan.getDostawca());
                zapiszDoPliku("produkty.json");
                wykonajKopieZapasowa();
                return true;
            }
        }
        return false;
    }

    /**
     * Uruchamia w osobnym wątku logowanie stanu magazynu.
     * Dzięki temu główny wątek (UI) nie jest blokowany.
     * Pokazuje użycie prostego mechanizmu wielowątkowości w Javie.
     */

    public void logujStanAsynchronicznie() {
        new Thread(() -> {
            int liczbaProduktow = produkty.size();
            long niskieStany = produkty.stream()
                    .filter(Produkt::czyPonizejMinimum)
                    .count();
            logger.info("Asynchronicznie loguję stan magazynu...");
            logger.info("Liczba produktów: " + liczbaProduktow);
            logger.info("Produkty poniżej minimum: " + niskieStany);
        }).start();
    }

    /**
     * Wyświetla historię transakcji (przyjęć i wydań) dla podanego produktu.
     */

    public List<Transakcja> historiaProduktu(String kod) {
        return historia.stream()
                .filter(t -> t.getKodProduktu().equalsIgnoreCase(kod))
                .collect(Collectors.toList());
    }


    /**
     * Filtrowanie produktów
     */

    public List<Produkt> filtrujPoKategorii(String kategoria) {
        return produkty.stream()
                .filter(p -> p.getKategoria().equalsIgnoreCase(kategoria))
                .toList(); // dla starszych JDK użyj .collect(Collectors.toList())
    }

    public List<Produkt> filtrujPoIlosci(int minIlosc) {
        return produkty.stream()
                .filter(p -> p.getIlosc() >= minIlosc)
                .toList();
    }

    public List<Produkt> filtrujPoCenie(double maksCena) {
        return produkty.stream()
                .filter(p -> p.getCena() <= maksCena)
                .toList();
    }

    /**
     * Auto zapisy i backupy
     */

    public void wykonajKopieZapasowa() {
        File oryginal = new File("produkty.json");
        File backup = new File("produkty_backup_" + LocalDate.now() + ".json");
        try {
            Files.copy(oryginal.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            logger.info("Utworzono kopię zapasową: " + backup.getName());
        } catch (IOException e) {
            logger.error("Błąd przy tworzeniu kopii zapasowej", e);
        }
    }

    /**
     * Generuje raport produktów dla danej kategorii i zapisuje do pliku.
     */

    public void raportProduktowPoKategorii(String kategoria, String nazwaPliku) throws IOException {
        List<Produkt> wynik = filtrujPoKategorii(kategoria);

        try (PrintWriter writer = new PrintWriter(nazwaPliku)) {
            writer.println("=== RAPORT PRODUKTÓW W KATEGORII: " + kategoria.toUpperCase() + " ===");

            if (wynik.isEmpty()) {
                writer.println("Brak produktów w tej kategorii.");
            } else {
                for (Produkt p : wynik) {
                    writer.println(p);
                    writer.println("--------------------------------------");
                }
            }

            writer.println("Data wygenerowania: " + LocalDate.now());
        }
    }

    /**
     * Metoda pomocnicza do testów jednostkowych – zwraca listę produktów.
     */

    public List<Produkt> getProdukty2() {
        return new ArrayList<>(produkty); // zwracamy kopię listy
    }
}

