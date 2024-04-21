package it.unicam.cs.CityTourNet.model.contest;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(force = true)
@DiscriminatorValue("POIDecorator")
public class POIDecorator extends ContestDecorator{
    public POIDecorator(Contest contest) {
        super(contest);
    }

}
