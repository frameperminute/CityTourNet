package it.unicam.cs.CityTourNet.model.utente;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@NoArgsConstructor(force = true)
@DiscriminatorValue("TuristaAutenticato")
public class TuristaAutenticato extends Utente implements Acquirente {

    private int punti;
    private LocalDateTime dataInizioAutenticazione;

    public TuristaAutenticato(String username, String email, String password) {
        super(username, email, password);
        this.dataInizioAutenticazione = LocalDateTime.now();
    }
}
