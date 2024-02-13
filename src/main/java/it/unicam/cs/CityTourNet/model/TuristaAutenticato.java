package it.unicam.cs.CityTourNet.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TuristaAutenticato {
    private final Account account;
    private final LocalDateTime dataFineAutenticazione;
    private CartaDiCredito cartaDiCredito;

    private List<Messaggio> messaggi;


    public TuristaAutenticato(Account account, CartaDiCredito cartaDiCredito) {
        this.account = account;
        this.dataFineAutenticazione = LocalDateTime.now().plusMonths(1);
        this.cartaDiCredito = cartaDiCredito;
    }

    public int giorniResiduiAutenticazione() {
        return (int) ChronoUnit.DAYS.between(LocalDateTime.now(),this.dataFineAutenticazione);
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
