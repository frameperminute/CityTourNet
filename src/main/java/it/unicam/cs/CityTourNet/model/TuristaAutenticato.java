package it.unicam.cs.CityTourNet.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TuristaAutenticato {
    private final Account account;
    private final LocalDateTime dataFineAutenticazione;
    private CartaDiCredito cartaDiCredito;


    public TuristaAutenticato(Account account, CartaDiCredito cartaDiCredito) {
        this.account = account;
        this.dataFineAutenticazione = LocalDateTime.now().plusMonths(1);
        this.cartaDiCredito = cartaDiCredito;
    }

    public int giorniResiduiAutenticazione() {
        return (int) ChronoUnit.DAYS.between(LocalDateTime.now(),this.dataFineAutenticazione);
    }

    public Account getAccount() {
        return this.account;
    }


}
