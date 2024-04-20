package it.unicam.cs.CityTourNet.handlers;

import it.unicam.cs.CityTourNet.model.Notifica;
import it.unicam.cs.CityTourNet.model.contenuto.Itinerario;
import it.unicam.cs.CityTourNet.model.contenuto.POI;
import it.unicam.cs.CityTourNet.model.contest.*;
import it.unicam.cs.CityTourNet.model.utente.*;
import it.unicam.cs.CityTourNet.repositories.ContenutoContestRepository;
import it.unicam.cs.CityTourNet.repositories.NotificaRepository;
import it.unicam.cs.CityTourNet.repositories.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;



@Service
public class ContestHandler {

    private final UtenteRepository utenteRepository;
    private final NotificaRepository notificaRepository;
    private final ContenutoContestRepository contenutoContestRepository;
    private Contest contest;
    private Utente utenteVincitore;
    private boolean isContestItinerario;
    private boolean isContestPOI;
    private boolean isContestWithTuristi;
    private boolean isContestWithTuristiAutenticati;
    private boolean isContestWithContributors;
    private boolean isAttivo;

    @Autowired
    public ContestHandler(UtenteRepository utenteRepository, NotificaRepository notificaRepository,
                            ContenutoContestRepository contenutoContestRepository){
        this.utenteRepository = utenteRepository;
        this.notificaRepository = notificaRepository;
        this.contenutoContestRepository = contenutoContestRepository;
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

    public boolean creaContest(LocalDateTime dataFine, String tematica, String username){
        if(!this.isAttivo && this.controllaOpzioni()){
            this.isAttivo = true;
            this.contest = new ConcreteContest(dataFine, tematica);
            this.inviaInformazioniContest(username);
            return true;
        }
        return false;
    }

    private boolean controllaOpzioni(){
        boolean isContenutoAttivo = false;
        boolean isPartecipantiAttivo = false;
        if(this.isContestItinerario){
            this.contest = new ItinerarioDecorator(this.contest);
            isContenutoAttivo = true;
        }
        if(this.isContestPOI){
            this.contest = new POIDecorator(this.contest);
            isContenutoAttivo = true;
        }
        if(this.isContestWithTuristi){
            this.contest = new TuristaDecorator(this.contest);
            isPartecipantiAttivo = true;
        }
        if(this.isContestWithTuristiAutenticati){
            this.contest = new TuristaAutenticatoDecorator(this.contest);
            isPartecipantiAttivo = true;
        }
        if(this.isContestWithContributors){
            this.contest = new ContributorDecorator(this.contest);
            isContenutoAttivo = true;
        }
        return isContenutoAttivo && isPartecipantiAttivo;
    }

    private boolean inviaInformazioniContest(String username){
        String testo = this.contest.getInfoContest() + "\n Il contenuto da caricare dev'essere un ";
        if(this.isContestItinerario){
            testo += "Itinerario.\n";
        }
        if(this.isContestPOI){
            testo += "POI.\n";
        }
        testo += "Se vuoi partecipare, rispondi con ('SI') entro 24 ore";
        String testoDefinitivo = testo;
        if(this.isContestWithTuristi) {
            this.utenteRepository.findByTipoUtente("Turista")
                    .stream()
                    .map(t -> this.notificaRepository
                            .saveAndFlush(new Notifica(username, t.getUsername(), testoDefinitivo)));

        }
        if(this.isContestWithTuristiAutenticati) {
            this.utenteRepository.findByTipoUtente("TuristaAutenticato")
                    .stream()
                    .map(t -> this.notificaRepository
                            .saveAndFlush(new Notifica(username, t.getUsername(), testoDefinitivo)));

        }
        if(this.isContestWithContributors) {
            this.utenteRepository.findByTipoUtente("Contributor")
                    .stream()
                    .map(t -> this.notificaRepository
                            .saveAndFlush(new Notifica(username, t.getUsername(), testoDefinitivo)));

        }
        return true;
    }

    public boolean addPartecipanti(String username) {
        List<Notifica> notifichePartecipanti = this.getNotifichePartecipanti(username);
            if(notifichePartecipanti.size() >= 5) {
                if (this.isContestWithTuristi) {
                    notifichePartecipanti.stream()
                            .map(n -> this.utenteRepository.getReferenceById(n.getUsernameMittente()))
                            .map(u -> this.contest.addPartecipante(u));

                }
                if (this.isContestWithTuristiAutenticati) {
                    notifichePartecipanti.stream()
                            .map(n -> this.utenteRepository.getReferenceById(n.getUsernameMittente()))
                            .map(u -> this.contest.addPartecipante(u));

                }
                if (this.isContestWithContributors) {
                    notifichePartecipanti.stream()
                            .map(n -> this.utenteRepository.getReferenceById(n.getUsernameMittente()))
                            .map(u -> this.contest.addPartecipante(u));

                }
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
        if(this.contenutoContestRepository.findByUsernameAutore(poi.getUsernameAutore()).isEmpty()) {
            this.contenutoContestRepository.save(poi);
            return true;
        }
        return false;
    }

    public boolean caricaItinerarioPerContest(Itinerario itinerario) {
        if(this.contenutoContestRepository.findByUsernameAutore(itinerario.getUsernameAutore()).isEmpty()) {
            this.contenutoContestRepository.save(itinerario);
            return true;
        }
        return false;
    }

    public boolean eliminaContenutoPerContest(String username) {
        this.contenutoContestRepository
                .deleteAllInBatch(this.contenutoContestRepository.findByUsernameAutore(username));
        return true;
    }

    public List<POI> getPOIsPartecipanti(){
        return this.contenutoContestRepository.findAll().stream()
                .filter(c -> c.getTipoContenuto().equals("POI"))
                .map(c -> (POI) c)
                .toList();
    }

    public List<Itinerario> getItinerariPartecipanti(){
        return this.contenutoContestRepository.findAll().stream()
                .filter(c -> c.getTipoContenuto().equals("Itinerario"))
                .map(c -> (Itinerario) c)
                .toList();
    }

    public boolean inviaUtenteVincitore(String usernameVincitore, String usernameAnimatore,
                                        String usernameGestore){
        this.utenteVincitore = this.contest.getPartecipante(usernameVincitore);
        this.notificaRepository.saveAndFlush(new Notifica(usernameAnimatore, usernameGestore,
                "Il vincitore del contest e': " + usernameVincitore));
        return true;
    }

    private GestoreDellaPiattaforma getGestore() {
        return (GestoreDellaPiattaforma) this.utenteRepository.findByTipoUtente("GestoreDellaPiattaforma")
                .stream().findFirst().orElse(null);
    }

    public boolean premiaVincitore(String usernameAnimatore){
        int puntiPremio = this.getGestore().getPuntiPerAutenticazione();
        if(this.utenteVincitore instanceof Turista){
            TuristaAutenticato turistaAutenticato = new TuristaAutenticato(this.utenteVincitore.getUsername(),
                    this.utenteVincitore.getEmail(),this.utenteVincitore.getPassword());
            turistaAutenticato.setPunti(((Turista) this.utenteVincitore).getPunti() + puntiPremio);
            this.utenteRepository.delete(this.utenteVincitore);
            this.utenteRepository.saveAndFlush(turistaAutenticato);
        }
        if (this.utenteVincitore instanceof TuristaAutenticato turistaAutenticato){
            turistaAutenticato.setPunti(turistaAutenticato.getPunti() + puntiPremio);
            this.utenteRepository.saveAndFlush(turistaAutenticato);
        }
        if(this.utenteVincitore instanceof Contributor){
            ContributorAutorizzato contributorAutorizzato = new ContributorAutorizzato(this.utenteVincitore.getUsername(),
                    this.utenteVincitore.getEmail(),this.utenteVincitore.getPassword());
            this.utenteRepository.delete(this.utenteVincitore);
            this.utenteRepository.saveAndFlush(contributorAutorizzato);
        }
        this.terminaContest(usernameAnimatore);
        this.notificaRepository.saveAndFlush(new Notifica(usernameAnimatore, this.utenteVincitore.getUsername(),
                "Complimenti " + this.utenteVincitore.getUsername() + " hai vinto il contest!"));
        return true;
    }

    private boolean terminaContest(String usernameAnimatore){
        this.isAttivo = false;
        this.isContestItinerario = false;
        this.isContestPOI = false;
        this.isContestWithTuristi = false;
        this.isContestWithTuristiAutenticati = false;
        this.isContestWithContributors = false;
        this.utenteVincitore = null;
        this.contest = null;
        this.contenutoContestRepository.deleteAllInBatch();
        this.notificaRepository.deleteAllInBatch(this.notificaRepository.findAll()
                .stream().filter(n -> n.getUsernameMittente().equals(usernameAnimatore)).toList());
        return true;
    }

}
