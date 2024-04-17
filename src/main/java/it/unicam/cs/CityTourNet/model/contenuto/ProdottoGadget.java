package it.unicam.cs.CityTourNet.model.contenuto;

import java.io.File;

public class ProdottoGadget extends Contenuto{
    private double prezzo;
    private int numPezzi;
    private File multimedia;
    public ProdottoGadget(String nome, String descrizione, String usernameAutore,double prezzo, int numPezzi,
                          File multimedia) {
        super(nome, descrizione, usernameAutore);
        this.prezzo = prezzo;
        this.numPezzi = numPezzi;
        this.multimedia = multimedia;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public int getNumPezzi() {
        return numPezzi;
    }

    public File getMultimedia() {
        return multimedia;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public void setMultimedia(File multimedia) {
        this.multimedia = multimedia;
    }

    public void setNumPezzi(int numPezzi) {
        this.numPezzi = numPezzi;
    }

}
