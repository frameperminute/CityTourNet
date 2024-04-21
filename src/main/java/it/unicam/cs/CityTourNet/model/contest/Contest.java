package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.utente.Utente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_contest", discriminatorType = DiscriminatorType.STRING)
@NoArgsConstructor(force = true)
@Getter
public abstract class Contest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;
    @Column(name = "tipo_contest", insertable = false, updatable = false)
    protected String tipoContest;

    abstract public Utente getPartecipante(String username);

    abstract public boolean addPartecipante(Utente partecipante);

    abstract public String getInfoContest();

    abstract public String getTempoResiduo();
}
