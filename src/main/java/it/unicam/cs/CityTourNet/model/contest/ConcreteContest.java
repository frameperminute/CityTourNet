package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.contenuto.Contenuto;
import it.unicam.cs.CityTourNet.model.utente.Utente;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ConcreteContest implements Contest {
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Utente> partecipanti;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Contenuto> contenutiContest;

    private LocalDateTime dataFine;

    private String tematica;

    public ConcreteContest (LocalDateTime dataFine, String tematica){
        this.dataFine = dataFine;
        this.tematica = tematica;
    }

    @Override
    public List<Utente> getPartecipanti() {
        return this.partecipanti;
    }

    @Override
    public String getUsernameAutore(Contenuto contenuto) {
        if(contenutiContest.contains(contenuto)){
            return contenuto.getUsernameAutore();
        }
        return "";
    }

    @Override
    public List<Contenuto> getContenuti() {
        return this.contenutiContest;
    }

    @Override
    public void addPartecipanti(List<Utente> partecipanti) {
        this.partecipanti.addAll(partecipanti);
    }

    @Override
    public void addContenuto(Contenuto contenuto) {
        this.contenutiContest.add(contenuto);
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

