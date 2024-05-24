package it.unicam.cs.CityTourNet.model.contenuto;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor(force = true)
@DiscriminatorValue("POI")
public class POI extends Contenuto{

    private String filepath;

    public POI(String nome, String descrizione, String usernameAutore) {
        super(nome, descrizione, usernameAutore);
    }

    @Override
    public POIMemento createMemento() {
        return new POIMemento(this);
    }

    @Override
    public void restoreMemento(ContenutoMemento memento) {
        super.restoreMemento(memento);
        if (memento instanceof POIMemento) {
            this.filepath = ((POIMemento) memento).getFilepath();
        }
    }
}
