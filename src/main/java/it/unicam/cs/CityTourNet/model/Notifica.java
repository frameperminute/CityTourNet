package it.unicam.cs.CityTourNet.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(force = true)
public class Notifica {
    @Getter
    private String emailMittente;
    @Getter
    private String emailDestinatario;
    private String testo;
    @Getter
    private boolean letto;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private long ID;

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

}
