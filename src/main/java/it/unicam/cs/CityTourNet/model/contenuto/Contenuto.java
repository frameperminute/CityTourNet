package it.unicam.cs.CityTourNet.model.contenuto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_contenuto", discriminatorType = DiscriminatorType.STRING)
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

    public Contenuto(String nome, String descrizione, String usernameAutore) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.usernameAutore = usernameAutore;
    }
}
