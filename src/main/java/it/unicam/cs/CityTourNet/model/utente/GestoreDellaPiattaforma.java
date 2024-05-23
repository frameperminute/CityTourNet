package it.unicam.cs.CityTourNet.model.utente;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor(force = true)
@DiscriminatorValue("GestoreDellaPiattaforma")
public class GestoreDellaPiattaforma extends Utente{

    private int puntiPartecipazioneContest = 10;
    private int puntiVittoriaContest = 100;
    private int contenutiMinimiPerAutorizzazione = 50;

    public GestoreDellaPiattaforma(String username, String email, String password) {
        super(username, email, password);
    }
}
