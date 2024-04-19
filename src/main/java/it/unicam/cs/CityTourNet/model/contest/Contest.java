package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.utente.Utente;

import java.util.List;

public interface Contest {

    Utente getPartecipante(String username);

    boolean addPartecipante(Utente partecipante);

    String getInfoContest();

    String getTempoResiduo();

}
