package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.utente.Contributor;
import it.unicam.cs.CityTourNet.model.utente.TuristaAutenticato;
import it.unicam.cs.CityTourNet.model.utente.Utente;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

public class TuristaAutenticatoDecorator extends ContestDecorator{
    public TuristaAutenticatoDecorator(Contest contest) {
        super(contest);
    }

    @Override
    public boolean addPartecipante(Utente partecipante) {
        if (partecipante instanceof TuristaAutenticato ||
                this.contest instanceof ContributorDecorator && partecipante instanceof Contributor) {
            return contest.addPartecipante(partecipante);
        }
        return false;
    }

}
