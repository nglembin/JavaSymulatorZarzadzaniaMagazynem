package pl.glembin.magazyn.db;

import pl.glembin.magazyn.model.Dostawca;
import pl.glembin.magazyn.model.Produkt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa zarządzająca połączeniem z bazą danych SQLite oraz operacjami CRUD dla produktów.
 */
public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:magazyn.db";

    public DatabaseManager() {
        utworzTabeleJesliNieIstnieje();
    }

    private void utworzTabeleJesliNieIstnieje() {
        String sql = """
            CREATE TABLE IF NOT EXISTS produkty (
                kod TEXT PRIMARY KEY,
                nazwa TEXT,
                cena REAL,
                jednostka TEXT,
                opis TEXT,
                kategoria TEXT,
                ilosc INTEGER,
                minimum INTEGER,
                dostawca_nazwa TEXT,
                dostawca_adres TEXT,
                dostawca_kontakt TEXT
            );
        """;

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("❌ Błąd tworzenia tabeli: " + e.getMessage());
        }
    }

    public boolean dodajProdukt(Produkt p) {
        String sql = """
        INSERT INTO produkty (kod, nazwa, cena, jednostka, opis, kategoria, ilosc, minimum, 
                              dostawca_nazwa, dostawca_adres, dostawca_kontakt)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
    """;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getKod());
            ps.setString(2, p.getNazwa());
            ps.setDouble(3, p.getCena());
            ps.setString(4, p.getJednostka());
            ps.setString(5, p.getOpis());
            ps.setString(6, p.getKategoria());
            ps.setInt(7, p.getIlosc());
            ps.setInt(8, p.getMinimum());

            Dostawca d = p.getDostawca();
            ps.setString(9, d.getNazwa());
            ps.setString(10, d.getAdres());
            ps.setString(11, d.getKontakt());

            ps.executeUpdate();
            System.out.println("Zapisano do bazy danych.");
            return true;

        } catch (SQLException e) {
            System.err.println("Błąd zapisu do bazy: " + e.getMessage());
            return false;
        }
    }

    public Produkt znajdzProdukt(String kod) {
        String sql = "SELECT * FROM produkty WHERE kod = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kod);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Dostawca d = new Dostawca(
                        rs.getString("dostawca_nazwa"),
                        rs.getString("dostawca_adres"),
                        rs.getString("dostawca_kontakt")
                );
                Produkt p = new Produkt(
                        rs.getString("nazwa"),
                        rs.getString("kod"),
                        rs.getDouble("cena"),
                        rs.getString("jednostka"),
                        rs.getString("opis"),
                        rs.getString("kategoria"),
                        d
                );
                p.setIlosc(rs.getInt("ilosc"));
                p.setMinimum(rs.getInt("minimum"));
                return p;
            }
        } catch (SQLException e) {
            System.err.println("Błąd podczas wyszukiwania: " + e.getMessage());
        }
        return null;
    }

    public List<Produkt> pobierzWszystkieProdukty() {
        List<Produkt> lista = new ArrayList<>();
        String sql = "SELECT * FROM produkty";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Dostawca d = new Dostawca(
                        rs.getString("dostawca_nazwa"),
                        rs.getString("dostawca_adres"),
                        rs.getString("dostawca_kontakt")
                );
                Produkt p = new Produkt(
                        rs.getString("nazwa"),
                        rs.getString("kod"),
                        rs.getDouble("cena"),
                        rs.getString("jednostka"),
                        rs.getString("opis"),
                        rs.getString("kategoria"),
                        d
                );
                p.setIlosc(rs.getInt("ilosc"));
                p.setMinimum(rs.getInt("minimum"));
                lista.add(p);
            }

        } catch (SQLException e) {
            System.err.println("Błąd pobierania produktów: " + e.getMessage());
        }

        return lista;
    }

    public void usunProdukt(String kod) {
        String sql = "DELETE FROM produkty WHERE kod = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kod);
            int usunieto = ps.executeUpdate();
            if (usunieto > 0) {
                System.out.println("Produkt usunięty z bazy.");
            } else {
                System.out.println("Nie znaleziono produktu do usunięcia.");
            }
        } catch (SQLException e) {
            System.err.println("Błąd usuwania produktu: " + e.getMessage());
        }
    }

    public void zaktualizujProdukt(Produkt p) {
        String sql = """
            UPDATE produkty SET
                nazwa = ?, cena = ?, jednostka = ?, opis = ?, kategoria = ?,
                ilosc = ?, minimum = ?, dostawca_nazwa = ?, dostawca_adres = ?, dostawca_kontakt = ?
            WHERE kod = ?;
        """;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNazwa());
            ps.setDouble(2, p.getCena());
            ps.setString(3, p.getJednostka());
            ps.setString(4, p.getOpis());
            ps.setString(5, p.getKategoria());
            ps.setInt(6, p.getIlosc());
            ps.setInt(7, p.getMinimum());

            Dostawca d = p.getDostawca();
            ps.setString(8, d.getNazwa());
            ps.setString(9, d.getAdres());
            ps.setString(10, d.getKontakt());

            ps.setString(11, p.getKod());

            int zmienione = ps.executeUpdate();
            System.out.println(zmienione > 0 ? "Zaktualizowano w bazie." : "Produkt nie istnieje.");
        } catch (SQLException e) {
            System.err.println("Błąd aktualizacji: " + e.getMessage());
        }
    }
}
