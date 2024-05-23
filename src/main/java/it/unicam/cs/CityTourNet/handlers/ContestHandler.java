package it.unicam.cs.CityTourNet.handlers;

import it.unicam.cs.CityTourNet.model.Notifica;
import it.unicam.cs.CityTourNet.model.contenuto.Contenuto;
import it.unicam.cs.CityTourNet.model.contenuto.Itinerario;
import it.unicam.cs.CityTourNet.model.contenuto.POI;
import it.unicam.cs.CityTourNet.model.contest.*;
import it.unicam.cs.CityTourNet.model.utente.*;
import it.unicam.cs.CityTourNet.repositories.ContenutoRepository;
import it.unicam.cs.CityTourNet.repositories.NotificaRepository;
import it.unicam.cs.CityTourNet.repositories.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;



@Service
public class ContestHandler {

    private final UtenteRepository utenteRepository;
    private final NotificaRepository notificaRepository;
    private final ContenutoRepository contenutoRepository;
    private Contest contest;
    private boolean isContestItinerario;
    private boolean isContestPOI;
    private boolean isContestWithTuristiAutenticati;
    private boolean isContestWithContributors;
    private boolean isAttivo;

    @Autowired
    public ContestHandler(UtenteRepository utenteRepository, NotificaRepository notificaRepository,
                          ContenutoRepository contenutoRepository){
        this.utenteRepository = utenteRepository;
        this.notificaRepository = notificaRepository;
        this.contenutoRepository = contenutoRepository;
    }

    public boolean isAttivo() {
        return this.isAttivo;
    }

    public boolean selezionaOpzioneItinerario(){
        if(!this.isAttivo){
            this.isContestItinerario = true;
            this.isContestPOI = false;
        }
        return true;
    }

    public boolean selezionaOpzionePOI(){
        if(!this.isAttivo){
            this.isContestPOI = true;
            this.isContestItinerario = false;
        }
        return true;
    }

    public boolean selezionaOpzioneTuristiAutenticati(){
        if(!this.isAttivo){
            this.isContestWithTuristiAutenticati = !this.isContestWithTuristiAutenticati;
        }
        return this.isContestWithTuristiAutenticati;
    }

    public boolean selezionaOpzioneContributors(){
        if(!this.isAttivo){
            this.isContestWithContributors = !this.isContestWithContributors;
        }
        return this.isContestWithContributors;
    }

    public boolean creaContest(ConcreteContest contest){
        if(!this.isAttivo && this.controllaOpzioni()){
            this.isAttivo = true;
            this.contest = contest;
            this.attivaOpzioni();
            this.inviaInformazioniContest();
            return true;
        }
        return false;
    }

    private boolean controllaOpzioni(){
        boolean isContenutoAttivo = false;
        boolean isPartecipantiAttivo = false;
        if(this.isContestItinerario){
            isContenutoAttivo = true;
        }
        if(this.isContestPOI){
            isContenutoAttivo = true;
        }
        if(this.isContestWithTuristiAutenticati){
            isPartecipantiAttivo = true;
        }
        if(this.isContestWithContributors){
            isPartecipantiAttivo = true;
        }
        return isContenutoAttivo && isPartecipantiAttivo;
    }

    private void attivaOpzioni(){
        if(this.isContestItinerario) {
            this.contest = new ItinerarioDecorator(this.contest);
        }
        if(this.isContestPOI){
            this.contest = new POIDecorator(this.contest);
        }
        if(this.isContestWithTuristiAutenticati){
            this.contest = new TuristaAutenticatoDecorator(this.contest);
        }
        if(this.isContestWithContributors){
            this.contest = new ContributorDecorator(this.contest);
        }
    }

    private boolean inviaInformazioniContest(){
        String testo = this.contest.getInfoContest() + "\nIl contenuto da caricare dev'essere un ";
        if(this.isContestItinerario){
            testo += "Itinerario.\n";
        }
        if(this.isContestPOI){
            testo += "POI.\n";
        }
        testo += "Se vuoi partecipare, rispondi con ('SI') entro 24 ore.";
        String testoDefinitivo = testo;
        if(this.isContestWithTuristiAutenticati) {
            this.utenteRepository.findAllByTipoUtente("TuristaAutenticato")
                    .forEach(t -> this.notificaRepository
                            .saveAndFlush(new Notifica(contest.getUsernameAutore(), t.getUsername(), testoDefinitivo)));

        }
        if(this.isContestWithContributors) {
            this.utenteRepository.findAllByTipoUtente("Contributor")
                    .forEach(t -> this.notificaRepository
                            .saveAndFlush(new Notifica(contest.getUsernameAutore(), t.getUsername(), testoDefinitivo)));

        }
        return true;
    }

    public boolean addPartecipanti() {
        List<Notifica> notifichePartecipanti = this.getNotifichePartecipanti();
        if(notifichePartecipanti.size() >= 5) {
            List<String> usernames = notifichePartecipanti.stream()
                    .map(Notifica::getUsernameMittente).toList();
            List<Utente> partecipanti = this.utenteRepository.findAllById(usernames);
            partecipanti.forEach(this.contest::addPartecipante);
            this.notificaRepository.deleteAll(notifichePartecipanti);
            return true;
        }
        this.terminaContest();
        return false;
    }

    private List<Notifica> getNotifichePartecipanti(){
        return this.notificaRepository.findAll()
                .stream()
                .filter(n -> n.getUsernameDestinatario().equals(contest.getUsernameAutore()) &&
                        n.leggi().equalsIgnoreCase("SI"))
                .toList();
        }

    public boolean caricaPOIPerContest(POI poi) {
        if(this.isContestPOI) {
            this.caricaContenutoPerContest(poi);
            return true;
        }
        return false;
    }

    public boolean caricaItinerarioPerContest(Itinerario itinerario) {
        if(this.isContestItinerario) {
            this.caricaContenutoPerContest(itinerario);
            return true;
        }
        return false;
    }

    private void caricaContenutoPerContest(Contenuto contenuto) {
        List<Contenuto> conts = this.contest.getContenuti()
                .stream()
                .filter(cont -> cont.getUsernameAutore().equals(contenuto.getUsernameAutore()))
                .toList();
        if (conts.isEmpty()) {
            contenuto.setForContest(true);
            this.contest.addContenuto(contenuto);
            this.addPunti(contenuto.getUsernameAutore());
        }
    }

    private void addPunti(String username) {
        Utente utente = this.utenteRepository.findById(username).get();
        if(utente instanceof TuristaAutenticato turistaAutenticato) {
            int puntiTotali = turistaAutenticato.getPunti()+this.getGestore().getPuntiPartecipazioneContest();
            turistaAutenticato.setPunti(puntiTotali);
            this.utenteRepository.saveAndFlush(turistaAutenticato);
        }
    }

    public List<POI> getPOIsPartecipanti(){
        List<POI> pois = this.contest.getContenuti().stream()
                .filter(c -> c instanceof POI)
                .map(c -> (POI) c)
                .toList();
        return pois;
    }

    public List<Itinerario> getItinerariPartecipanti(){
        return this.contest.getContenuti().stream()
                .filter(c -> c instanceof Itinerario)
                .map(c -> (Itinerario) c)
                .toList();
    }

    private GestoreDellaPiattaforma getGestore() {
        return (GestoreDellaPiattaforma) this.utenteRepository.findAllByTipoUtente("GestoreDellaPiattaforma")
                .stream().findFirst().orElse(null);
    }

    public boolean premiaVincitore(String usernameAnimatore, Utente vincitore){
        if(this.contest.getContenuti()
                .stream()
                .filter(contenuto -> contenuto.getUsernameAutore().equals(vincitore.getUsername()))
                .toList()
                .isEmpty()) {
            return false;
        }
        if (vincitore instanceof TuristaAutenticato turistaAutenticato){
            int puntiTotali = turistaAutenticato.getPunti() + this.getGestore().getPuntiVittoriaContest();
            turistaAutenticato.setPunti(puntiTotali);
            this.utenteRepository.saveAndFlush(turistaAutenticato);
        }
        if(vincitore instanceof Contributor){
            ContributorAutorizzato contributorAutorizzato = new ContributorAutorizzato(vincitore.getUsername(),
                    vincitore.getEmail(),vincitore.getPassword());
            this.utenteRepository.delete(vincitore);
            this.utenteRepository.saveAndFlush(contributorAutorizzato);
        }
        Contenuto daCaricare = this.contest.getContenuti().stream()
                .filter(contenuto -> contenuto.getUsernameAutore().equals(vincitore.getUsername()))
                .findFirst().orElse(null);
        this.caricaContenutoVincitore(daCaricare);
        this.contest.removeContenuto(daCaricare);
        this.terminaContest();
        this.notificaRepository.saveAndFlush(new Notifica(usernameAnimatore, vincitore.getUsername(),
                "Complimenti " + vincitore.getUsername() + " hai vinto il contest!"));
        return true;
    }

    private void caricaContenutoVincitore(Contenuto contenuto) {
        if(contenuto != null) {
            contenuto.setDefinitive(true);
            contenuto.setForContest(false);
            this.contenutoRepository.saveAndFlush(contenuto);
        }
    }

    private void terminaContest(){
        if(this.isContestPOI) {
            this.cancellaFilePoiNonVincitori(this.contest.getContenuti());
        }
        this.notificaRepository.deleteAllInBatch(this.notificaRepository.findAll()
                .stream().filter(n -> n.getUsernameMittente().equals(this.contest.getUsernameAutore())).toList());
        this.isAttivo = false;
        this.isContestItinerario = false;
        this.isContestPOI = false;
        this.isContestWithTuristiAutenticati = false;
        this.isContestWithContributors = false;
        this.contest = null;
    }

    private void cancellaFilePoiNonVincitori(List<Contenuto> daCancellare) {
        daCancellare.forEach(poi ->
                {
                    File fileDaCancellare = new File(((POI) poi).getFilepath());
                    if (fileDaCancellare.exists()) {
                        fileDaCancellare.delete();
                    }
                });
    }

}
