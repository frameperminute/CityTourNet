package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.contenuto.Contenuto;
import it.unicam.cs.CityTourNet.model.utente.Utente;

import java.util.List;

public class ContestDecorator implements Contest {

    private Contest contest;

    public ContestDecorator(Contest contest) {
        this.contest = contest;
    }


    @Override
    public List<Utente> getPartecipanti() {
        return this.contest.getPartecipanti();
    }

    @Override
    public String getUsernameAutore(Contenuto contenuto) {
        return this.contest.getUsernameAutore(contenuto);
    }

    @Override
    public List<Contenuto> getContenuti() {
        return this.contest.getContenuti();
    }

    @Override
    public void addPartecipanti(List<Utente> partecipanti) {
        this.contest.addPartecipanti(partecipanti);
    }

    @Override
    public void addContenuto(Contenuto contenuto) {
        this.contest.addContenuto(contenuto);
    }

    @Override
    public String getInfoContest() {
        return this.contest.getInfoContest();
    }

    @Override
    public String getTempoResiduo() {
        return this.contest.getTempoResiduo();
    }
}
