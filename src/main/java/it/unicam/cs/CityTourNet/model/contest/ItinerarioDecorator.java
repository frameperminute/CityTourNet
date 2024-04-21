package it.unicam.cs.CityTourNet.model.contest;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(force = true)
@DiscriminatorValue("ItinerarioDecorator")
public class ItinerarioDecorator extends ContestDecorator{
    public ItinerarioDecorator(Contest contest) {
        super(contest);
    }

}
