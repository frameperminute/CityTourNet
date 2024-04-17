package it.unicam.cs.CityTourNet.model;

public class Notifica {
    private String emailMittente;
    private String emailDestinatario;
    private String testo;
    private boolean letto;

    public Notifica(String emailMittente, String emailDestinatario, String testo) {
        this.emailMittente = emailMittente;
        this.emailDestinatario = emailDestinatario;
        this.testo = testo;
        this.letto = false;
    }

    public String leggi() {
        this.letto = true;
        return this.testo;
    }

    public boolean isLetto() {
        return letto;
    }

    public String getEmailMittente() {
        return emailMittente;
    }

    public String getEmailDestinatario() {
        return emailDestinatario;
    }

}
