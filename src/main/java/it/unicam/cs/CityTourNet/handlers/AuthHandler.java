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

    /**
     * Verifica che il Contributor abbia il numero minimo di contenuti per poter diventare
     * ContributorAutorizzato e, in caso positivo, lo rende tale, cancellando l'account da Contributor
     * @param username il nome utente del Contributor che richiede l'autorizzazione
     * @return true se il Contributor ha raggiunto il numero minimo di contenuti caricati sulla piattaforma,
     * false altrimenti
     */
    public boolean richiediAutorizzazione(String username){
        if(!this.utenteRepository.existsById(username) || this.getGestore() == null) {
            return false;
        }
        Contributor contributor = (Contributor) this.utenteRepository.findById(username).get();
        List<Contenuto> contenutiCaricati = this.contenutoRepository.findContenutiByUsernameAutore(username)
                .stream().filter(Contenuto::isDefinitive).toList();
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

    /**
     * Verifica che ogni ContributorAutorizzato abbia caricato almeno un contenuto nell'ultima settimana.
     * I ContributorAutorizzato che non rispettano questa condizione subiscono una ammonizione. Di questi,
     * coloro che sono arrivati a 3 ammonizioni, compresa quella appena data, perdono l'autorizzazione e
     * tornano a essere Contributor
     */
    public void gestisciAutorizzazioni(){
        this.getContributorAutorizzati()
                .stream()
                .filter(c -> this.findContenutiRecenti(c.getUsername()))
                .forEach(c -> this.gestisciEsitiNegativi(c.getUsername()));
    }

    private boolean findContenutiRecenti(String username) {
        return this.contenutoRepository.findContenutiByUsernameAutore(username)
                .stream()
                .noneMatch(cont -> cont.getDataCreazione().until(LocalDateTime.now(),ChronoUnit.DAYS) <= 7);
    }

    public void gestisciEsitiNegativi(String username){
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
        } else {
            this.utenteRepository.saveAndFlush(daControllare);
        }
    }

    public void setContenutiMinimiAutorizzazione(String username, Integer contenutiMinimiAutorizzazione){
        if(this.utenteRepository.findById(username).get() instanceof
                GestoreDellaPiattaforma gestoreDellaPiattaforma){
            gestoreDellaPiattaforma.setContenutiMinimiPerAutorizzazione(contenutiMinimiAutorizzazione);
            this.utenteRepository.saveAndFlush(gestoreDellaPiattaforma);
        }
    }
}
