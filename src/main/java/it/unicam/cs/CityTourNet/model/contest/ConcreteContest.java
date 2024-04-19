package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.contenuto.Contenuto;
import it.unicam.cs.CityTourNet.model.utente.Utente;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ConcreteContest implements Contest {
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Utente> partecipanti;
    private LocalDateTime dataFine;

    private String tematica;

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

