package pl.glembin.magazyn;

import pl.glembin.magazyn.db.DatabaseManager;
import pl.glembin.magazyn.model.Produkt;
import pl.glembin.magazyn.service.Magazyn;

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
            System.out.println("8. Pokaż produkty poniżej minimum");
            System.out.println("9. Wygeneruj raport do pliku");
            System.out.println("10. Przyjmij dostawę");
            System.out.println("11. Wydaj towar");
            System.out.println("12. Wyświetl powiadomienia o brakach");
            System.out.println("13. Edytuj produkt");
            System.out.println("14. Historia produktu");
            System.out.println("15. Generuj raport asynchronicznie (w tle)");
            System.out.println("16. Zapisz produkt do bazy danych");
            System.out.println("17. Wczytaj produkt z bazy danych");
            System.out.println("18. Usuń produkt z bazy danych");
            System.out.println("19. Wyświetl wszystkie produkty z bazy");
            System.out.println("0. Wyjście");
            System.out.print("Wybierz opcję: ");

            switch (scanner.nextLine()) {
                case "1" -> {
                    System.out.print("Nazwa: ");
                    String nazwa = scanner.nextLine().trim();
                    if (nazwa.isEmpty()) {
                        System.out.println("❌ Nazwa nie może być pusta!");
                        break;
                    }

                    System.out.print("Kod: ");
                    String kod = scanner.nextLine().trim();
                    if (kod.isEmpty()) {
                        System.out.println("❌ Kod nie może być pusty!");
                        break;
                    }

                    double cena;
                    try {
                        System.out.print("Cena: ");
                        String cenaInput = scanner.nextLine().trim();
                        if (cenaInput.isEmpty()) throw new NumberFormatException();
                        cena = Double.parseDouble(cenaInput);
                        if (cena <= 0) {
                            System.out.println("❌ Cena musi być większa niż 0!");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Nieprawidłowa cena.");
                        break;
                    }

                    System.out.print("Jednostka: ");
                    String jednostka = scanner.nextLine().trim();
                    if (jednostka.isEmpty()) {
                        System.out.println("❌ Jednostka nie może być pusta!");
                        break;
                    }

                    System.out.print("Opis: ");
                    String opis = scanner.nextLine().trim();

                    System.out.print("Kategoria: ");
                    String kategoria = scanner.nextLine().trim();
                    if (kategoria.isEmpty()) {
                        System.out.println("❌ Kategoria nie może być pusta!");
                        break;
                    }

                    int minimum;
                    try {
                        System.out.print("Minimalna ilość (powiadomienie): ");
                        String minInput = scanner.nextLine().trim();
                        if (minInput.isEmpty()) throw new NumberFormatException();
                        minimum = Integer.parseInt(minInput);
                        if (minimum < 0) {
                            System.out.println("❌ Minimalna ilość nie może być ujemna!");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Nieprawidłowa wartość minimalna.");
                        break;
                    }

                    System.out.println("== DOSTAWCA ==");

                    System.out.print("Nazwa dostawcy: ");
                    String nazwaD = scanner.nextLine().trim();
                    if (nazwaD.isEmpty()) {
                        System.out.println("❌ Nazwa dostawcy nie może być pusta!");
                        break;
                    }

                    System.out.print("Adres: ");
                    String adres = scanner.nextLine().trim();
                    if (adres.isEmpty()) {
                        System.out.println("❌ Adres dostawcy nie może być pusty!");
                        break;
                    }

                    System.out.print("Kontakt: ");
                    String kontakt = scanner.nextLine().trim();
                    if (kontakt.isEmpty()) {
                        System.out.println("❌ Kontakt dostawcy nie może być pusty!");
                        break;
                    }

                    int ilosc;
                    try {
                        System.out.print("Ilość początkowa: ");
                        String iloscInput = scanner.nextLine().trim();
                        if (iloscInput.isEmpty()) throw new NumberFormatException();
                        ilosc = Integer.parseInt(iloscInput);
                        if (ilosc < 0) {
                            System.out.println("❌ Ilość nie może być ujemna!");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Nieprawidłowa ilość.");
                        break;
                    }

                    boolean sukces = magazyn.dodajProdukt(nazwa, kod, cena, jednostka, opis, kategoria, minimum, ilosc, nazwaD, adres, kontakt);
                    if (sukces) {
                        System.out.println("✅ Produkt dodany.");
                    } else {
                        System.out.println("❌ Produkt o takim kodzie już istnieje!");
                    }
                }

                case "2" -> magazyn.wyswietlProdukty();
                case "3" -> magazyn.usunProdukt(scanner);
                case "4" -> magazyn.zapiszDoPliku();
                case "5" -> magazyn.wczytajZPliku();
                case "6" -> magazyn.wyszukaj(scanner);
                case "7" -> magazyn.sortuj(scanner);
                case "8" -> magazyn.pokazNiskieStany();
                case "9" -> magazyn.generujRaport();
                case "10" -> magazyn.przyjmijDostawę(scanner);
                case "11" -> magazyn.wydajTowar(scanner);
                case "12" -> magazyn.wyswietlPowiadomienia();
                case "13" -> magazyn.edytujProdukt(scanner);
                case "14" -> magazyn.historiaProduktu(scanner);
                case "15" -> magazyn.generujRaportAsynchronicznie();
                case "16" -> {
                    Produkt produkt = magazyn.utworzProduktZKonsoli(scanner);
                    db.dodajProdukt(produkt);
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
                case "0" -> {
                    System.out.println("Do widzenia!");
                    trwa = false;
                }
                default -> System.out.println("Nieprawidłowa opcja!");
            }
        }
    }
}
