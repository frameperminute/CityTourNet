package it.unicam.cs.CityTourNet.model.contenuto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(force = true)
@DiscriminatorValue("Itinerario")
public class Itinerario extends Contenuto{

    @ElementCollection
    private List<Long> indiciPOIs;
    @Setter
    private String difficolta;
    private int ore;
    private int minuti;

    public Itinerario(String nome, String descrizione, String usernameAutore, List<Long> indiciPOIs,
                      String difficolta, int ore, int minuti) {
        super(nome, descrizione, usernameAutore);
        this.indiciPOIs = indiciPOIs;
        this.difficolta = difficolta;
        this.ore = ore;
        this.minuti = minuti;
    }

    public String getDurata(){
        return this.ore+" h "+this.minuti+" min ";
    }
    @Override
    public ItinerarioMemento createMemento() {
        return new ItinerarioMemento(this);
    }

    @Override
    public void restoreMemento(ContenutoMemento memento) {
        super.restoreMemento(memento);
        if (memento instanceof ItinerarioMemento itinerarioMemento) {
            this.indiciPOIs = new ArrayList<>(itinerarioMemento.getIndiciPOIs());
            this.difficolta = itinerarioMemento.getDifficolta();
            this.ore = itinerarioMemento.getOre();
            this.minuti = itinerarioMemento.getMinuti();
        }
    }

}
