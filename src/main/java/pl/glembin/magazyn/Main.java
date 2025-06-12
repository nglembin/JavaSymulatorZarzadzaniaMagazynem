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
                case "1" -> magazyn.dodajProdukt(scanner);
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
