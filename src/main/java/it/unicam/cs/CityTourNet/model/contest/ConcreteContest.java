package it.unicam.cs.CityTourNet.model.contest;

import it.unicam.cs.CityTourNet.model.contenuto.Contenuto;
import it.unicam.cs.CityTourNet.model.utente.Utente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


public class ConcreteContest implements Contest {

    private final String usernameAutore;

    private final LocalDate dataFine;

    private final String tematica;

    private List<Utente> partecipanti;

    private List<Contenuto> contenuti;



    public ConcreteContest(String dataFine, String tematica, String usernameAutore) {
        this.dataFine = LocalDate.parse(dataFine, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        this.tematica = tematica;
        this.usernameAutore = usernameAutore;
        this.partecipanti = new ArrayList<>();
        this.contenuti = new ArrayList<>();
    }

    @Override
    public String getInfoContest() {
        return "Il contest presenta la seguente tematica: \n" + this.tematica + "\n" +
                "e termina il: \n" + this.dataFine.getDayOfMonth()+"/"+this.dataFine.getMonthValue();
    }

    @Override
    public String getTempoResiduo() {
        long giorniResidui = LocalDateTime.now().until(this.dataFine, ChronoUnit.DAYS);
        long oreResidue = LocalDateTime.now().until(this.dataFine, ChronoUnit.HOURS);
        long minutiResidui = LocalDateTime.now().until(this.dataFine, ChronoUnit.MINUTES);
        return "Il contest terminera' tra: \n" + giorniResidui + " giorni, "
                + oreResidue + " ore e " + minutiResidui + " minuti";
    }

    @Override
    public List<Utente> getPartecipanti() {
        return this.partecipanti;
    }

    @Override
    public Utente getUtenteByUsername(String username) {
        for(Utente u : this.partecipanti) {
            if(u.getUsername().equals(username)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public List<Contenuto> getContenuti() {
        return this.contenuti;
    }

    @Override
    public boolean addContenuto(Contenuto contenuto) {
        return this.contenuti.add(contenuto);
    }

    @Override
    public boolean addPartecipante(Utente partecipante) {
        return this.partecipanti.add(partecipante);
    }

    @Override
    public boolean removeContenuto(Contenuto contenuto) {
        return this.contenuti.remove(contenuto);
    }

    @Override
    public String getUsernameAutore() {
        return this.usernameAutore;
    }

}

