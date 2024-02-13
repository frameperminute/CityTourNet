package it.unicam.cs.CityTourNet.model;

import java.util.List;

public class Contributor {
    private final Account account;

    private int numeroContenutiApprovati;

    private List<Messaggio> messaggi;

    public Contributor(String username, String password) {
        this.account = new Account(AccountType.CONTRIBUTOR,
                username, password);
        this.numeroContenutiApprovati = 0;
    }

    public void registraContenutoApprovato(Account adminAccount) {
        if(adminAccount.getAccountType() == AccountType.CURATORE) {
            this.numeroContenutiApprovati++;
        }
    }

    public ContributorAutorizzato richiediAutorizzazione(String password) {
        if(this.numeroContenutiApprovati >= 50 && this.account.isPasswordCorrect(password)) {
            return new ContributorAutorizzato(new Account(AccountType.CONTRIBUTOR_AUTORIZZATO,
                    this.account.getUsername(password), password));
        }
        return null;
    }

    public Account getAccount(Account adminAccount) {
        if(adminAccount.getAccountType() == AccountType.GESTORE_DELLA_PIATTAFORMA) {
            return this.account;
        }
        return null;
    }

    public void riceviMessaggio(Messaggio messaggio) {
        this.messaggi.add(messaggio);
    }

    public void eliminaUltimoMessaggio(String password) {
        if(this.account.isPasswordCorrect(password)) {
            this.messaggi.remove(this.messaggi.size()-1);
        }
    }

    public void eliminaTuttiIMessaggi(String password) {
        if(this.account.isPasswordCorrect(password)) {
            this.messaggi.clear();
        }
    }
}
