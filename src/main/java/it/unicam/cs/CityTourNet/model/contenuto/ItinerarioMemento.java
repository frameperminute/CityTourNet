package it.unicam.cs.CityTourNet.model.contenuto;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor(force = true)
@DiscriminatorValue("Itinerario")
public class ItinerarioMemento extends ContenutoMemento{

    private List<Long> indiciPOIs;
    private String difficolta;
    private int ore;
    private int minuti;

    public ItinerarioMemento(Itinerario itinerario) {
        super(itinerario);
        this.indiciPOIs = itinerario.getIndiciPOIs();
        this.minuti = itinerario.getMinuti();
        this.ore = itinerario.getOre();
        this.difficolta = itinerario.getDifficolta();
    }
}
