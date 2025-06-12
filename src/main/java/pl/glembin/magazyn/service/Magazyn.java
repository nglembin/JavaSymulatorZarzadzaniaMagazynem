package pl.glembin.magazyn.service;
import pl.glembin.magazyn.utils.Config;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.glembin.magazyn.model.*;

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
    private final File plik = new File("produkty.json");

    /**
     * Dodaje nowy produkt do magazynu wraz z danymi dostawcy i minimalną ilością.
     * @param scanner Obiekt do wczytywania danych od użytkownika.
     */

    public void dodajProdukt(Scanner scanner) {
        System.out.print("Nazwa: ");
        String nazwa = scanner.nextLine();
        System.out.print("Kod: ");
        String kod = scanner.nextLine();
        System.out.print("Cena: ");
        double cena = Double.parseDouble(scanner.nextLine());
        System.out.print("Jednostka: ");
        String jednostka = scanner.nextLine();
        System.out.print("Opis: ");
        String opis = scanner.nextLine();
        System.out.print("Kategoria: ");
        String kategoria = scanner.nextLine();
        System.out.print("Minimalna ilość (powiadomienie): ");
        int minimum = Integer.parseInt(scanner.nextLine());

        System.out.println("== DOSTAWCA ==");
        System.out.print("Nazwa dostawcy: ");
        String nazwaD = scanner.nextLine();
        System.out.print("Adres: ");
        String adres = scanner.nextLine();
        System.out.print("Kontakt: ");
        String kontakt = scanner.nextLine();
        Dostawca d = new Dostawca(nazwaD, adres, kontakt);

        Produkt p = new Produkt(nazwa, kod, cena, jednostka, opis, kategoria, d);
        p.setMinimum(minimum);
        System.out.print("Ilość początkowa: ");
        p.setIlosc(Integer.parseInt(scanner.nextLine()));

        produkty.add(p);
        System.out.println("Produkt dodany.");
    }

    /**
     * Wyświetla wszystkie produkty znajdujące się w magazynie.
     */

    public void wyswietlProdukty() {
        if (produkty.isEmpty()) {
            System.out.println("Brak produktów.");
            return;
        }
        Produkt.wypiszListe(produkty);
    }

    /**
     * Usuwa produkt z magazynu na podstawie jego kodu.
     * @param scanner Obiekt do wczytywania danych od użytkownika.
     */

    public void usunProdukt(Scanner scanner) {
        System.out.print("Kod produktu do usunięcia: ");
        String kod = scanner.nextLine();
        boolean usunieto = produkty.removeIf(p -> p.getKod().equals(kod));
        System.out.println(usunieto ? "Usunięto." : "Nie znaleziono.");
    }

    /**
     * Zapisuje listę produktów do pliku.
     */

    public void zapiszDoPliku() {
        String sciezka = Config.get("raport.sciezka"); // czytaj z config.properties
        try {
            mapper.writeValue(new File(sciezka), produkty);
            System.out.println("Zapisano do pliku: " + sciezka);
        } catch (IOException e) {
            System.out.println("Błąd zapisu: " + e.getMessage());
        }
    }

    /**
     * Wczytuje produkty z pliku JSON i aktualizuje stan magazynu.
     */

    public void wczytajZPliku() {
        if (!plik.exists()) {
            System.out.println("Brak pliku.");
            return;
        }
        try {
            List<Produkt> wczytane = mapper.readValue(plik, new TypeReference<>() {});
            produkty.clear();
            produkty.addAll(wczytane);
            System.out.println("Wczytano.");
        } catch (IOException e) {
            System.out.println("Błąd odczytu: " + e.getMessage());
        }
    }

    /**
     * Wyszukuje produkty pasujące do podanej frazy (nazwa, kod, opis, dostawca).
     * @param scanner Obiekt do wczytywania danych od użytkownika.
     */

    public void wyszukaj(Scanner scanner) {
        System.out.print("Wpisz szukaną frazę: ");
        String fraza = scanner.nextLine();
        produkty.stream()
                .filter(p -> p.pasujeDoWyszukiwania(fraza))
                .forEach(System.out::println);
    }

    /**
     * Sortuje listę produktów według wybranego kryterium.
     * @param scanner Obiekt do wczytywania danych od użytkownika.
     */

    public void sortuj(Scanner scanner) {
        System.out.println("Sortuj po: 1-nazwa, 2-kod, 3-cena, 4-ilość");
        String wybor = scanner.nextLine();
        Comparator<Produkt> komparator = switch (wybor) {
            case "1" -> Produkt.sortujPoNazwie();
            case "2" -> Produkt.sortujPoKodzie();
            case "3" -> Produkt.sortujPoCenie();
            case "4" -> Produkt.sortujPoIlosci();
            default -> null;
        };
        if (komparator != null) {
            produkty.sort(komparator);
            System.out.println("Posortowano.");
        } else {
            System.out.println("Nieznane kryterium.");
        }
    }

    /**
     * Wyświetla listę produktów, których ilość jest mniejsza niż określone minimum.
     */


    public void pokazNiskieStany() {
        produkty.stream()
                .filter(Produkt::czyPonizejMinimum)
                .forEach(p -> System.out.println("🔔 " + p.getNazwa() + " (" + p.getIlosc() + " < " + p.getMinimum() + ")"));
    }

    /**
     * Przyjmuje dostawę danego produktu, aktualizując jego ilość i zapisując transakcję.
     * @param scanner Obiekt do wczytywania danych od użytkownika.
     */

    public void przyjmijDostawę(Scanner scanner) {
        System.out.print("Podaj kod produktu: ");
        String kod = scanner.nextLine();
        Produkt produkt = znajdzProdukt(kod);
        if (produkt == null) {
            System.out.println("Nie znaleziono produktu.");
            return;
        }
        System.out.print("Ilość przyjęcia: ");
        int ile = Integer.parseInt(scanner.nextLine());
        produkt.przyjmij(ile);
        historia.add(new Transakcja(LocalDate.now(), kod, ile, TypTransakcji.PRZYJECIE));
        System.out.println("Przyjęto dostawę.");
    }

    public void wydajTowar(Scanner scanner) {
        System.out.print("Podaj kod produktu: ");
        String kod = scanner.nextLine();
        Produkt produkt = znajdzProdukt(kod);
        if (produkt == null) {
            System.out.println("Nie znaleziono produktu.");
            return;
        }
        System.out.print("Ilość do wydania: ");
        int ile = Integer.parseInt(scanner.nextLine());
        if (produkt.wydaj(ile)) {
            historia.add(new Transakcja(LocalDate.now(), kod, ile, TypTransakcji.WYDANIE));
            System.out.println("Wydano towar.");
        } else {
            System.out.println("Brak wystarczającej ilości.");
        }
    }

    /**
     * Wyszukuje produkt po jego kodzie.
     * @param kod Kod produktu.
     * @return Obiekt Produkt lub null jeśli nie znaleziono.
     */

    private Produkt znajdzProdukt(String kod) {
        return produkty.stream()
                .filter(p -> p.getKod().equalsIgnoreCase(kod))
                .findFirst()
                .orElse(null);
    }

    /**
     * Generuje raport zawierający wszystkie produkty i historię transakcji.
     */

    public void generujRaport() {
        try (FileWriter writer = new FileWriter("raport.txt")) {
            writer.write("=== RAPORT PRODUKTÓW ===\n");
            for (Produkt p : produkty) {
                writer.write(p + "\n\n");
            }
            writer.write("=== HISTORIA TRANSAKCJI ===\n");
            for (Transakcja t : historia) {
                writer.write(t + "\n");
            }
            System.out.println("Raport zapisany do 'raport.txt'");
        } catch (IOException e) {
            System.out.println("Błąd zapisu raportu: " + e.getMessage());
        }
    }

    /**
     * Wyświetla powiadomienia o produktach poniżej minimalnego stanu magazynowego.
     */

    public void wyswietlPowiadomienia() {
        boolean znaleziono = false;
        for (Produkt p : produkty) {
            if (p.czyPonizejMinimum()) {
                System.out.println("🔔 " + p.getNazwa() + " (" + p.getIlosc() + "/" + p.getMinimum() + ")");
                znaleziono = true;
            }
        }
        if (!znaleziono) {
            System.out.println("Wszystkie produkty powyżej minimalnego stanu.");
        }
    }
    public void edytujProdukt(Scanner scanner) {
        System.out.print("Podaj kod produktu do edycji: ");
        String kod = scanner.nextLine();
        Produkt p = znajdzProdukt(kod);
        if (p == null) {
            System.out.println("Nie znaleziono produktu.");
            return;
        }

        System.out.print("Nowa nazwa [" + p.getNazwa() + "]: ");
        String nowaNazwa = scanner.nextLine();
        if (!nowaNazwa.isBlank()) p.setNazwa(nowaNazwa);

        System.out.print("Nowa cena [" + p.getCena() + "]: ");
        String nowaCena = scanner.nextLine();
        if (!nowaCena.isBlank()) p.setCena(Double.parseDouble(nowaCena));

        System.out.print("Nowy opis [" + p.getOpis() + "]: ");
        String nowyOpis = scanner.nextLine();
        if (!nowyOpis.isBlank()) p.setOpis(nowyOpis);

        System.out.print("Nowa kategoria [" + p.getKategoria() + "]: ");
        String nowaKategoria = scanner.nextLine();
        if (!nowaKategoria.isBlank()) p.setKategoria(nowaKategoria);

        System.out.print("Nowe minimum [" + p.getMinimum() + "]: ");
        String noweMinimum = scanner.nextLine();
        if (!noweMinimum.isBlank()) p.setMinimum(Integer.parseInt(noweMinimum));

        logger.info("Edytowano produkt: {}", kod);
        System.out.println("Zaktualizowano dane produktu.");
    }

    /**
     * Wyświetla historię transakcji (przyjęć i wydań) dla podanego produktu.
     */
    public void historiaProduktu(Scanner scanner) {
        System.out.print("Podaj kod produktu: ");
        String kod = scanner.nextLine();
        boolean znaleziono = false;
        for (Transakcja t : historia) {
            if (t.getKodProduktu().equalsIgnoreCase(kod)) {
                System.out.println(t);
                znaleziono = true;
            }
        }
        if (!znaleziono) {
            System.out.println("Brak historii dla tego produktu.");


        }
    }
    /**
     * Tworzy raport w osobnym wątku, bez blokowania głównego programu.
     */
    public void generujRaportAsynchronicznie() {
        new Thread(() -> {
            logger.info("Rozpoczęto asynchroniczne generowanie raportu...");
            generujRaport();
            logger.info("Zakończono generowanie raportu (async).");
        }).start();
    }

    public Produkt utworzProduktZKonsoli(Scanner scanner) {
        System.out.print("Nazwa: ");
        String nazwa = scanner.nextLine();
        System.out.print("Kod: ");
        String kod = scanner.nextLine();
        System.out.print("Cena: ");
        double cena = Double.parseDouble(scanner.nextLine());
        System.out.print("Jednostka: ");
        String jednostka = scanner.nextLine();
        System.out.print("Opis: ");
        String opis = scanner.nextLine();
        System.out.print("Kategoria: ");
        String kategoria = scanner.nextLine();
        System.out.print("Minimalna ilość: ");
        int min = Integer.parseInt(scanner.nextLine());
        System.out.print("Ilość: ");
        int ilosc = Integer.parseInt(scanner.nextLine());

        System.out.println("== DOSTAWCA ==");
        System.out.print("Nazwa: ");
        String nazwad = scanner.nextLine();
        System.out.print("Adres: ");
        String adres = scanner.nextLine();
        System.out.print("Kontakt: ");
        String kontakt = scanner.nextLine();

        Produkt p = new Produkt(nazwa, kod, cena, jednostka, opis, kategoria, new Dostawca(nazwad, adres, kontakt));
        p.setMinimum(min);
        p.setIlosc(ilosc);
        return p;
    }

    /**
     * Metoda pomocnicza do testów jednostkowych – zwraca listę produktów.
     */
    public List<Produkt> getProdukty() {
        return produkty;
    }
}

