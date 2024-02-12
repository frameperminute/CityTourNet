package it.unicam.cs.CityTourNet.model;

import java.time.LocalDate;

public class TuristaAutenticato {
    private final Account account;
    private final LocalDate dataFineAutenticazione;
    private CartaDiCredito cartaDiCredito;


    public TuristaAutenticato(Account account, CartaDiCredito cartaDiCredito) {
        this.account = account;
        this.dataFineAutenticazione = LocalDate.now().plusMonths(1);
        this.cartaDiCredito = cartaDiCredito;
    }



    public int giorniResiduiAutenticazione() {
        return LocalDate.now().until(this.dataFineAutenticazione).getDays();
    }

    public Account getAccount(Account adminAccount) {
        if(adminAccount.getAccountType() == AccountType.GESTORE_DELLA_PIATTAFORMA) {
            return this.account;
        }
        return null;
    }
}
