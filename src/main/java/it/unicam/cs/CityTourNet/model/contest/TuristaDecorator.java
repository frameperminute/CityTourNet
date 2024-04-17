package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.utente.Contributor;
import it.unicam.cs.CityTourNet.model.utente.Turista;
import it.unicam.cs.CityTourNet.model.utente.Utente;

import java.util.ArrayList;
import java.util.List;

public class TuristaDecorator extends ContestDecorator{
    public TuristaDecorator(Contest contest) {
        super(contest);
    }

    @Override
    public void addPartecipanti(List<Utente> partecipanti) {
        List<Utente> partecipantiAmmessi = new ArrayList<Utente>();
        for (Utente utente : partecipanti) {
            if(utente instanceof Turista) {
                partecipantiAmmessi.add((Turista) utente);
            }
        }
        super.addPartecipanti(partecipantiAmmessi);
    }
}
