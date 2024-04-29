package it.unicam.cs.CityTourNet.model.contest;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("ConcreteContest")
@NoArgsConstructor(force = true)
@Getter
public class ConcreteContest extends Contest {
    protected String usernameAutore;

    protected LocalDate dataFine;

    protected String tematica;

    public ConcreteContest(String dataFine, String tematica, String usernameAutore) {
        this.dataFine = LocalDate.parse(dataFine, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        this.tematica = tematica;
        this.usernameAutore = usernameAutore;
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

}

