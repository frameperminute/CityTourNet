package it.unicam.cs.CityTourNet.model;

public class Contributor {
    private final Account account;

    private int numeroContenutiApprovati;

    public Contributor(String username, String password) {
        this.account = new Account(AccountType.CONTRIBUTOR,
                username, password);
        this.numeroContenutiApprovati = 0;
    }

    public void registraContenutoApprovato(Curatore curatore) {
        this.numeroContenutiApprovati++;
    }

    public ContributorAutorizzato richiediAutorizzazione(String password) {
        if(this.numeroContenutiApprovati >= 50 && this.account.isPasswordCorrect(password)) {
            return new ContributorAutorizzato(new Account(AccountType.CONTRIBUTOR_AUTORIZZATO,
                    this.account.getUsername(password), password));
        }
        return null;
    }

    public Account getAccount() {
        return this.account;
    }

}
