package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.contenuto.Contenuto;
import it.unicam.cs.CityTourNet.model.contenuto.Itinerario;
import it.unicam.cs.CityTourNet.model.contenuto.POI;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

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
