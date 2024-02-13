package it.unicam.cs.CityTourNet.model;

import java.util.List;

public class Turista {
    private final Account account;

    private CartaDiCredito cartaDiCredito;

    private List<Messaggio> messaggi;

    public Turista(String username, String password) {
        this.account = new Account(AccountType.TURISTA, username, password);
    }

    public Turista(String username, String password, CartaDiCredito cartaDiCredito) {
        this.account = new Account(AccountType.TURISTA, username, password);
        this.cartaDiCredito = cartaDiCredito;
    }

    public void changeCartaDiCredito(CartaDiCredito cartaDiCredito) {
        this.cartaDiCredito = cartaDiCredito;
    }

    public Account getAccount(Account adminAccount) {
        if(adminAccount.getAccountType() == AccountType.GESTORE_DELLA_PIATTAFORMA) {
            return this.account;
        }
        return null;
    }

    public TuristaAutenticato richiediAutenticazione(String password) {
        if(this.cartaDiCredito.getCredito() >= 50 && this.account.isPasswordCorrect(password)) {
            this.cartaDiCredito.effettuaPagamento(50);
            return new TuristaAutenticato(new Account(AccountType.TURISTA_AUTENTICATO,
                    this.account.getUsername(password), password),this.cartaDiCredito);
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
