package pl.glembin.magazyn.model;

import java.time.LocalDate;

/**
 * Klasa reprezentująca transakcję wydania produktu z magazynu.
 */
public class Wydanie extends Transakcja {
    public Wydanie(LocalDate data, String kodProduktu, int ilosc) {
        super(data, kodProduktu, ilosc, TypTransakcji.WYDANIE);
    }

    @Override
    public String getTyp() {
        return "WYDANIE";
    }
}
