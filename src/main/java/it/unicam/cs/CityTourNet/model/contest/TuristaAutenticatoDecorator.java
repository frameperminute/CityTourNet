package it.unicam.cs.CityTourNet.model.contest;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(force = true)
@DiscriminatorValue("TuristaAutenticatoDecorator")
public class TuristaAutenticatoDecorator extends ContestDecorator{
    public TuristaAutenticatoDecorator(Contest contest) {
        super(contest);
    }

}
