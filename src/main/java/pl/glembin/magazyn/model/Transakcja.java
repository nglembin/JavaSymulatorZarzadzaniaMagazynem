package pl.glembin.magazyn.model;

import java.time.LocalDate;

/**
 * Klasa reprezentująca transakcję magazynową (przyjęcie lub wydanie).
 * Zawiera informacje o dacie, ilości, produkcie i typie transakcji.
 */

public class Transakcja {
    private LocalDate data;
    private String kodProduktu;
    private int ilosc;
    private TypTransakcji typ;

    public Transakcja() {}

    public Transakcja(LocalDate data, String kodProduktu, int ilosc, TypTransakcji typ) {
        this.data = data;
        this.kodProduktu = kodProduktu;
        this.ilosc = ilosc;
        this.typ = typ;
    }

    public LocalDate getData() { return data; }
    public String getKodProduktu() { return kodProduktu; }
    public int getIlosc() { return ilosc; }
    public TypTransakcji getTyp() { return typ; }

    @Override
    public String toString() {
        return data + " | " + typ + " | " + kodProduktu + " | " + ilosc;
    }
}