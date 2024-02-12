package it.unicam.cs.CityTourNet.model;

public class CartaDiCredito {
    private final String codiceCarta;

    private final int CVV;

    private double credito;

    public CartaDiCredito(String codiceCarta, int CVV) {
        this.codiceCarta = codiceCarta;
        this.CVV = CVV;
    }

    public boolean check(String codiceCarta, int CVV) {
        return this.codiceCarta.equals(codiceCarta) && this.CVV == CVV;
    }

    public void ricarica(double importo) {
        this.credito += importo;
    }

    public void effettuaPagamento(double importo) {
        this.credito -= importo;
    }

    public double getCredito() {
        return credito;
    }
}
