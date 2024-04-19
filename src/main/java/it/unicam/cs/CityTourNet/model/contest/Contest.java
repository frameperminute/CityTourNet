package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.utente.Utente;


public interface Contest {

    Utente getPartecipante(String username);

    boolean addPartecipante(Utente partecipante);

    String getInfoContest();

    String getTempoResiduo();

}
