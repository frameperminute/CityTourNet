package it.unicam.cs.CityTourNet.model.utente;

public class ContributorAutorizzato extends Utente{
    private int esitiNegativi;

    public ContributorAutorizzato(String username, String email, String password) {
        super(username, email, password);
    }
}
