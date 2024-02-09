package it.unicam.cs.CityTourNet.model;

public class Contributor {
    private final Account account;

    private int numeroContenutiApprovati;

    public Contributor(Account account) {
        this.account = account;
        this.numeroContenutiApprovati = 0;
    }

    //TODO inserire Account Curatore come parametro
    public void registraContenutoApprovato() {
        this.numeroContenutiApprovati++;
    }

    public void changeUsername(String username) {
        account.changeUsername(username);
    }
    public void changePassword(String oldPassword, String newPassword) {
        account.changePassword(oldPassword, newPassword);
    }

    public ContributorAutorizzato richiediAutorizzazione() {
        if(this.numeroContenutiApprovati >= 50) {
            return new ContributorAutorizzato(this);
        }
        return null;
    }
}
