package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.utente.Utente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("ConcreteContest")
@NoArgsConstructor(force = true)
public class ConcreteContest extends Contest {
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Utente> partecipanti;
    private final LocalDateTime dataFine;

    private final String tematica;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;
    @Column(name = "tipo_contest", insertable = false, updatable = false)
    protected String tipoContest;

    public ConcreteContest (LocalDateTime dataFine, String tematica){
        this.dataFine = dataFine;
        this.tematica = tematica;
    }

    @Override
    public Utente getPartecipante(String username) {
        return this.partecipanti.stream()
                .filter(x -> x.getUsername().equals(username))
                .findFirst().orElse(null);
    }

    @Override
    public boolean addPartecipante(Utente partecipante) {
        return this.partecipanti.add(partecipante);
    }

    @Override
    public String getInfoContest() {
        return "Il contest presenta la seguente tematica: \n" + this.tematica + "\n" +
                "e termina il: \n" + this.dataFine.getDayOfMonth()+"/"+this.dataFine.getMonth() +
                "alle ore: \n" +  this.dataFine.getHour()+":"+this.dataFine.getMinute();
    }

    @Override
    public String getTempoResiduo() {
        long giorniResidui = LocalDateTime.now().until(this.dataFine, ChronoUnit.DAYS);
        long oreResidue = LocalDateTime.now().until(this.dataFine, ChronoUnit.HOURS);
        long minutiResidui = LocalDateTime.now().until(this.dataFine, ChronoUnit.MINUTES);
        return "Il contest terminera' tra: \n" + giorniResidui + " giorni, "
                + oreResidue + " ore e " + minutiResidui + " minuti";
    }

}

