package pl.glembin.magazyn;

import org.junit.jupiter.api.Test;
import pl.glembin.magazyn.model.Dostawca;
import pl.glembin.magazyn.model.Produkt;

import static org.junit.jupiter.api.Assertions.*;

public class ProduktTest {

    @Test
    void testCzyPonizejMinimumTrue() {
        Produkt produkt = new Produkt("Mleko", "MLK01", 2.5, "l", "Świeże mleko", "Nabiał", new Dostawca());
        produkt.setIlosc(3);
        produkt.setMinimum(5);
        assertTrue(produkt.czyPonizejMinimum());
    }

    @Test
    void testCzyPonizejMinimumFalse() {
        Produkt produkt = new Produkt("Chleb", "CHB01", 3.0, "szt", "Chleb pszenny", "Pieczywo", new Dostawca());
        produkt.setIlosc(10);
        produkt.setMinimum(5);
        assertFalse(produkt.czyPonizejMinimum());
    }

    @Test
    void testPrzyjmij() {
        Produkt produkt = new Produkt("Woda", "WOD01", 1.5, "l", "Woda źródlana", "Napoje", new Dostawca());
        produkt.setIlosc(10);
        produkt.przyjmij(5);
        assertEquals(15, produkt.getIlosc());
    }

    @Test
    void testWydajTrue() {
        Produkt produkt = new Produkt("Sok", "SOK01", 4.0, "l", "Sok pomarańczowy", "Napoje", new Dostawca());
        produkt.setIlosc(10);
        boolean wynik = produkt.wydaj(4);
        assertTrue(wynik);
        assertEquals(6, produkt.getIlosc());
    }

    @Test
    void testWydajFalse() {
        Produkt produkt = new Produkt("Sok", "SOK02", 4.0, "l", "Sok jabłkowy", "Napoje", new Dostawca());
        produkt.setIlosc(2);
        boolean wynik = produkt.wydaj(5);
        assertFalse(wynik);
        assertEquals(2, produkt.getIlosc());
    }
}
