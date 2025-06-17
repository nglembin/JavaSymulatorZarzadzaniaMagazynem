package pl.glembin.magazyn.model;

import java.time.LocalDate;

/**
 * Klasa reprezentująca transakcję przyjęcia produktu do magazynu.
 */
public class Przyjecie extends Transakcja {
    public Przyjecie(LocalDate data, String kodProduktu, int ilosc) {
        super(data, kodProduktu, ilosc, TypTransakcji.PRZYJECIE);
    }

    @Override
    public String getTyp() {
        return "PRZYJĘCIE";
    }
}
