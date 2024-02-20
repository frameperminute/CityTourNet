package it.unicam.cs.CityTourNet.model;

import java.util.List;

public class GestoreDellaPiattaforma {
    private List<Turista> turistaList;

    private List<TuristaAutenticato> turistaAutenticatoList;

    private List<Contributor> contributorList;

    private List<ContributorAutorizzato> contributorAutorizzatoList;


    public GestoreDellaPiattaforma(){}
    public List<Turista> getTuristaList() {
        return turistaList;
    }

    public void addTurista(String username, String password) {
        turistaList.add(new Turista(username, password));
    }
    public void addTurista(String username, String password, CartaDiCredito cartaDiCredito) {
        turistaList.add(new Turista(username, password, cartaDiCredito));
    }

    public List<TuristaAutenticato> getTuristaAutenticatoList() {
        return turistaAutenticatoList;
    }

    public void addTuristaAutenticato(Account account, CartaDiCredito cartaDiCredito) {
        turistaAutenticatoList.add(new TuristaAutenticato(account, cartaDiCredito));
    }

    public List<Contributor> getContributorList() {
        return contributorList;
    }

    public void addContributor(String username, String password) {
        contributorList.add(new Contributor(username, password));
    }

    public List<ContributorAutorizzato> getContributorAutorizzatoList() {
        return contributorAutorizzatoList;
    }

    public void addContributorAutorizzato(Account account) {
        contributorAutorizzatoList.add(new ContributorAutorizzato(account));
    }
}
