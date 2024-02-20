package it.unicam.cs.CityTourNet.model;

public class ContributorAutorizzato {
    private final Account account;

    private boolean autorizzato;

    public ContributorAutorizzato(Account account) {
        this.account = account;
        this.autorizzato = true;
    }

    public Account getAccount() {
        return this.account;
    }
}
