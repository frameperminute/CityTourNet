package it.unicam.cs.CityTourNet.model.utente;

public class TuristaAutenticato extends Utente{
    private int punti;
    public TuristaAutenticato(String username, String email, String password) {
        super(username, email, password);
    }
}
