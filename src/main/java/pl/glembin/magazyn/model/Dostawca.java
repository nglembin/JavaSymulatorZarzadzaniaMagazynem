package pl.glembin.magazyn.model;

/**
 * Klasa reprezentująca dostawcę produktu.
 * Zawiera dane kontaktowe oraz adresowe dostawcy.
 */

public class Dostawca {
    private String nazwa;
    private String adres;
    private String kontakt;

    public Dostawca() {}

    public Dostawca(String nazwa, String adres, String kontakt) {
        this.nazwa = nazwa;
        this.adres = adres;
        this.kontakt = kontakt;
    }

    public String getNazwa() { return nazwa; }
    public void setNazwa(String nazwa) { this.nazwa = nazwa; }
    public String getAdres() { return adres; }
    public void setAdres(String adres) { this.adres = adres; }
    public String getKontakt() { return kontakt; }
    public void setKontakt(String kontakt) { this.kontakt = kontakt; }

    @Override
    public String toString() {
        return nazwa + ", " + adres + ", " + kontakt;
    }
}