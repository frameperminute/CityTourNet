package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.contenuto.Contenuto;
import it.unicam.cs.CityTourNet.model.contenuto.Itinerario;
import it.unicam.cs.CityTourNet.model.contenuto.POI;

public class ItinerarioDecorator extends ContestDecorator{
    public ItinerarioDecorator(Contest contest) {
        super(contest);
    }

    @Override
    public void addContenuto(Contenuto contenuto) {
        if(contenuto instanceof Itinerario) {
            super.addContenuto((Itinerario) contenuto);
        }
    }
}
