package it.unicam.cs.CityTourNet.model.contenuto;

import java.time.Duration;
import java.util.List;

public class Itinerario extends Contenuto{
    private List<POI> POIsPerItinerario;
    private Difficolta difficolta;
    private int ore;
    private int minuti;

    public Itinerario(String nome, String descrizione, String usernameAutore, List<POI> POIsPerItinerario,
                      Difficolta difficolta, int ore, int minuti) {
        super(nome, descrizione, usernameAutore);
        this.POIsPerItinerario = POIsPerItinerario;
        this.difficolta = difficolta;
        this.setDurata(ore,minuti);
    }

    public List<POI> getPOIsPerItinerario() {
        return POIsPerItinerario;
    }

    public void addPOIPerItinerario(POI POIDaAggiungere) {
        this.POIsPerItinerario.add(POIDaAggiungere);
    }

    public void removePOIPerItinerario(int numPOI) {
        if(this.POIsPerItinerario.size() > 2) {
            this.POIsPerItinerario.remove(numPOI);
        }
    }

    public Difficolta getDifficolta() {
        return difficolta;
    }

    public void setDifficolta(Difficolta difficolta) {
        this.difficolta = difficolta;
    }

    public String getDurata(){
        return this.ore+" h "+this.minuti+" min ";
    }

    public void setDurata(int ore, int minuti) {
        if(checkDurata(ore, minuti)) {
            this.ore = ore;
            this.minuti = minuti;
        }
    }

    private boolean checkDurata(int ore, int minuti) {
        return 0 <= ore && ore <= 24 && 0 <= minuti && minuti <= 60;
    }
}
