package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.utente.Utente;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(force = true)
@DiscriminatorValue("ContestDecorator")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_decorator", discriminatorType = DiscriminatorType.STRING)
public class ContestDecorator extends Contest {

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "contest_id", referencedColumnName = "ID")
    private final Contest contest;

    public ContestDecorator(Contest contest) {
        this.contest = contest;
    }


    @Override
    public Utente getPartecipante(String username) {
        return this.contest.getPartecipante(username);
    }

    @Override
    public boolean addPartecipante(Utente partecipante) {
        return this.contest.addPartecipante(partecipante);
    }

    @Override
    public String getInfoContest() {
        return this.contest.getInfoContest();
    }

    @Override
    public String getTempoResiduo() {
        return this.contest.getTempoResiduo();
    }

}
