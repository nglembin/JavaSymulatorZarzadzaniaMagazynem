package pl.glembin.magazyn;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.glembin.magazyn.model.Dostawca;
import pl.glembin.magazyn.model.Produkt;
import pl.glembin.magazyn.service.Magazyn;

import static org.junit.jupiter.api.Assertions.*;

public class MagazynTest {

    private Magazyn magazyn;

    @BeforeEach
    void setUp() {
        magazyn = new Magazyn();
    }

    @Test
    void testDodajProduktManually() {
        Produkt p = new Produkt(
                "Testowy", "T01", 10.0, "szt", "Opis test", "Testowa",
                new Dostawca("Dostawca", "Miasto", "123")
        );
        p.setIlosc(5);
        p.setMinimum(2);
        magazyn.getProdukty2().add(p);  // Zakładamy, że getProdukty2() zwraca testową listę

        assertEquals(1, magazyn.getProdukty2().size());
        assertEquals("T01", magazyn.getProdukty2().get(0).getKod());
    }

    @Test
    void testUsunProdukt() {
        Produkt p = new Produkt(
                "DoUsuniecia", "DEL01", 5.0, "szt", "Opis", "Kategoria",
                new Dostawca("Firma", "Miasto", "tel")
        );
        p.setIlosc(2);
        p.setMinimum(1);
        magazyn.getProdukty2().add(p);

        magazyn.usunProdukt("DEL01");  // ✅ teraz przekazujemy tylko kod

        assertTrue(magazyn.getProdukty2().isEmpty());
    }
}
