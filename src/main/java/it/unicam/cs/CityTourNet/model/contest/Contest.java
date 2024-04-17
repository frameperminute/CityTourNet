package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.contenuto.Contenuto;
import it.unicam.cs.CityTourNet.model.utente.Utente;

import java.util.List;

public interface Contest {

    List<Utente> getPartecipanti();

    String getUsernameAutore(Contenuto contenuto);

    List<Contenuto> getContenuti();

    void addPartecipanti(List<Utente> partecipanti);

    void addContenuto(Contenuto contenuto);

    String getInfoContest();

    String getTempoResiduo();

}
