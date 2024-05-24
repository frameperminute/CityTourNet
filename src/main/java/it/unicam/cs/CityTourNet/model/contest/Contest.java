package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.contenuto.Contenuto;
import it.unicam.cs.CityTourNet.model.utente.Utente;

import java.util.List;

public interface Contest {

    String getInfoContest();

    String getTempoResiduo();

    List<Utente> getPartecipanti();

    Utente getUtenteByUsername(String username);

    List<Contenuto> getContenuti();

    boolean addContenuto(Contenuto contenuto);

    boolean addPartecipante(Utente partecipante);

    boolean removeContenuto(Contenuto contenuto);

    String getUsernameAutore();
}
