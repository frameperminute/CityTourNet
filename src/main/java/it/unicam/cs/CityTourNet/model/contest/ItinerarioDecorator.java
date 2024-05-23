package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.contenuto.Contenuto;
import it.unicam.cs.CityTourNet.model.contenuto.Itinerario;
import it.unicam.cs.CityTourNet.model.utente.Contributor;
import it.unicam.cs.CityTourNet.model.utente.Utente;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

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
