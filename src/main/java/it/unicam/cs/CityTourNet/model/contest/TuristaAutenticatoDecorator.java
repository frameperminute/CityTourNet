package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.utente.TuristaAutenticato;
import it.unicam.cs.CityTourNet.model.utente.Utente;
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

    @Override
    public boolean addPartecipante(Utente partecipante) {
        if(partecipante instanceof TuristaAutenticato) {
            super.addPartecipante(partecipante);
        }
        return true;
    }
}
