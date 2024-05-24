package it.unicam.cs.CityTourNet.model.contenuto;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(force = true)
@DiscriminatorValue("POI")
public class POIMemento extends ContenutoMemento{

    private String filepath;

    public POIMemento(POI poi) {
        super(poi);
        this.filepath = poi.getFilepath();
    }
}
