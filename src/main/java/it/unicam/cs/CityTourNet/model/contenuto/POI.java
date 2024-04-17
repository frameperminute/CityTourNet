package it.unicam.cs.CityTourNet.model.contenuto;

import java.io.File;

public class POI extends Contenuto{
    private File multimedia;
    public POI(String nome, String descrizione, String usernameAutore, File multimedia) {
        super(nome, descrizione, usernameAutore);
        this.multimedia = multimedia;
    }
    public File getMultimedia() {
        return multimedia;
    }

    public void setMultimedia(File multimedia) {
        this.multimedia = multimedia;
    }
}
