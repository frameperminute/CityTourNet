package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.utente.Contributor;
import it.unicam.cs.CityTourNet.model.utente.TuristaAutenticato;
import it.unicam.cs.CityTourNet.model.utente.Utente;

import java.util.ArrayList;
import java.util.List;

public class TuristaAutenticatoDecorator extends ContestDecorator{
    public TuristaAutenticatoDecorator(Contest contest) {
        super(contest);
    }

    @Override
    public void addPartecipanti(List<Utente> partecipanti) {
        List<Utente> partecipantiAmmessi = new ArrayList<Utente>();
        for (Utente utente : partecipanti) {
            if(utente instanceof TuristaAutenticato) {
                partecipantiAmmessi.add((TuristaAutenticato) utente);
            }
        }
        super.addPartecipanti(partecipantiAmmessi);
    }
}
