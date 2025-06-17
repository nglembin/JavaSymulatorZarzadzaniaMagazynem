# 🏭 System Zarządzania Magazynem

Projekt konsolowej aplikacji do zarządzania magazynem napisany w języku Java. Umożliwia zarządzanie produktami, dostawami, wydaniami, historią transakcji oraz integrację z bazą danych i zapis do plików.

## 📦 Funkcje

- ✅ Dodawanie, edytowanie i usuwanie produktów
- 📊 Śledzenie stanów magazynowych i minimalnych progów
- 🔎 Wyszukiwanie i filtrowanie produktów
- 📂 Zapis i odczyt danych z pliku `.json`
- 📈 Generowanie raportów magazynowych i transakcyjnych
- 📤 Eksport raportu do pliku `.txt`
- 🧵 Wielowątkowe generowanie raportów (nie blokuje programu)
- ⚠️ Powiadomienia o produktach poniżej minimum
- 👤 Zarządzanie dostawcami produktów
- 🧪 Testy jednostkowe z JUnit
- 💾 Połączenie z bazą danych SQLite (CRUD)
- 🪵 Logowanie zdarzeń (SLF4J + Logback)
- ⚙️ Konfiguracja aplikacji z pliku `config.properties`

## 🗂️ Struktura projektu
src/

└── pl.glembin.magazyn

├── model/ ← Klasy danych: Produkt, Transakcja, Dostawca

├── service/ ← Logika biznesowa (klasa Magazyn)

├── db/ ← Obsługa bazy danych (DatabaseManager)

├── utils/ ← Konfiguracja i pomocnicze klasy

└── Main.java ← Główna klasa uruchamiająca program

## ⚙️ Wymagania

- Java 17+
- Maven
- SQLite JDBC Driver
- Biblioteki: Jackson, SLF4J, Logback, JUnit

## 🛠️ Uruchomienie

1. Sklonuj repozytorium:
2. Uruchom aplikację z klasy Main

# 🧪 Testowanie
Testy znajdują się w katalogu pl.glembin.magazyn z adnotacją @Test (JUnit 5).
Można uruchomić z poziomu IntelliJ lub komendą Maven

# 📚 Autor
Nikodem Glembin – system stworzony w ramach projektu zaliczeniowego.
