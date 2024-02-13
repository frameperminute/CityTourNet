package it.unicam.cs.CityTourNet.model;

import java.util.List;

public record GestoreDellaPiattaforma() {
    private static List<Turista> turistaList;

    private static List<TuristaAutenticato> turistaAutenticatoList;

    private static List<Contributor> contributorList;

    private static List<ContributorAutorizzato> contributorAutorizzatoList;

    public static List<Turista> getTuristaList() {
        return turistaList;
    }

    public static void addTurista(String username, String password) {
        turistaList.add(new Turista(username, password));
    }
    public static void addTurista(String username, String password, CartaDiCredito cartaDiCredito) {
        turistaList.add(new Turista(username, password, cartaDiCredito));
    }

    public static List<TuristaAutenticato> getTuristaAutenticatoList() {
        return turistaAutenticatoList;
    }

    public static void addTuristaAutenticato(Account account, CartaDiCredito cartaDiCredito) {
        turistaAutenticatoList.add(new TuristaAutenticato(account, cartaDiCredito));
    }

    public static List<Contributor> getContributorList() {
        return contributorList;
    }

    public static void addContributor(String username, String password) {
        contributorList.add(new Contributor(username, password));
    }

    public static List<ContributorAutorizzato> getContributorAutorizzatoList() {
        return contributorAutorizzatoList;
    }

    public static void addContributorAutorizzato(Account account) {
        contributorAutorizzatoList.add(new ContributorAutorizzato(account));
    }
}
