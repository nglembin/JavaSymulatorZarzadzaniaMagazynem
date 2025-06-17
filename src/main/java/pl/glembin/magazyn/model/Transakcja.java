package pl.glembin.magazyn.model;

import java.time.LocalDate;

/**
 * Abstrakcyjna klasa reprezentująca transakcję magazynową.
 */

public abstract class Transakcja {
    protected LocalDate data;
    protected String kodProduktu;
    protected int ilosc;
    protected TypTransakcji typ;

    public Transakcja(LocalDate data, String kodProduktu, int ilosc, TypTransakcji typ) {
        this.data = data;
        this.kodProduktu = kodProduktu;
        this.ilosc = ilosc;
        this.typ = typ;
    }

    public abstract String getTyp();

    public LocalDate getData() {
        return data;
    }

    public String getKodProduktu() {
        return kodProduktu;
    }

    public int getIlosc() {
        return ilosc;
    }

    public TypTransakcji getTypTransakcji() {
        return typ;
    }

    @Override
    public String toString() {
        return getTyp() + ": " + data + ", " + kodProduktu + ", " + ilosc + " szt.";
    }
}
