package it.unicam.cs.CityTourNet.model.contenuto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@DiscriminatorColumn(name = "tipo_memento", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor(force = true)
public abstract class ContenutoMemento {

    private String nome;

    private String descrizione;

    private final String usernameAutore;

    private long IDContenuto;

    protected String tipoContenuto;

    protected boolean isInPending;

    protected boolean isDefinitive;

    protected LocalDateTime dataCreazione;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public ContenutoMemento(Contenuto contenuto) {
        this.nome = contenuto.getNome();
        this.descrizione = contenuto.getDescrizione();
        this.usernameAutore = contenuto.getUsernameAutore();
        this.IDContenuto = contenuto.getID();
        this.tipoContenuto = contenuto.getTipoContenuto();
        this.isInPending = contenuto.isInPending();
        this.isDefinitive = contenuto.isDefinitive();
        this.dataCreazione = contenuto.getDataCreazione();
    }

}
