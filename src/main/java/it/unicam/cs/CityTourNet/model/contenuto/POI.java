package it.unicam.cs.CityTourNet.model.contenuto;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;

@Setter
@Getter
@Entity
@NoArgsConstructor(force = true)
@DiscriminatorValue("POI")
public class POI extends Contenuto{
    private File multimedia;

    public POI(String nome, String descrizione, String usernameAutore, File multimedia) {
        super(nome, descrizione, usernameAutore);
        this.multimedia = multimedia;
    }
}
