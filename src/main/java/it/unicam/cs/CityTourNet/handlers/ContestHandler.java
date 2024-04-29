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
import java.util.ArrayList;
import java.util.List;



@Service
public class ContestHandler {

    private final UtenteRepository utenteRepository;
    private final NotificaRepository notificaRepository;
    private final ContenutoRepository contenutoRepository;
    private Contest contest;
    private List<String> usernamePartecipanti;
    private boolean isContestItinerario;
    private boolean isContestPOI;
    private boolean isContestWithTuristi;
    private boolean isContestWithTuristiAutenticati;
    private boolean isContestWithContributors;
    private boolean isAttivo;

    @Autowired
    public ContestHandler(UtenteRepository utenteRepository, NotificaRepository notificaRepository,
                          ContenutoRepository contenutoRepository){
        this.utenteRepository = utenteRepository;
        this.notificaRepository = notificaRepository;
        this.contenutoRepository = contenutoRepository;
        this.usernamePartecipanti = new ArrayList<>();
    }

    public boolean isAttivo() {
        return this.isAttivo;
    }

    public boolean selezionaOpzioneItinerario(){
        this.isContestItinerario = true;
        this.isContestPOI = false;
        return true;

    }

    public boolean selezionaOpzionePOI(){
        this.isContestPOI = true;
        this.isContestItinerario = false;
        return true;
    }

    public boolean selezionaOpzioneTuristi(){
        this.isContestWithTuristi = !this.isContestWithTuristi;
        return this.isContestWithTuristi;
    }

    public boolean selezionaOpzioneTuristiAutenticati(){
        this.isContestWithTuristiAutenticati = !this.isContestWithTuristiAutenticati;
        return this.isContestWithTuristiAutenticati;
    }

    public boolean selezionaOpzioneContributors(){
        this.isContestWithContributors = !this.isContestWithContributors;
        return this.isContestWithContributors;
    }

    public boolean creaContest(ConcreteContest contest){
        if(!this.isAttivo && this.controllaOpzioni()){
            this.isAttivo = true;
            this.contest = contest;
            this.attivaOpzioni();
            this.inviaInformazioniContest(contest.getUsernameAutore());
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
        if(this.isContestWithTuristi){
            isPartecipantiAttivo = true;
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
        if(this.isContestWithTuristi){
            this.contest = new TuristaDecorator(this.contest);
        }
        if(this.isContestWithTuristiAutenticati){
            this.contest = new TuristaAutenticatoDecorator(this.contest);
        }
        if(this.isContestWithContributors){
            this.contest = new ContributorDecorator(this.contest);
        }
    }

    private boolean inviaInformazioniContest(String username){
        String testo = this.contest.getInfoContest() + "\nIl contenuto da caricare dev'essere un ";
        if(this.isContestItinerario){
            testo += "Itinerario.\n";
        }
        if(this.isContestPOI){
            testo += "POI.\n";
        }
        testo += "Se vuoi partecipare, rispondi con ('SI') entro 24 ore.";
        String testoDefinitivo = testo;
        if(this.isContestWithTuristi) {
            this.utenteRepository.findAllByTipoUtente("Turista")
                    .forEach(t -> this.notificaRepository
                            .saveAndFlush(new Notifica(username, t.getUsername(), testoDefinitivo)));

        }
        if(this.isContestWithTuristiAutenticati) {
            this.utenteRepository.findAllByTipoUtente("TuristaAutenticato")
                    .forEach(t -> this.notificaRepository
                            .saveAndFlush(new Notifica(username, t.getUsername(), testoDefinitivo)));

        }
        if(this.isContestWithTuristi) {
            this.utenteRepository.findAllByTipoUtente("Contributor")
                    .forEach(t -> this.notificaRepository
                            .saveAndFlush(new Notifica(username, t.getUsername(), testoDefinitivo)));

        }
        return true;
    }

    public boolean addPartecipanti(String username) {
        List<Notifica> notifichePartecipanti = this.getNotifichePartecipanti(username);
        if(notifichePartecipanti.size() >= 5) {
            List<String> usernames = notifichePartecipanti.stream()
                    .map(Notifica::getUsernameMittente).toList();
            this.usernamePartecipanti.addAll(usernames);
            this.notificaRepository.deleteAll(notifichePartecipanti);
            return true;
        }
        this.terminaContest(username);
        return false;
    }

    private List<Notifica> getNotifichePartecipanti(String username){
        return this.notificaRepository.findAll()
                .stream()
                .filter(n -> n.getUsernameDestinatario().equals(username) &&
                        n.leggi().equalsIgnoreCase("SI"))
                .toList();
        }

    public boolean caricaPOIPerContest(POI poi) {
        List<Contenuto> cont = this.contenutoRepository
                .findContenutiByUsernameAutore(poi.getUsernameAutore());
        if(cont.isEmpty() || cont.stream().filter(Contenuto::isForContest).toList().isEmpty()) {
            poi.setForContest(true);
            this.contenutoRepository.saveAndFlush(poi);
            return true;
        }
        return false;
    }

    public boolean caricaItinerarioPerContest(Itinerario itinerario) {
        List<Contenuto> cont = this.contenutoRepository
                .findContenutiByUsernameAutore(itinerario.getUsernameAutore());
        if(cont.isEmpty() || cont.stream().filter(Contenuto::isForContest).toList().isEmpty()) {
            itinerario.setForContest(true);
            this.contenutoRepository.saveAndFlush(itinerario);
            return true;
        }
        return false;
    }

    public boolean eliminaContenutoPerContest(String username) {
        List<Contenuto> cont = this.contenutoRepository.findContenutiByUsernameAutore(username);
        if(!cont.isEmpty()) {
            Contenuto daEliminare = cont.stream().filter(Contenuto::isForContest).findFirst().get();
            if (daEliminare instanceof POI) {
                File fileDaCancellare = new File(((POI) daEliminare).getFilepath());
                if (fileDaCancellare.exists()) {
                    fileDaCancellare.delete();
                }
            }
            this.contenutoRepository.deleteById(daEliminare.getID());
        }
        return true;
    }

    public List<POI> getPOIsPartecipanti(){
        return this.contenutoRepository.findAll().stream()
                .filter(c -> c.getTipoContenuto().equals("POI") && c.isForContest())
                .map(c -> (POI) c)
                .toList();
    }

    public List<Itinerario> getItinerariPartecipanti(){
        return this.contenutoRepository.findAll().stream()
                .filter(c -> c.getTipoContenuto().equals("Itinerario") && c.isForContest())
                .map(c -> (Itinerario) c)
                .toList();
    }

    private GestoreDellaPiattaforma getGestore() {
        return (GestoreDellaPiattaforma) this.utenteRepository.findAllByTipoUtente("GestoreDellaPiattaforma")
                .stream().findFirst().orElse(null);
    }

    public boolean premiaVincitore(String usernameAnimatore, Utente vincitore){
        if(!this.usernamePartecipanti.contains(vincitore.getUsername())) {
            return false;
        }
        int puntiPremio = this.getGestore().getPuntiPerAutenticazione();
        if(vincitore instanceof Turista){
            TuristaAutenticato turistaAutenticato = new TuristaAutenticato(vincitore.getUsername(),
                    vincitore.getEmail(),vincitore.getPassword());
            turistaAutenticato.setPunti(((Turista) vincitore).getPunti() + puntiPremio);
            this.utenteRepository.delete(vincitore);
            this.utenteRepository.saveAndFlush(turistaAutenticato);
        }
        if (vincitore instanceof TuristaAutenticato turistaAutenticato){
            turistaAutenticato.setPunti(turistaAutenticato.getPunti() + puntiPremio);
            this.utenteRepository.saveAndFlush(turistaAutenticato);
        }
        if(vincitore instanceof Contributor){
            ContributorAutorizzato contributorAutorizzato = new ContributorAutorizzato(vincitore.getUsername(),
                    vincitore.getEmail(),vincitore.getPassword());
            this.utenteRepository.delete(vincitore);
            this.utenteRepository.saveAndFlush(contributorAutorizzato);
        }
        this.contenutoRepository.findContenutiByUsernameAutore(vincitore.getUsername())
                .stream().filter(Contenuto::isForContest).findFirst().ifPresent(this::caricaContenutoVincitore);
        this.terminaContest(usernameAnimatore);
        this.notificaRepository.saveAndFlush(new Notifica(usernameAnimatore, vincitore.getUsername(),
                "Complimenti " + vincitore.getUsername() + " hai vinto il contest!"));
        return true;
    }

    private void caricaContenutoVincitore(Contenuto contenuto) {
        contenuto.setDefinitive(true);
        contenuto.setForContest(false);
        this.contenutoRepository.saveAndFlush(contenuto);
    }

    private void terminaContest(String usernameAnimatore){
        this.isAttivo = false;
        this.isContestItinerario = false;
        this.isContestPOI = false;
        this.isContestWithTuristi = false;
        this.isContestWithTuristiAutenticati = false;
        this.isContestWithContributors = false;
        this.contest = new ConcreteContest();
        this.usernamePartecipanti = new ArrayList<>();
        List<Contenuto> daCancellare = this.contenutoRepository.findAll().stream()
                .filter(Contenuto::isForContest).toList();
        this.cancellaFilePoiNonVincitori(daCancellare);
        this.contenutoRepository.deleteAllInBatch(daCancellare);
        this.notificaRepository.deleteAllInBatch(this.notificaRepository.findAll()
                .stream().filter(n -> n.getUsernameMittente().equals(usernameAnimatore)).toList());
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
