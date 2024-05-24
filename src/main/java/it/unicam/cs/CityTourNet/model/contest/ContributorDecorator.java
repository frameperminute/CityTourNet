package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.utente.Contributor;
import it.unicam.cs.CityTourNet.model.utente.TuristaAutenticato;
import it.unicam.cs.CityTourNet.model.utente.Utente;

public class ContributorDecorator extends ContestDecorator {

    public ContributorDecorator(Contest contest) {
        super(contest);
    }

    @Override
    public boolean addPartecipante(Utente partecipante) {
        if (partecipante instanceof Contributor ||
                this.contest instanceof TuristaAutenticatoDecorator && partecipante instanceof TuristaAutenticato) {
            return contest.addPartecipante(partecipante);
        }
        return false;
    }

}
