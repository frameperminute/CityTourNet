package it.unicam.cs.CityTourNet.model;

public class Turista {
    private final Account account;

    private CartaDiCredito cartaDiCredito;

    public Turista(Account account) {
        this.account = account;
    }

    public Turista(Account account, CartaDiCredito cartaDiCredito) {
        this.account = account;
        this.cartaDiCredito = cartaDiCredito;
    }

    public void changeUsername(String username) {
        account.changeUsername(username);
    }

    public void changePassword(String oldPassword, String newPassword) {
        account.changePassword(oldPassword, newPassword);
    }

    public void changeCartaDiCredito(CartaDiCredito cartaDiCredito) {
        this.cartaDiCredito = cartaDiCredito;
    }

}
