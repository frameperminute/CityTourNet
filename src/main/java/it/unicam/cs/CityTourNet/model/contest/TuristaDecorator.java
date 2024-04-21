package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.utente.Turista;
import it.unicam.cs.CityTourNet.model.utente.Utente;
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

    @Override
    public boolean addPartecipante(Utente partecipante) {
        if(partecipante instanceof Turista) {
            super.addPartecipante(partecipante);
        }
        return true;
    }
}
