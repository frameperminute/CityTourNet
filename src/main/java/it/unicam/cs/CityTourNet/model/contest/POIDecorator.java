package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.contenuto.Contenuto;
import it.unicam.cs.CityTourNet.model.contenuto.POI;
import it.unicam.cs.CityTourNet.model.utente.Turista;
import it.unicam.cs.CityTourNet.model.utente.Utente;

import java.util.ArrayList;
import java.util.List;

public class POIDecorator extends ContestDecorator{
    public POIDecorator(Contest contest) {
        super(contest);
    }

    @Override
    public void addContenuto(Contenuto contenuto) {
        if(contenuto instanceof POI) {
            super.addContenuto((POI) contenuto);
        }
    }
}
