package it.unicam.cs.CityTourNet.model.utente;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(force = true)
@DiscriminatorValue("Contributor")
public class Contributor extends Utente{
    public Contributor(String username, String email, String password) {
        super(username, email, password);
    }
}
