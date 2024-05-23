package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.contenuto.Contenuto;
import it.unicam.cs.CityTourNet.model.utente.Utente;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.List;

public class ContestDecorator implements Contest {

    protected Contest contest;

    public ContestDecorator(Contest contest) {
        this.contest = contest;
    }

    @Override
    public String getInfoContest() {
        return this.contest.getInfoContest();
    }

    @Override
    public String getTempoResiduo() {
        return this.contest.getTempoResiduo();
    }

    @Override
    public List<Utente> getPartecipanti() {
        return contest.getPartecipanti();
    }

    @Override
    public Utente getUtenteByUsername(String username) {
        return contest.getUtenteByUsername(username);
    }

    @Override
    public List<Contenuto> getContenuti() {
        return contest.getContenuti();
    }

    @Override
    public boolean addContenuto(Contenuto contenuto) {
        return contest.addContenuto(contenuto);
    }

    @Override
    public boolean addPartecipante(Utente partecipante) {
        return contest.addPartecipante(partecipante);
    }

    @Override
    public boolean removeContenuto(Contenuto contenuto) {
        return contest.removeContenuto(contenuto);
    }

    @Override
    public String getUsernameAutore() {
        return contest.getUsernameAutore();
    }

}
