package pl.glembin.magazyn.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

public class Wydanie extends Transakcja {

    private static final Logger logger = LoggerFactory.getLogger(Wydanie.class);

    private String odbiorca;

    public Wydanie(LocalDate data, String kodProduktu, int ilosc, String odbiorca) {
        super(data, kodProduktu, ilosc, TypTransakcji.WYDANIE);
        this.odbiorca = odbiorca;
        zapiszDokumentWZ();
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
            logger.error("Nie udało się zapisać dokumentu WZ: {}", e.getMessage());
        }
    }
}
