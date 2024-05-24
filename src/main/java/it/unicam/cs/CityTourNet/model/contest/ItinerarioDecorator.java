package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.contenuto.Contenuto;
import it.unicam.cs.CityTourNet.model.contenuto.Itinerario;

public class ItinerarioDecorator extends ContestDecorator{
    public ItinerarioDecorator(Contest contest) {
        super(contest);
    }

    @Override
    public boolean addContenuto(Contenuto contenuto) {
        if (contenuto instanceof Itinerario) {
            return contest.addContenuto(contenuto);
        }
        return false;
    }

}
