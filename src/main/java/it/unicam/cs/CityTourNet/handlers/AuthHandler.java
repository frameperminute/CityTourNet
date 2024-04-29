package it.unicam.cs.CityTourNet.handlers;

import it.unicam.cs.CityTourNet.model.Notifica;
import it.unicam.cs.CityTourNet.model.contenuto.Contenuto;
import it.unicam.cs.CityTourNet.model.utente.*;
import it.unicam.cs.CityTourNet.repositories.ContenutoRepository;
import it.unicam.cs.CityTourNet.repositories.NotificaRepository;
import it.unicam.cs.CityTourNet.repositories.UtenteRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Service
public class AuthHandler {

    private final ContenutoRepository contenutoRepository;
    private final UtenteRepository utenteRepository;
    private final NotificaRepository notificaRepository;
    @Getter
    @Setter
    private boolean isContestAttivo;

    @Autowired
    public AuthHandler(ContenutoRepository contenutoRepository, UtenteRepository utenteRepository,
                       NotificaRepository notificaRepository){
        this.contenutoRepository = contenutoRepository;
        this.utenteRepository = utenteRepository;
        this.notificaRepository = notificaRepository;
        this.isContestAttivo = false;
    }

    public boolean isGestore(String username, String password){
        if(this.utenteRepository.existsById(username)) {
            Utente utente = this.utenteRepository.findById(username).get();
            return utente instanceof GestoreDellaPiattaforma && utente.getPassword().equals(password);
        }
        return false;
    }

    private List<TuristaAutenticato> getTuristiAutenticatiInScadenza(){
        return this.getTuristiAutenticati()
                .stream()
                .filter(t -> t.getDataInizioAutenticazione().until(LocalDateTime.now(), ChronoUnit.DAYS) > 30)
                .toList();
    }

    private List<TuristaAutenticato> getTuristiAutenticati(){
        return this.utenteRepository.findAllByTipoUtente("TuristaAutenticato")
                .stream()
                .map(t -> (TuristaAutenticato) t)
                .toList();
    }

    private List<ContributorAutorizzato> getContributorAutorizzati(){
        return this.utenteRepository.findAllByTipoUtente("ContributorAutorizzato")
                .stream()
                .map(t -> (ContributorAutorizzato) t)
                .toList();
    }

    private GestoreDellaPiattaforma getGestore() {
        return (GestoreDellaPiattaforma) this.utenteRepository.findAllByTipoUtente("GestoreDellaPiattaforma")
                .stream().findFirst().orElse(null);
    }

    public boolean eliminaAutenticazioni(){
        List<TuristaAutenticato> turistiAutenticatiInScadenza = this.getTuristiAutenticatiInScadenza();
        if(!turistiAutenticatiInScadenza.isEmpty()) {
            List<Turista> listaTuristi = turistiAutenticatiInScadenza
                    .stream()
                    .map(t -> {
                        Turista turista = new Turista(t.getUsername(), t.getEmail(), t.getPassword());
                        turista.setPunti(t.getPunti());
                        return turista;
                    })
                    .toList();
            this.utenteRepository.deleteAll(turistiAutenticatiInScadenza);
            this.utenteRepository.saveAllAndFlush(listaTuristi);
            listaTuristi.forEach(t -> this.notificaRepository.saveAndFlush(
                            new Notifica(this.getGestore().getUsername(), t.getUsername(),
                                    "Autenticazione scaduta")));
        }
        return true;
    }

    public boolean richiediAutenticazione(String username){
        if(!this.utenteRepository.existsById(username) || this.getGestore() == null) {
            return false;
        }
        Turista turista = (Turista) this.utenteRepository.findById(username).get();
        int puntiPerAutenticazione = this.getGestore().getPuntiPerAutenticazione();
        if(turista.getPunti() >= puntiPerAutenticazione){
            TuristaAutenticato turistaAutenticato = new TuristaAutenticato(turista.getUsername(),
                    turista.getEmail(),turista.getPassword());
            turistaAutenticato.setPunti(turista.getPunti()-puntiPerAutenticazione);
            this.utenteRepository.delete(turista);
            this.utenteRepository.saveAndFlush(turistaAutenticato);
            return true;
        }
        return false;
    }

    public boolean richiediAutorizzazione(String username){
        if(!this.utenteRepository.existsById(username) || this.getGestore() == null) {
            return false;
        }
        Contributor contributor = (Contributor) this.utenteRepository.findById(username).get();
        List<Contenuto> contenutiCaricati = this.contenutoRepository.findContenutiByUsernameAutore(username);
        int contenutiMinimiPerAutorizzazione = this.getGestore().getContenutiMinimiPerAutorizzazione();
        if(contenutiCaricati.size() >= contenutiMinimiPerAutorizzazione){
            ContributorAutorizzato contributorAutorizzato = new ContributorAutorizzato(contributor.getUsername(),
                    contributor.getEmail(),contributor.getPassword());
            this.utenteRepository.delete(contributor);
            this.utenteRepository.saveAndFlush(contributorAutorizzato);
            return true;
        }
        return false;
    }

    public boolean gestisciAutorizzazioni(){
        this.getContributorAutorizzati()
                .stream()
                .filter(c -> this.findContenutiRecenti(c.getUsername()))
                .forEach(c -> this.gestisciEsitiNegativi(c.getUsername()));
            return true;
    }

    private boolean findContenutiRecenti(String username) {
        return this.contenutoRepository.findContenutiByUsernameAutore(username)
                .stream()
                .filter(cont -> cont.getDataCreazione()
                        .until(LocalDateTime.now(),ChronoUnit.DAYS) <= 7).toList().isEmpty();
    }

    public boolean gestisciEsitiNegativi(String username){
        if(!this.utenteRepository.existsById(username) || this.getGestore() == null) {
            return false;
        }
        ContributorAutorizzato daControllare =
                (ContributorAutorizzato) this.utenteRepository.findById(username).get();
        daControllare.setEsitiNegativi(daControllare.getEsitiNegativi()+1);
        if(daControllare.getEsitiNegativi() == 3){
            Contributor nuovoContributor = new Contributor(daControllare.getUsername(),
                    daControllare.getEmail(), daControllare.getPassword());
            this.utenteRepository.delete(daControllare);
            this.utenteRepository.saveAndFlush(nuovoContributor);
            this.notificaRepository.saveAndFlush(new Notifica(this.getGestore().getUsername(),
                    nuovoContributor.getUsername(),
                    "Autorizzazione scaduta"));
            return true;
        } else {
            this.utenteRepository.saveAndFlush(daControllare);
        }
        return false;
    }
}
