package it.unicam.cs.CityTourNet.model;

public class Messaggio {
    String testoMessaggio;

    boolean letto;

    public Messaggio(String testoMessaggio) {
        this.testoMessaggio = testoMessaggio;
        this.letto = false;
    }

    public String leggi() {
        this.letto = true;
        return this.testoMessaggio;
    }
}
