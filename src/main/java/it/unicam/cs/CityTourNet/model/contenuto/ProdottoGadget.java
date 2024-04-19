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
@DiscriminatorValue("ProdottoGadget")
public class ProdottoGadget extends Contenuto{
    private int prezzo;
    private int numPezzi;
    private File multimedia;
    public ProdottoGadget(String nome, String descrizione, String usernameAutore,int prezzo, int numPezzi,
                          File multimedia) {
        super(nome, descrizione, usernameAutore);
        this.prezzo = prezzo;
        this.numPezzi = numPezzi;
        this.multimedia = multimedia;
    }

}
