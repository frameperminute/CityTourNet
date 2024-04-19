package it.unicam.cs.CityTourNet.handlers;

import it.unicam.cs.CityTourNet.model.contenuto.Contenuto;
import it.unicam.cs.CityTourNet.model.utente.Contributor;
import it.unicam.cs.CityTourNet.model.utente.ContributorAutorizzato;
import it.unicam.cs.CityTourNet.model.utente.Turista;
import it.unicam.cs.CityTourNet.model.utente.TuristaAutenticato;
import it.unicam.cs.CityTourNet.repositories.ContenutoRepository;
import it.unicam.cs.CityTourNet.repositories.NotificaRepository;
import it.unicam.cs.CityTourNet.repositories.UtenteRepository;
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

    @Autowired
    public AuthHandler(ContenutoRepository contenutoRepository, UtenteRepository utenteRepository,
                       NotificaRepository notificaRepository){
        this.contenutoRepository = contenutoRepository;
        this.utenteRepository = utenteRepository;
        this.notificaRepository = notificaRepository;
    }

    public List<TuristaAutenticato> getTuristiAutenticatiInScadenza(){
        return this.getTuristiAutenticati()
                .stream()
                .filter(t -> t.getDataInizioAutenticazione().until(LocalDateTime.now(), ChronoUnit.DAYS) > 30)
                .toList();
    }

    public List<TuristaAutenticato> getTuristiAutenticati(){
        return this.utenteRepository.findByTipoUtente("TuristaAutenticato")
                .stream()
                .map(t -> (TuristaAutenticato) t)
                .toList();
    }

    public List<ContributorAutorizzato> getContributorAutorizzati(){
        return this.utenteRepository.findByTipoUtente("ContributorAutorizzato")
                .stream()
                .map(t -> (ContributorAutorizzato) t)
                .toList();
    }

    public boolean removeAutenticazione(){
        List<TuristaAutenticato> turistiAutenticatiInScadenza = this.getTuristiAutenticatiInScadenza();
        List<Turista> listaTuristi = turistiAutenticatiInScadenza
                .stream()
                .map(t -> new Turista(t.getUsername(),t.getEmail(),t.getPassword()))
                .toList();
        this.utenteRepository.deleteAll(turistiAutenticatiInScadenza);
        this.utenteRepository.saveAllAndFlush(listaTuristi);
        return true;
    }

    public boolean richiediAutenticazione(String username, int puntiPerAutenticazione){
        Turista turista = (Turista) this.utenteRepository.getReferenceById(username);
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

    public boolean richiediAutorizzazione(String username, int contenutiMinimiPerAutorizzazione){
        Contributor contributor = (Contributor) this.utenteRepository.getReferenceById(username);
        List<Contenuto> contenutiCaricati = this.contenutoRepository.findContenutiByUsernameAutore(username);
        if(contenutiCaricati.size() >= contenutiMinimiPerAutorizzazione){
            ContributorAutorizzato contributorAutorizzato = new ContributorAutorizzato(contributor.getUsername(),
                    contributor.getEmail(),contributor.getPassword());
            this.utenteRepository.delete(contributor);
            this.utenteRepository.saveAndFlush(contributorAutorizzato);
            return true;
        }
        return false;
    }

    public boolean gestisciAutorizzazione(){
        this.getContributorAutorizzati()
                .stream()
                .filter( c -> this.contenutoRepository.findContenutiByUsernameAutore(c.getUsername()).isEmpty())
                .map(c -> this.controllaContenutoContributorAutorizzato(c.getUsername())
                );
            return true;
    }

    public boolean controllaContenutoContributorAutorizzato(String username){
        ContributorAutorizzato daControllare =
                (ContributorAutorizzato) this.utenteRepository.getReferenceById(username);
        daControllare.setEsitiNegativi(daControllare.getEsitiNegativi()+1);
        if(daControllare.getEsitiNegativi() == 3){
            Contributor nuovoContributor = new Contributor(daControllare.getUsername(),
                    daControllare.getEmail(), daControllare.getPassword());
            this.utenteRepository.delete(daControllare);
            this.utenteRepository.saveAndFlush(nuovoContributor);
            return true;
        }
        return false;
    }
}
