package it.unicam.cs.CityTourNet.model.utente;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(force = true)
@DiscriminatorValue("GestoreDellaPiattaforma")
public class GestoreDellaPiattaforma extends Utente{
    public GestoreDellaPiattaforma(String username, String email, String password) {
        super(username, email, password);
    }
}
