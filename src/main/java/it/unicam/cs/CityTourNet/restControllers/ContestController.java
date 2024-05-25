package it.unicam.cs.CityTourNet.restControllers;

import it.unicam.cs.CityTourNet.handlers.AuthHandler;
import it.unicam.cs.CityTourNet.handlers.ContenutiHandler;
import it.unicam.cs.CityTourNet.handlers.ContestHandler;
import it.unicam.cs.CityTourNet.handlers.UtentiHandler;
import it.unicam.cs.CityTourNet.model.contenuto.Itinerario;
import it.unicam.cs.CityTourNet.model.contenuto.POI;
import it.unicam.cs.CityTourNet.model.contest.ConcreteContest;
import it.unicam.cs.CityTourNet.model.utente.Animatore;
import it.unicam.cs.CityTourNet.model.utente.Utente;
import it.unicam.cs.CityTourNet.utils.FileUtils;
import it.unicam.cs.CityTourNet.utils.UtenteCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v0/contest")
public class ContestController extends FileUtils {

    private final ContestHandler contestHandler;

    private final ContenutiHandler contenutiHandler;

    private final AuthHandler authHandler;

    private final UtentiHandler utentiHandler;


    @Autowired
    public ContestController(ContestHandler contestHandler, ContenutiHandler contenutiHandler,
                             AuthHandler authHandler, UtentiHandler utentiHandler) {
        this.contestHandler = contestHandler;
        this.contenutiHandler = contenutiHandler;
        this.authHandler = authHandler;
        this.utentiHandler = utentiHandler;
    }

    @PostMapping(value = "/caricaPOI", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> caricaPOI(@RequestParam MultipartFile file,
                                            @RequestParam String nome,
                                            @RequestParam String descrizione,
                                            @RequestParam String usernameAutore) {
        if(!this.contestHandler.isAttivo()) {
            return new ResponseEntity<>("Contest non attivo", HttpStatus.BAD_REQUEST);
        }
        String path = super.controllaFile(file);
        if(path.equals("File non trovato") || path.equals("File non supportato")) {
            return new ResponseEntity<>(path, HttpStatus.BAD_REQUEST);
        }
        File newFile = new File(path);
        POI poi = new POI(nome, descrizione, usernameAutore);
        poi.setFilepath(path);
        try (OutputStream os = new FileOutputStream(newFile)) {
            os.write(file.getBytes());
            if(!this.contestHandler.caricaPOIPerContest(poi)) {
                return new ResponseEntity<>("se vuoi aggiungere questo contenuto al contest," +
                        " prima rimuovi quello gia' inserito",
                        HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("POI salvato", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Errore durante il salvataggio del file",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/fileDownload")
    public ResponseEntity<Object> fileDownload(@RequestParam String username, @RequestParam String filepath) {
        Utente utente = this.utentiHandler.getUtenteByUsername(username);
        if(!(utente instanceof Animatore)) {
            return new ResponseEntity<>("Non sei autorizzato", HttpStatus.BAD_REQUEST);
        }
        else return super.fileDownload(filepath);
    }

    @PostMapping("/caricaItinerario")
    public ResponseEntity<Object> caricaItinerario(@RequestBody Itinerario itinerario) {
        if(!this.contestHandler.isAttivo()) {
            return new ResponseEntity<>("Contest non attivo", HttpStatus.BAD_REQUEST);
        }
        List<POI> POIsDaInserire = itinerario.getIndiciPOIs().stream()
                .map(this.contenutiHandler::getPOIByID).filter(Objects::nonNull).toList();
        if(POIsDaInserire.size() == itinerario.getIndiciPOIs().size()) {
            if(!this.contestHandler.caricaItinerarioPerContest(itinerario)) {
                return new ResponseEntity<>("se vuoi aggiungere questo contenuto al contest" +
                        " prima rimuovi quello gia' inserito",
                        HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Itinerario salvato", HttpStatus.OK);
        }
        return new ResponseEntity<>("Hai fatto riferimento a POI inesistenti", HttpStatus.BAD_REQUEST);
    }


    @PostMapping("/crea")
    public ResponseEntity<Object> creaContest(@RequestBody ConcreteContest contest) {
        Utente utente = this.utentiHandler.getUtenteByUsername(contest.getUsernameAutore());
        if(utente instanceof Animatore) {
            if(!this.contestHandler.creaContest(contest)) {
                return new ResponseEntity<>("C'e' gia' un contest attivo " +
                        "oppure non hai ancora selezionato le opzioni", HttpStatus.BAD_REQUEST);
            }
            this.authHandler.setContestAttivo(true);
            return new ResponseEntity<>("Contest creato", HttpStatus.OK);
        }
        return new ResponseEntity<>("Non sei autorizzato", HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("/selezionaOpzionePOI")
    public ResponseEntity<Object> selezionaOpzionePOI(@RequestParam String username) {
        Utente utente = this.utentiHandler.getUtenteByUsername(username);
        if(!(utente instanceof Animatore)) {
            return new ResponseEntity<>("Non sei autorizzato", HttpStatus.BAD_REQUEST);
        }
        if(!this.contestHandler.isAttivo()) {
            this.contestHandler.selezionaOpzionePOI();
            return new ResponseEntity<>("Opzione POI attivata, " +
                    "opzione Itinerario disattivata", HttpStatus.OK);
        }
        return new ResponseEntity<>("Contest gia' attivo", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/selezionaOpzioneItinerario")
    public ResponseEntity<Object> selezionaOpzioneItinerario(@RequestParam String username) {
        Utente utente = this.utentiHandler.getUtenteByUsername(username);
        if(!(utente instanceof Animatore)) {
            return new ResponseEntity<>("Non sei autorizzato", HttpStatus.BAD_REQUEST);
        }
        if(!this.contestHandler.isAttivo()) {
            this.contestHandler.selezionaOpzioneItinerario();
            return new ResponseEntity<>("Opzione Itinerario attivata, " +
                    "opzione POI disattivata", HttpStatus.OK);
        }
        return new ResponseEntity<>("Contest gia' attivo", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/selezionaOpzioneTuristiAutenticati")
    public ResponseEntity<Object> selezionaOpzioneTuristiAutenticati(@RequestParam String username) {
        Utente utente = this.utentiHandler.getUtenteByUsername(username);
        if(!(utente instanceof Animatore)) {
            return new ResponseEntity<>("Non sei autorizzato", HttpStatus.BAD_REQUEST);
        }
        if(!this.contestHandler.isAttivo()) {
            if(this.contestHandler.selezionaOpzioneTuristiAutenticati()) {
                return new ResponseEntity<>("Opzione turisti autenticati attivata", HttpStatus.OK);
            }
            return new ResponseEntity<>("Opzione turisti autenticati disattivata", HttpStatus.OK);
        }
        return new ResponseEntity<>("Contest gia' attivo", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/selezionaOpzioneContributors")
    public ResponseEntity<Object> selezionaOpzioneContributors(@RequestParam String username) {
        Utente utente = this.utentiHandler.getUtenteByUsername(username);
        if(!(utente instanceof Animatore)) {
            return new ResponseEntity<>("Non sei autorizzato", HttpStatus.BAD_REQUEST);
        }
        if(!this.contestHandler.isAttivo()) {
            if(this.contestHandler.selezionaOpzioneContributors()) {
                return new ResponseEntity<>("Opzione contributors attivata", HttpStatus.OK);
            }
            return new ResponseEntity<>("Opzione contributors disattivata", HttpStatus.OK);
        }
        return new ResponseEntity<>("Contest gia' attivo", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/POIsContest")
    public ResponseEntity<Object> visualizzaPOIsContest(@RequestParam String username) {
        Utente utente = this.utentiHandler.getUtenteByUsername(username);
        if(!(utente instanceof Animatore)) {
            return new ResponseEntity<>("Non sei autorizzato", HttpStatus.BAD_REQUEST);
        }
        if(this.contestHandler.isAttivo()) {
            return new ResponseEntity<>(this.contestHandler.getPOIsPartecipanti(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Contest non attivo", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/itinerariContest")
    public ResponseEntity<Object> visualizzaItinerariContest(@RequestParam String username) {
        Utente utente = this.utentiHandler.getUtenteByUsername(username);
        if(!(utente instanceof Animatore)) {
            return new ResponseEntity<>("Non sei autorizzato", HttpStatus.BAD_REQUEST);
        }
        if(this.contestHandler.isAttivo()) {
            return new ResponseEntity<>(this.contestHandler.getItinerariPartecipanti(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Contest non attivo", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/aggiungiPartecipanti")
    public ResponseEntity<Object> aggiungiPartecipanti(@RequestBody UtenteCredentials credentials) {
        Utente utente = this.utentiHandler.getUtenteByUsername(credentials.username());
        if(utente instanceof Animatore && utente.getPassword().equals(credentials.password())) {
            if (this.contestHandler.isAttivo()) {
                if(this.contestHandler.addPartecipanti()) {
                    return new ResponseEntity<>("Partecipanti aggiunti", HttpStatus.OK);
                }
                return new ResponseEntity<>("Numero partecipanti insufficiente. Contest terminato",
                        HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Contest non attivo", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Username o password errati", HttpStatus.BAD_REQUEST);
    }


    @PostMapping("/premiaVincitore")
    public ResponseEntity<Object> premiaVincitore(@RequestParam String username,
                                                  @RequestParam String password,
                                                  @RequestParam String usernameVincitore) {
        Utente animatore = this.utentiHandler.getUtenteByUsername(username);
        Utente vincitore = this.utentiHandler.getUtenteByUsername(usernameVincitore);
        if(animatore instanceof Animatore && animatore.getPassword().equals(password)) {
            if (this.contestHandler.isAttivo()) {
                if(this.contestHandler.premiaVincitore(username,vincitore)) {
                    this.authHandler.setContestAttivo(false);
                    return new ResponseEntity<>("Il vincitore e' : " + usernameVincitore +
                            "\nIl contest e' terminato", HttpStatus.OK);
                }
                return new ResponseEntity<>("L'utente scelto non e' presente " +
                        "o non ha caricato alcun contenuto per il contest", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Contest non attivo", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Username o password errati", HttpStatus.BAD_REQUEST);
    }

}
