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
@DiscriminatorValue("ContributorAutorizzato")
public class ContributorAutorizzato extends Utente{
    private int esitiNegativi;

    public ContributorAutorizzato(String username, String email, String password) {
        super(username, email, password);
        this.esitiNegativi = 0;
    }
}
