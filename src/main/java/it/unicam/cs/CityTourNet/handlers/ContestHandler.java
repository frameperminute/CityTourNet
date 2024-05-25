package it.unicam.cs.CityTourNet.handlers;

import it.unicam.cs.CityTourNet.model.Notifica;
import it.unicam.cs.CityTourNet.model.contenuto.Contenuto;
import it.unicam.cs.CityTourNet.model.contenuto.Itinerario;
import it.unicam.cs.CityTourNet.model.contenuto.POI;
import it.unicam.cs.CityTourNet.model.contest.*;
import it.unicam.cs.CityTourNet.model.utente.*;
import it.unicam.cs.CityTourNet.repositories.ContenutoMementoRepository;
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
    private final ContenutoMementoRepository contenutoMementoRepository;
    private Contest contest;
    private boolean isContestItinerario;
    private boolean isContestPOI;
    private boolean isContestWithTuristiAutenticati;
    private boolean isContestWithContributors;
    private boolean isAttivo;

    @Autowired
    public ContestHandler(UtenteRepository utenteRepository, NotificaRepository notificaRepository,
                          ContenutoRepository contenutoRepository, ContenutoMementoRepository contenutoMementoRepository){
        this.utenteRepository = utenteRepository;
        this.notificaRepository = notificaRepository;
        this.contenutoRepository = contenutoRepository;
        this.contenutoMementoRepository = contenutoMementoRepository;
    }

    public boolean isAttivo() {
        return this.isAttivo;
    }

    public void selezionaOpzioneItinerario(){
        if(!this.isAttivo){
            this.isContestItinerario = true;
            this.isContestPOI = false;
        }
    }

    public void selezionaOpzionePOI(){
        if(!this.isAttivo){
            this.isContestPOI = true;
            this.isContestItinerario = false;
        }
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

    private void inviaInformazioniContest(){
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
        if(this.isContestPOI && this.contest.getPartecipanti()
                .stream()
                .anyMatch(p -> p.getUsername().equals(poi.getUsernameAutore()))) {
            this.caricaContenutoPerContest(poi);
            return true;
        }
        return false;
    }

    public boolean caricaItinerarioPerContest(Itinerario itinerario) {
        if(this.isContestItinerario && this.contest.getPartecipanti()
                .stream()
                .anyMatch(p -> p.getUsername().equals(itinerario.getUsernameAutore()))) {
            this.caricaContenutoPerContest(itinerario);
            return true;
        }
        return false;
    }

    private void caricaContenutoPerContest(Contenuto contenuto) {
        if (this.contest.getContenuti()
                .stream()
                .noneMatch(cont -> cont.getUsernameAutore().equals(contenuto.getUsernameAutore()))) {
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
        return this.contest.getContenuti().stream()
                .filter(c -> c instanceof POI)
                .map(c -> (POI) c)
                .toList();
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
                .noneMatch(contenuto -> contenuto.getUsernameAutore().equals(vincitore.getUsername()))) {
            return false;
        }
        this.scegliPremio(vincitore);
        this.caricaContenutoVincitore(vincitore);
        this.terminaContest();
        this.notificaRepository.saveAndFlush(new Notifica(usernameAnimatore, vincitore.getUsername(),
                "Complimenti " + vincitore.getUsername() + " hai vinto il contest!"));
        return true;
    }

    private void scegliPremio(Utente vincitore){
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
    }

    private void caricaContenutoVincitore(Utente vincitore) {
        Contenuto daCaricare = this.contest.getContenuti()
                .stream()
                .filter(c -> c.getUsernameAutore().equals(vincitore.getUsername()))
                .findFirst().orElse(null);
        if(daCaricare != null) {
            daCaricare.setDefinitive(true);
            this.salvaStato(daCaricare);
            this.contenutoRepository.saveAndFlush(daCaricare);
            this.contest.removeContenuto(daCaricare);
        }
    }

    private void salvaStato(Contenuto contenuto){
        this.contenutoMementoRepository.saveAndFlush(contenuto.createMemento());
    }

    private void terminaContest(){
        if(this.isContestPOI) {
            this.cancellaFilePoiNonVincitori(this.contest.getContenuti());
        }
        this.notificaRepository.deleteAllInBatch(this.notificaRepository.findAll()
                .stream()
                .filter(n -> n.getUsernameMittente().equals(this.contest.getUsernameAutore()))
                .toList());
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
