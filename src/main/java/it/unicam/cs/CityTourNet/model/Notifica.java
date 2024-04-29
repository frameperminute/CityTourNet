package it.unicam.cs.CityTourNet.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor(force = true)
public class Notifica {
    private String usernameMittente;
    private String usernameDestinatario;
    private String testo;
    @Setter
    private boolean letto;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
