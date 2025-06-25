package pl.glembin.magazyn.model;

import java.util.Comparator;
import java.util.List;

/**
 * Klasa reprezentująca produkt w systemie magazynowym.
 * Przechowuje informacje o nazwie, kodzie, cenie, opisie, dostawcy, itp.
 */

public class Produkt {
    private String nazwa;
    private String kod;
    private double cena;
    private String jednostka;
    private String opis;
    private String kategoria;
    private int ilosc;
    private int minimum;
    private Dostawca dostawca;

    public Produkt() {}

    public Produkt(String nazwa, String kod, double cena, String jednostka, String opis, String kategoria, Dostawca dostawca) {
        this.nazwa = nazwa;
        this.kod = kod;
        this.cena = cena;
        this.jednostka = jednostka;
        this.opis = opis;
        this.kategoria = kategoria;
        this.dostawca = dostawca;
    }

    @Override
    public String toString() {
        return "Produkt: " + nazwa + " (" + kod + ") - " + cena + " zł/" + jednostka +
                "\nOpis: " + opis +
                "\nKategoria: " + kategoria +
                "\nIlość: " + ilosc +
                "\nMinimum: " + minimum +
                "\nDostawca: " + dostawca;
    }

    public String getNazwa() { return nazwa; }
    public void setNazwa(String nazwa) { this.nazwa = nazwa; }
    public String getKod() { return kod; }
    public void setKod(String kod) { this.kod = kod; }
    public double getCena() { return cena; }
    public void setCena(double cena) { this.cena = cena; }
    public String getJednostka() { return jednostka; }
    public void setJednostka(String jednostka) { this.jednostka = jednostka; }
    public String getOpis() { return opis; }
    public void setOpis(String opis) { this.opis = opis; }
    public String getKategoria() { return kategoria; }
    public void setKategoria(String kategoria) { this.kategoria = kategoria; }
    public int getIlosc() { return ilosc; }
    public void setIlosc(int ilosc) { this.ilosc = ilosc; }
    public int getMinimum() { return minimum; }
    public void setMinimum(int minimum) { this.minimum = minimum; }
    public Dostawca getDostawca() { return dostawca; }
    public void setDostawca(Dostawca dostawca) { this.dostawca = dostawca; }

    // Sortery
    public static Comparator<Produkt> sortujPoNazwie() {
        return Comparator.comparing(Produkt::getNazwa);
    }

    public static Comparator<Produkt> sortujPoKodzie() {
        return Comparator.comparing(Produkt::getKod);
    }

    public static Comparator<Produkt> sortujPoCenie() {
        return Comparator.comparingDouble(Produkt::getCena);
    }

    public static Comparator<Produkt> sortujPoIlosci() {
        return Comparator.comparingInt(Produkt::getIlosc);
    }

    public String formatujDoListy() {
        return String.format("Produkt: %s (%s) - %.2f zł/%s\nOpis: %s\nKategoria: %s\nIlość: %d\nMinimum: %d\nDostawca: %s\n",
                nazwa, kod, cena, jednostka, opis, kategoria, ilosc, minimum,
                dostawca != null ? dostawca.getNazwa() : "brak");
    }


    public static String formatujTabelarycznie(List<Produkt> lista) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s %-20s %-8s %-8s %-12s %-15s%n", "Kod", "Nazwa", "Cena", "Ilość", "Kategoria", "Dostawca"));
        sb.append("--------------------------------------------------------------------------------\n");

        for (Produkt p : lista) {
            sb.append(String.format("%-10s %-20s %-8.2f %-8d %-12s %-15s%n",
                    p.getKod(), p.getNazwa(), p.getCena(), p.getIlosc(), p.getKategoria(),
                    (p.getDostawca() != null ? p.getDostawca().getNazwa() : "brak")));
        }
        return sb.toString();
    }



    // Czy produkt poniżej minimum?
    public boolean czyPonizejMinimum() {
        return ilosc < minimum;
    }

    // Modyfikuj stan po transakcji
    public void przyjmij(int ilosc) {
        this.ilosc += ilosc;
    }

    public boolean wydaj(int ilosc) {
        if (this.ilosc >= ilosc) {
            this.ilosc -= ilosc;
            return true;
        }
        return false;
    }
}
