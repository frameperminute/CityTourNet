package it.unicam.cs.CityTourNet.model.contenuto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@DiscriminatorColumn(name = "tipo_contenuto", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor(force = true)
public abstract class Contenuto {
    @Setter
    private String nome;

    @Setter
    private String descrizione;

    private final String usernameAutore;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;

    @Column(name = "tipo_contenuto", insertable = false, updatable = false)
    protected String tipoContenuto;
    @Setter
    protected boolean isForContest;
    @Setter
    protected boolean isInPending;
    @Setter
    protected boolean isDefinitive;
    @Setter
    protected LocalDateTime dataCreazione;

    public Contenuto(String nome, String descrizione, String usernameAutore) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.usernameAutore = usernameAutore;
        this.dataCreazione = LocalDateTime.now();
    }
}
