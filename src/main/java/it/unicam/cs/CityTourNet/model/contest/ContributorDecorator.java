package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.utente.Contributor;
import it.unicam.cs.CityTourNet.model.utente.Utente;

import java.util.ArrayList;
import java.util.List;

public class ContributorDecorator extends ContestDecorator {

    public ContributorDecorator(Contest contest) {
        super(contest);
    }

    @Override
    public void addPartecipanti(List<Utente> partecipanti) {
        List<Utente> partecipantiAmmessi = new ArrayList<Utente>();
        for (Utente utente : partecipanti) {
            if(utente instanceof Contributor) {
                partecipantiAmmessi.add((Contributor) utente);
            }
        }
        super.addPartecipanti(partecipantiAmmessi);
    }
}
