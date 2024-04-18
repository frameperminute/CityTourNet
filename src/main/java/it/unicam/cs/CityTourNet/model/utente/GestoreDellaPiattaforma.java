package it.unicam.cs.CityTourNet.model.utente;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor(force = true)
@DiscriminatorValue("GestoreDellaPiattaforma")
public class GestoreDellaPiattaforma extends Utente{

    private int puntiPerAutenticazione;
    private int contenutiMinimiPerAutorizzazione;

    public GestoreDellaPiattaforma(String username, String email, String password) {
        super(username, email, password);
    }
}
