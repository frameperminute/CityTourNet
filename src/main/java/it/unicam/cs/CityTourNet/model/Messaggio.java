package it.unicam.cs.CityTourNet.model;

public class Messaggio {
    private String testoMessaggio;

    private boolean letto;

    public Messaggio(String testoMessaggio) {
        this.testoMessaggio = testoMessaggio;
        this.letto = false;
    }

    public String leggi() {
        this.letto = true;
        return this.testoMessaggio;
    }
}
