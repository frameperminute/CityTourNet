package it.unicam.cs.CityTourNet.model;

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
                    this.account.getUsername(), password),this.cartaDiCredito);
        }
        return null;
    }
}
