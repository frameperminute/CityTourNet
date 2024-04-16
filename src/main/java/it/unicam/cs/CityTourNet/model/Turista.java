package it.unicam.cs.CityTourNet.model;

import java.util.List;

public class Turista {
    private final Account account;

    private CartaDiCredito cartaDiCredito;



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

    public Account getAccount() {
        return this.account;
    }

    public TuristaAutenticato richiediAutenticazione(String password) {
        if(this.cartaDiCredito.getCredito() >= 50 && this.account.isPasswordCorrect(password)) {
            this.cartaDiCredito.effettuaPagamento(50);
            return new TuristaAutenticato(new Account(AccountType.TURISTA_AUTENTICATO,
                    this.account.getUsername(), password),this.cartaDiCredito);
        }
        return null;
    }

}
