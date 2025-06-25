package pl.glembin.magazyn;

import pl.glembin.magazyn.db.DatabaseManager;
import pl.glembin.magazyn.model.Dostawca;
import pl.glembin.magazyn.model.Produkt;
import pl.glembin.magazyn.model.Transakcja;
import pl.glembin.magazyn.service.Magazyn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Klasa Main zawiera główną metodę programu, która uruchamia aplikację konsolową
 * do zarządzania magazynem i umożliwia użytkownikowi wykonywanie operacji
 * za pomocą menu tekstowego.
 */

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Magazyn magazyn = new Magazyn();
        DatabaseManager db = new DatabaseManager(); // Manager bazy

        boolean trwa = true;
        while (trwa) {
            System.out.println("\n==== MENU MAGAZYNU ====");
            System.out.println("1. Dodaj produkt");
            System.out.println("2. Pokaż produkty");
            System.out.println("3. Usuń produkt");
            System.out.println("4. Zapisz do pliku");
            System.out.println("5. Wczytaj z pliku");
            System.out.println("6. Wyszukaj produkt");
            System.out.println("7. Sortuj produkty");
            System.out.println("8. Wygeneruj raport produktów z wybranej kategorii do pliku");
            System.out.println("9. Wygeneruj raport do pliku");
            System.out.println("10. Przyjmij dostawę");
            System.out.println("11. Wydaj towar");
            System.out.println("12. Wyświetl powiadomienia o brakach");
            System.out.println("13. Edytuj produkt");
            System.out.println("14. Historia produktu");
            System.out.println("15. Zaloguj informacje o stanie magazynu (w tle, wielowątkowość)");
            System.out.println("16. Zapisz produkt do bazy danych");
            System.out.println("17. Wczytaj produkt z bazy danych");
            System.out.println("18. Usuń produkt z bazy danych");
            System.out.println("19. Wyświetl wszystkie produkty z bazy");
            System.out.println("20. Filtruj produkty");
            System.out.println("0. Wyjście");
            System.out.print("Wybierz opcję: ");

            switch (scanner.nextLine()) {
                case "1" -> {
                    System.out.print("Nazwa: ");
                    String nazwa = scanner.nextLine().trim();
                    if (nazwa.isEmpty()) {
                        System.out.println("Nazwa nie może być pusta!");
                        break;
                    }

                    System.out.print("Kod: ");
                    String kod = scanner.nextLine().trim();
                    if (kod.isEmpty()) {
                        System.out.println("Kod nie może być pusty!");
                        break;
                    }

                    double cena;
                    try {
                        System.out.print("Cena: ");
                        String cenaInput = scanner.nextLine().trim();
                        if (cenaInput.isEmpty()) throw new NumberFormatException();
                        cena = Double.parseDouble(cenaInput);
                        if (cena <= 0) {
                            System.out.println("Cena musi być większa niż 0!");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Nieprawidłowa cena.");
                        break;
                    }

                    System.out.print("Jednostka: ");
                    String jednostka = scanner.nextLine().trim();
                    if (jednostka.isEmpty()) {
                        System.out.println("Jednostka nie może być pusta!");
                        break;
                    }

                    System.out.print("Opis: ");
                    String opis = scanner.nextLine().trim();
                    if (opis.isEmpty()) {
                        System.out.println("Opis nie może być pusty!");
                        break;
                    }

                    System.out.print("Kategoria: ");
                    String kategoria = scanner.nextLine().trim();
                    if (kategoria.isEmpty()) {
                        System.out.println("Kategoria nie może być pusta!");
                        break;
                    }

                    int minimum;
                    try {
                        System.out.print("Minimalna ilość (powiadomienie): ");
                        String minInput = scanner.nextLine().trim();
                        if (minInput.isEmpty()) throw new NumberFormatException();
                        minimum = Integer.parseInt(minInput);
                        if (minimum < 0) {
                            System.out.println("Minimalna ilość nie może być ujemna!");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Nieprawidłowa wartość minimalna.");
                        break;
                    }

                    System.out.println("== DOSTAWCA ==");

                    System.out.print("Nazwa dostawcy: ");
                    String nazwaD = scanner.nextLine().trim();
                    if (nazwaD.isEmpty()) {
                        System.out.println("Nazwa dostawcy nie może być pusta!");
                        break;
                    }

                    System.out.print("Adres: ");
                    String adres = scanner.nextLine().trim();
                    if (adres.isEmpty()) {
                        System.out.println("Adres dostawcy nie może być pusty!");
                        break;
                    }

                    System.out.print("Kontakt: ");
                    String kontakt = scanner.nextLine().trim();
                    if (kontakt.isEmpty()) {
                        System.out.println("Kontakt dostawcy nie może być pusty!");
                        break;
                    }

                    int ilosc;
                    try {
                        System.out.print("Ilość początkowa: ");
                        String iloscInput = scanner.nextLine().trim();
                        if (iloscInput.isEmpty()) throw new NumberFormatException();
                        ilosc = Integer.parseInt(iloscInput);
                        if (ilosc < 0) {
                            System.out.println("Ilość nie może być ujemna!");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Nieprawidłowa ilość.");
                        break;
                    }

                    Dostawca d = new Dostawca(nazwaD, adres, kontakt);
                    Produkt p = new Produkt(nazwa, kod, cena, jednostka, opis, kategoria, d);
                    p.setMinimum(minimum);
                    p.setIlosc(ilosc);

                    boolean sukces = magazyn.dodajProdukt(p);
                    if (sukces) {
                        System.out.println("Produkt dodany.");
                        if (p.czyPonizejMinimum()) {
                            System.out.println("Uwaga: produkt ma ilość poniżej minimum!");
                            System.out.println("Ilość: " + p.getIlosc() + " / Minimum: " + p.getMinimum());
                        }
                    } else {
                        System.out.println("Produkt o takim kodzie już istnieje!");
                    }
                }

                case "2" -> {
                    List<Produkt> lista = magazyn.getProdukty();
                    if (lista.isEmpty()) {
                        System.out.println("Brak produktów.");
                    } else {
                        for (Produkt p : lista) {
                            System.out.println(p.formatujDoListy());
                            System.out.println("--------------------------");
                        }
                    }
                }

                case "3" -> {
                    System.out.print("Kod produktu do usunięcia: ");
                    String kod = scanner.nextLine();

                    Produkt produkt = magazyn.znajdzProdukt(kod);
                    if (produkt == null) {
                        System.out.println("Nie znaleziono produktu o podanym kodzie.");
                        break;
                    }

                    System.out.println("Znaleziono: " + produkt.getNazwa() + " (" + produkt.getKod() + ")");
                    System.out.print("Czy na pewno chcesz usunąć ten produkt? (tak/nie): ");
                    String decyzja = scanner.nextLine().toLowerCase();

                    if (decyzja.equals("tak")) {
                        boolean usunieto = magazyn.usunProdukt(kod);
                        System.out.println(usunieto ? "Produkt został usunięty." : "Nie udało się usunąć produktu.");
                    } else {
                        System.out.println("Operacja anulowana.");
                    }
                }

                case "4" -> {
                    System.out.print("Podaj nazwę pliku do zapisu (np. plikA.json): ");
                    String sciezka = scanner.nextLine();

                    boolean sukces = magazyn.zapiszDoPliku(sciezka);

                    if (sukces) {
                        System.out.println("Dane zapisane do pliku: " + sciezka);
                    } else {
                        System.out.println("Wystąpił błąd podczas zapisu.");
                    }
                }
                case "5" -> {
                    System.out.print("Podaj nazwę pliku do wczytania (np. pilkA.json): ");
                    String sciezka = scanner.nextLine();

                    boolean sukces = magazyn.wczytajZPliku(sciezka);

                    if (sukces) {
                        System.out.println("Dane wczytane z pliku: " + sciezka);
                    } else {
                        System.out.println("Nie udało się wczytać pliku lub plik nie istnieje.");
                    }
                }
                case "6" -> {
                    System.out.println("Po czym chcesz wyszukać? (nazwa / kod / opis / dostawca)");
                    String pole = scanner.nextLine().trim().toLowerCase();

                    if (!List.of("nazwa", "kod", "opis", "dostawca").contains(pole)) {
                        System.out.println("Nieprawidłowe pole wyszukiwania.");
                        break;
                    }

                    System.out.print("Wpisz szukaną frazę: ");
                    String fraza = scanner.nextLine();

                    List<Produkt> znalezione = magazyn.wyszukaj(fraza, pole);
                    if (znalezione.isEmpty()) {
                        System.out.println("Nie znaleziono pasujących produktów.");
                    } else {
                        System.out.println("Znalezione produkty:");
                        znalezione.forEach(System.out::println);
                    }
                }
                case "7" -> {
                    System.out.println("Sortuj po: 1 - nazwa, 2 - kod, 3 - cena, 4 - ilość");
                    String wybor = scanner.nextLine();

                    System.out.println("Kierunek: 1 - rosnąco, 2 - malejąco");
                    String kierunek = scanner.nextLine();

                    Comparator<Produkt> komparator = switch (wybor) {
                        case "1" -> Produkt.sortujPoNazwie();
                        case "2" -> Produkt.sortujPoKodzie();
                        case "3" -> Produkt.sortujPoCenie();
                        case "4" -> Produkt.sortujPoIlosci();
                        default -> null;
                    };

                    if (komparator != null) {
                        if ("2".equals(kierunek)) {
                            komparator = komparator.reversed();
                        }

                        magazyn.sortujProdukty(komparator);
                        System.out.println("Posortowano:");
                        for (Produkt p : magazyn.getProdukty()) {
                            System.out.println(p.formatujDoListy());
                            System.out.println("--------------------------");
                        }
                    } else {
                        System.out.println("Nieznane kryterium.");
                    }
                }

                case "8" -> {
                    System.out.print("Podaj kategorię do raportu: ");
                    String kategoria = scanner.nextLine();
                    String nazwaPliku = "raport_kategoria_" + kategoria.toLowerCase() + ".txt";

                    try {
                        magazyn.raportProduktowPoKategorii(kategoria, nazwaPliku);
                        System.out.println("Raport zapisano do pliku: " + nazwaPliku);
                    } catch (IOException e) {
                        System.out.println("Błąd podczas zapisu raportu: " + e.getMessage());
                    }
                }
                case "9" -> {
                    System.out.print("Podaj nazwę pliku raportu (np. raportA.txt): ");
                    String nazwaPliku = scanner.nextLine();
                    boolean sukces = magazyn.generujRaportDoPliku(nazwaPliku);
                    if (sukces) {
                        System.out.println("Raport zapisano do pliku: " + nazwaPliku);
                    } else {
                        System.out.println("Błąd podczas zapisu raportu.");
                    }
                }
                case "10" -> {
                    System.out.print("Podaj kod produktu: ");
                    String kod = scanner.nextLine();

                    System.out.print("Ilość przyjęcia: ");
                    int ilosc;
                    try {
                        ilosc = Integer.parseInt(scanner.nextLine());
                        if (ilosc <= 0) {
                            System.out.println("Ilość musi być większa niż 0.");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Niepoprawna liczba.");
                        break;
                    }

                    System.out.print("Podaj nazwę dostawcy: ");
                    String dostawca = scanner.nextLine();

                    boolean sukces = magazyn.przyjmijDostawe(kod, ilosc, dostawca);
                    System.out.println(sukces ? "Przyjęto dostawę." : "Nie znaleziono produktu.");
                }
                case "11" -> {
                    System.out.print("Podaj kod produktu: ");
                    String kod = scanner.nextLine();

                    System.out.print("Ilość do wydania: ");
                    int ilosc;
                    try {
                        ilosc = Integer.parseInt(scanner.nextLine());
                        if (ilosc <= 0) {
                            System.out.println("Ilość musi być większa niż 0.");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Niepoprawna liczba.");
                        break;
                    }

                    System.out.print("Podaj nazwę odbiorcy: ");
                    String odbiorca = scanner.nextLine();

                    int status = magazyn.wydajTowar(kod, ilosc, odbiorca);
                    switch (status) {
                        case 0 -> System.out.println("Nie znaleziono produktu.");
                        case 1 -> System.out.println("Brak wystarczającej ilości na stanie.");
                        case 2 -> System.out.println("Wydano towar.");
                        case 3 -> {
                            System.out.println("Wydano towar, ALE UWAGA: ilość poniżej progu minimalnego!");
                            Produkt p = magazyn.znajdzProdukt(kod);
                            if (p != null) {
                                System.out.println("Produkt: " + p.getNazwa() + " – ilość: " + p.getIlosc() + " / minimum: " + p.getMinimum());
                            }
                        }
                    }
                }
                case "12" -> {
                    System.out.println("Produkty wymagające uzupełnienia:");
                    List<Produkt> doUzupelnienia = magazyn.getProdukty().stream()
                            .filter(Produkt::czyPonizejMinimum)
                            .toList();

                    if (doUzupelnienia.isEmpty()) {
                        System.out.println("Wszystkie produkty mają wystarczającą ilość.");
                    } else {
                        for (Produkt p : doUzupelnienia) {
                            System.out.println(p.formatujDoListy());
                            System.out.println("--------------------------");
                        }
                    }
                }
                case "13" -> {
                    System.out.print("Podaj kod produktu do edycji: ");
                    String kod = scanner.nextLine();

                    Produkt istniejący = magazyn.znajdzProdukt(kod);
                    if (istniejący == null) {
                        System.out.println("Produkt o podanym kodzie nie istnieje.");
                        break;
                    }

                    System.out.println("=== EDYCJA PRODUKTU ===");

                    System.out.print("Nowa nazwa [" + istniejący.getNazwa() + "]: ");
                    String nazwa = scanner.nextLine();
                    if (nazwa.isBlank()) nazwa = istniejący.getNazwa();

                    System.out.print("Nowa cena [" + istniejący.getCena() + "]: ");
                    String cenaInput = scanner.nextLine();
                    double cena;
                    if (cenaInput.isBlank()) {
                        cena = istniejący.getCena();
                    } else {
                        try {
                            cena = Double.parseDouble(cenaInput);
                            if (cena < 0) {
                                System.out.println("Cena nie może być ujemna.");
                                break;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Nieprawidłowa cena.");
                            break;
                        }
                    }

                    System.out.print("Nowa jednostka [" + istniejący.getJednostka() + "]: ");
                    String jednostka = scanner.nextLine();
                    if (jednostka.isBlank()) jednostka = istniejący.getJednostka();

                    System.out.print("Nowy opis [" + istniejący.getOpis() + "]: ");
                    String opis = scanner.nextLine();
                    if (opis.isBlank()) opis = istniejący.getOpis();

                    System.out.print("Nowa kategoria [" + istniejący.getKategoria() + "]: ");
                    String kategoria = scanner.nextLine();
                    if (kategoria.isBlank()) kategoria = istniejący.getKategoria();

                    System.out.print("Nowe minimum [" + istniejący.getMinimum() + "]: ");
                    String minInput = scanner.nextLine();
                    int minimum;
                    if (minInput.isBlank()) {
                        minimum = istniejący.getMinimum();
                    } else {
                        try {
                            minimum = Integer.parseInt(minInput);
                            if (minimum < 0) {
                                System.out.println("Minimum nie może być ujemne.");
                                break;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Nieprawidłowa wartość minimum.");
                            break;
                        }
                    }

                    System.out.print("Nowa ilość [" + istniejący.getIlosc() + "]: ");
                    String iloscInput = scanner.nextLine();
                    int ilosc;
                    if (iloscInput.isBlank()) {
                        ilosc = istniejący.getIlosc();
                    } else {
                        try {
                            ilosc = Integer.parseInt(iloscInput);
                            if (ilosc < 0) {
                                System.out.println("Ilość nie może być ujemna.");
                                break;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Nieprawidłowa ilość.");
                            break;
                        }
                    }

                    System.out.println("== EDYCJA DOSTAWCY ==");

                    System.out.print("Nowa nazwa dostawcy [" + istniejący.getDostawca().getNazwa() + "]: ");
                    String nazwaD = scanner.nextLine();
                    if (nazwaD.isBlank()) nazwaD = istniejący.getDostawca().getNazwa();

                    System.out.print("Nowy adres [" + istniejący.getDostawca().getAdres() + "]: ");
                    String adres = scanner.nextLine();
                    if (adres.isBlank()) adres = istniejący.getDostawca().getAdres();

                    System.out.print("Nowy kontakt [" + istniejący.getDostawca().getKontakt() + "]: ");
                    String kontakt = scanner.nextLine();
                    if (kontakt.isBlank()) kontakt = istniejący.getDostawca().getKontakt();

                    Produkt nowy = new Produkt(nazwa, kod, cena, jednostka, opis, kategoria, new Dostawca(nazwaD, adres, kontakt));
                    nowy.setMinimum(minimum);
                    nowy.setIlosc(ilosc);

                    if (magazyn.edytujProdukt(kod, nowy)) {
                        System.out.println("Produkt zaktualizowany.");
                    } else {
                        System.out.println("Błąd przy edycji produktu.");
                    }
                }
                case "14" -> {
                    System.out.print("Podaj kod produktu: ");
                    String kod = scanner.nextLine();
                    List<Transakcja> lista = magazyn.historiaProduktu(kod);
                    if (lista.isEmpty()) {
                        System.out.println("Brak historii dla tego produktu.");
                    } else {
                        System.out.println("=== HISTORIA PRODUKTU: " + kod + " ===");
                        lista.forEach(System.out::println);
                    }
                }
                case "15" -> magazyn.logujStanAsynchronicznie();
                case "16" -> {
                    System.out.println("=== TWORZENIE NOWEGO PRODUKTU ===");

                    System.out.print("Nazwa: ");
                    String nazwa = scanner.nextLine();
                    if (nazwa.isBlank()) {
                        System.out.println("Nazwa nie może być pusta.");
                        break;
                    }

                    System.out.print("Kod: ");
                    String kod = scanner.nextLine();
                    if (kod.isBlank()) {
                        System.out.println("Kod nie może być pusty.");
                        break;
                    }

                    System.out.print("Cena: ");
                    double cena;
                    try {
                        cena = Double.parseDouble(scanner.nextLine());
                        if (cena < 0) {
                            System.out.println("Cena nie może być ujemna.");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Błędny format ceny.");
                        break;
                    }

                    System.out.print("Jednostka: ");
                    String jednostka = scanner.nextLine();

                    System.out.print("Opis: ");
                    String opis = scanner.nextLine();

                    System.out.print("Kategoria: ");
                    String kategoria = scanner.nextLine();

                    System.out.print("Minimalna ilość: ");
                    int minimum;
                    try {
                        minimum = Integer.parseInt(scanner.nextLine());
                        if (minimum < 0) {
                            System.out.println("Minimum nie może być ujemne.");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Błędny format liczby.");
                        break;
                    }

                    System.out.print("Ilość: ");
                    int ilosc;
                    try {
                        ilosc = Integer.parseInt(scanner.nextLine());
                        if (ilosc < 0) {
                            System.out.println("Ilość nie może być ujemna.");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Błędny format liczby.");
                        break;
                    }

                    System.out.println("== DOSTAWCA ==");
                    System.out.print("Nazwa: ");
                    String nazwad = scanner.nextLine();
                    System.out.print("Adres: ");
                    String adres = scanner.nextLine();
                    System.out.print("Kontakt: ");
                    String kontakt = scanner.nextLine();

                    Dostawca d = new Dostawca(nazwad, adres, kontakt);
                    Produkt produkt = new Produkt(nazwa, kod, cena, jednostka, opis, kategoria, d);
                    produkt.setMinimum(minimum);
                    produkt.setIlosc(ilosc);

                    if (db.dodajProdukt(produkt)) {
                        System.out.println("Produkt został dodany do bazy.");
                    } else {
                        System.out.println("Produkt o tym kodzie już istnieje.");
                    }
                }
                case "17" -> {
                    System.out.print("Podaj kod produktu: ");
                    String kod = scanner.nextLine();
                    Produkt znaleziony = db.znajdzProdukt(kod);
                    if (znaleziony != null) System.out.println(znaleziony);
                    else System.out.println("Nie znaleziono.");
                }
                case "18" -> {
                    System.out.print("Podaj kod produktu do usunięcia: ");
                    String kod = scanner.nextLine();
                    db.usunProdukt(kod);
                }
                case "19" -> {
                    List<Produkt> lista = db.pobierzWszystkieProdukty();
                    if (lista.isEmpty()) System.out.println("Brak produktów w bazie.");
                    else lista.forEach(System.out::println);
                }
                case "20" -> {
                    System.out.println("Filtruj po: 1 - kategoria, 2 - cena max, 3 - minimalna ilość");
                    String wybor = scanner.nextLine();

                    List<Produkt> wynik = new ArrayList<>();
                    switch (wybor) {
                        case "1" -> {
                            System.out.print("Podaj kategorię: ");
                            String kat = scanner.nextLine();
                            wynik = magazyn.filtrujPoKategorii(kat);
                        }
                        case "2" -> {
                            System.out.print("Podaj maksymalną cenę: ");
                            try {
                                double cena = Double.parseDouble(scanner.nextLine());
                                wynik = magazyn.filtrujPoCenie(cena);
                            } catch (NumberFormatException e) {
                                System.out.println("Niepoprawna liczba.");
                            }
                        }
                        case "3" -> {
                            System.out.print("Podaj minimalną ilość: ");
                            try {
                                int ilosc = Integer.parseInt(scanner.nextLine());
                                wynik = magazyn.filtrujPoIlosci(ilosc);
                            } catch (NumberFormatException e) {
                                System.out.println("Niepoprawna liczba.");
                            }
                        }
                        default -> System.out.println("Nieznany wybór.");
                    }

                    if (!wynik.isEmpty()) {
                        System.out.println(Produkt.formatujTabelarycznie(wynik));
                    } else {
                        System.out.println("Brak wyników.");
                    }
                }
                case "0" -> {
                    System.out.println("Do widzenia!");
                    trwa = false;
                }
                default -> System.out.println("Nieprawidłowa opcja!");
            }
        }
    }
}
