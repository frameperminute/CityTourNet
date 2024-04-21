package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.utente.Contributor;
import it.unicam.cs.CityTourNet.model.utente.Utente;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(force = true)
@DiscriminatorValue("ContributorDecorator")
public class ContributorDecorator extends ContestDecorator {

    public ContributorDecorator(Contest contest) {
        super(contest);
    }

    @Override
    public boolean addPartecipante(Utente partecipante) {
        if(partecipante instanceof Contributor) {
            super.addPartecipante(partecipante);
        }
        return true;
    }
}
