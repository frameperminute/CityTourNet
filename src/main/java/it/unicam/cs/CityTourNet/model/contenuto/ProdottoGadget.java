package it.unicam.cs.CityTourNet.model.contenuto;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor(force = true)
@DiscriminatorValue("ProdottoGadget")
public class ProdottoGadget extends Contenuto{
    private int prezzo;
    private int numPezzi;
    private String filepath;
    public ProdottoGadget(String nome, String descrizione, String usernameAutore,int prezzo, int numPezzi) {
        super(nome, descrizione, usernameAutore);
        this.prezzo = prezzo;
        this.numPezzi = numPezzi;
        this.isDefinitive = true;
    }

}
