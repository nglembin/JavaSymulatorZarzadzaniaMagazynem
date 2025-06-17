package pl.glembin.magazyn.model;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

/**
 * Klasa reprezentująca transakcję wydania produktu z magazynu.
 * Zawiera informacje o dacie, kodzie produktu, ilości oraz nazwie odbiorcy.
 * Automatycznie generuje dokument WZ w formie pliku tekstowego.
 */
public class Wydanie extends Transakcja {

    private String odbiorca;

    public Wydanie(LocalDate data, String kodProduktu, int ilosc, String odbiorca) {
        super(data, kodProduktu, ilosc, TypTransakcji.WYDANIE);
        this.odbiorca = odbiorca;
        zapiszDokumentWZ(); // automatyczne generowanie WZ
    }

    @Override
    public String getTyp() {
        return "WYDANIE";
    }

    private void zapiszDokumentWZ() {
        String nazwaPliku = "WZ_" + kodProduktu + "_" + data + ".txt";
        try (FileWriter writer = new FileWriter(nazwaPliku)) {
            writer.write("=== Dokument WZ ===\n");
            writer.write("Data: " + data + "\n");
            writer.write("Kod produktu: " + kodProduktu + "\n");
            writer.write("Ilość: " + ilosc + "\n");
            writer.write("Odbiorca: " + odbiorca + "\n");
        } catch (IOException e) {
            System.err.println("Nie udało się zapisać dokumentu WZ: " + e.getMessage());
        }
    }
}
