package it.unicam.cs.CityTourNet.model.contest;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(force = true)
@DiscriminatorValue("TuristaDecorator")
public class TuristaDecorator extends ContestDecorator{
    public TuristaDecorator(Contest contest) {
        super(contest);
    }

}
