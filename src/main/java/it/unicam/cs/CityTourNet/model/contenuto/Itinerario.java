package it.unicam.cs.CityTourNet.model.contenuto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor(force = true)
@DiscriminatorValue("Itinerario")
public class Itinerario extends Contenuto{

    @Getter
    @ElementCollection
    private List<Long> indiciPOIs;
    @Setter
    @Getter
    private String difficolta;
    private int ore;
    private int minuti;

    public Itinerario(String nome, String descrizione, String usernameAutore, List<Long> indiciPOIs,
                      String difficolta, int ore, int minuti) {
        super(nome, descrizione, usernameAutore);
        this.indiciPOIs = indiciPOIs;
        this.difficolta = difficolta;
        this.ore = ore;
        this.minuti = minuti;
    }

    public String getDurata(){
        return this.ore+" h "+this.minuti+" min ";
    }

}
