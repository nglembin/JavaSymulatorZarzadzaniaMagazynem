package pl.glembin.magazyn.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

/**
 * Klasa reprezentująca transakcję przyjęcia produktu do magazynu.
 * Zawiera informacje o dacie, kodzie produktu, ilości oraz nazwie dostawcy.
 * Automatycznie generuje dokument PZ w formie pliku tekstowego.
 */

public class Przyjecie extends Transakcja {

    private static final Logger logger = LoggerFactory.getLogger(Przyjecie.class);
    private String dostawca;

    public Przyjecie(LocalDate data, String kodProduktu, int ilosc, String dostawca) {
        super(data, kodProduktu, ilosc, TypTransakcji.PRZYJECIE);
        this.dostawca = dostawca;
        zapiszDokumentPZ(); // generowanie dokumentu przy utworzeniu
    }

    @Override
    public String getTyp() {
        return "PRZYJĘCIE";
    }

    private void zapiszDokumentPZ() {
        String nazwaPliku = "PZ_" + kodProduktu + "_" + data + ".txt";
        try (FileWriter writer = new FileWriter(nazwaPliku)) {
            writer.write("=== Dokument PZ ===\n");
            writer.write("Data: " + data + "\n");
            writer.write("Kod produktu: " + kodProduktu + "\n");
            writer.write("Ilość: " + ilosc + "\n");
            writer.write("Dostawca: " + dostawca + "\n");
        } catch (IOException e) {
            logger.error("Nie udało się zapisać dokumentu PZ: {}", e.getMessage());
        }
    }
}
