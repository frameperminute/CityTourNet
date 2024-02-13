package it.unicam.cs.CityTourNet.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Animatore {
    private final Account account;

    private String tematica;

    private LocalDateTime dataInizioContest;

    private LocalDateTime dataFineContest;

    private boolean contestTerminato;

    private boolean contestPosticipato;

    private List<Messaggio> inArrivo;

    private int numeroContenutiRicevuti;

    public Animatore(String username, String password) {
        this.account = new Account(AccountType.ANIMATORE, username, password);
        this.contestTerminato = true;
        this.contestPosticipato = false;
    }

    public void riceviMessaggio(Messaggio messaggio) {
        this.inArrivo.add(messaggio);
    }

    public void eliminaUltimoMessaggio(String password) {
        if(this.account.isPasswordCorrect(password)) {
            this.inArrivo.remove(this.inArrivo.size()-1);
        }
    }

    public void eliminaTuttiIMessaggi(String password) {
        if(this.account.isPasswordCorrect(password)) {
            this.inArrivo.clear();
        }
    }

    public void annunciaContest(String tematica, boolean turisti,
                                boolean turistiAutenticati, boolean contributors) {
        if(this.contestTerminato) {
            this.tematica = tematica;
            this.contestTerminato = false;
            this.dataInizioContest = LocalDateTime.now().plusHours(24);
            this.inviaNotifica(turisti, turistiAutenticati, contributors);
        }
    }

    public void avviaContest(boolean turisti, boolean turistiAutenticati,
                             boolean contributors, LocalDateTime dataFineContest) {
        if(LocalDateTime.now().isAfter(this.dataInizioContest)) {
            if(ChronoUnit.DAYS.between(this.dataInizioContest, dataFineContest) >= 7) {
                if(inArrivo.size() >= 5) {
                    this.dataFineContest = dataFineContest;
                    this.inviaNotifica(turisti, turistiAutenticati, contributors);
                } else {
                    this.contestTerminato = true;
                }
            }
        }
    }

    public void terminaContest() {
        if(LocalDateTime.now().isAfter(this.dataFineContest)) {
            if(this.numeroContenutiRicevuti >= 5) {
                //TODO: decretare vincitore e assegnargli il premio
            } else if(!this.contestPosticipato) {
                this.dataFineContest = this.dataFineContest.plusDays(3);
                this.contestPosticipato = true;
            } else {
                this.contestTerminato = true;
                this.contestPosticipato = false;
            }
        }
    }
    public void inviaNotifica(boolean turisti, boolean turistiAutenticati, boolean contributors) {
        if(turisti) {
            for(Turista t : GestoreDellaPiattaforma.getTuristaList()) {
                t.riceviMessaggio(new Messaggio(this.tematica));
            }
        }
        if(turistiAutenticati) {
            for(TuristaAutenticato t : GestoreDellaPiattaforma.getTuristaAutenticatoList()) {
                t.riceviMessaggio(new Messaggio(this.tematica));
            }
        }
        if(contributors) {
            for(Contributor c : GestoreDellaPiattaforma.getContributorList()) {
                c.riceviMessaggio(new Messaggio(this.tematica));
            }
        }
    }

}
