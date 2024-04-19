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
    private String usernameMittente;
    @Getter
    private String usernameDestinatario;
    private String testo;
    @Getter
    private boolean letto;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private long ID;

    public Notifica(String usernameMittente, String usernameDestinatario, String testo) {
        this.usernameMittente = usernameMittente;
        this.usernameDestinatario = usernameDestinatario;
        this.testo = testo;
        this.letto = false;
    }

    public String leggi() {
        this.letto = true;
        return this.testo;
    }

}
