package it.unicam.cs.CityTourNet.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Animatore {
    private String tematica;

    private LocalDateTime dataInizioContest;

    private LocalDateTime dataFineContest;


    public Animatore() {}


    public void creaContest(String tematica, LocalDateTime dataInizioContest,
                            LocalDateTime dataFineContest) {
       this.tematica = tematica;
       this.dataInizioContest = dataInizioContest;
       this.dataFineContest = dataFineContest;
    }






}
