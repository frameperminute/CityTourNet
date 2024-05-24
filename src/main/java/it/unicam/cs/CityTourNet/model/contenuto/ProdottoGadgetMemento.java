package it.unicam.cs.CityTourNet.model.contenuto;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(force = true)
@DiscriminatorValue("ProdottoGadget")
public class ProdottoGadgetMemento extends ContenutoMemento {

    private final int prezzo;

    private final int numPezzi;

    private final String filepath;

    public ProdottoGadgetMemento(ProdottoGadget prodottoGadget) {
        super(prodottoGadget);
        this.prezzo = prodottoGadget.getPrezzo();
        this.numPezzi = prodottoGadget.getNumPezzi();
        this.filepath = prodottoGadget.getFilepath();
    }

}
