package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.utente.Utente;


public class ContestDecorator implements Contest {

    private final Contest contest;

    public ContestDecorator(Contest contest) {
        this.contest = contest;
    }


    @Override
    public Utente getPartecipante(String username) {
        return this.contest.getPartecipante(username);
    }

    @Override
    public boolean addPartecipante(Utente partecipante) {
        return this.contest.addPartecipante(partecipante);
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
