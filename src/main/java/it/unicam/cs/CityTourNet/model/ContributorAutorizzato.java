package it.unicam.cs.CityTourNet.model;

public class ContributorAutorizzato {
    private final Contributor contributor;

    private boolean autorizzato;

    public ContributorAutorizzato(Contributor contributor) {
        this.contributor = contributor;
        this.autorizzato = true;
    }


}
