package it.unicam.cs.CityTourNet.model;

public class ContributorAutorizzato {
    private final Account account;

    private boolean autorizzato;

    public ContributorAutorizzato(Account account) {
        this.account = account;
        this.autorizzato = true;
    }

    public Account getAccount(Account adminAccount) {
        if(adminAccount.getAccountType() == AccountType.GESTORE_DELLA_PIATTAFORMA) {
            return this.account;
        }
        return null;
    }
}
