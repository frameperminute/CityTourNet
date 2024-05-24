package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.contenuto.Contenuto;
import it.unicam.cs.CityTourNet.model.contenuto.POI;

public class POIDecorator extends ContestDecorator{
    public POIDecorator(Contest contest) {
        super(contest);
    }

    @Override
    public boolean addContenuto(Contenuto contenuto) {
        if (contenuto instanceof POI) {
            return contest.addContenuto(contenuto);
        }
        return false;
    }


}
