package it.unicam.cs.CityTourNet.restControllers;

import it.unicam.cs.CityTourNet.handlers.ContenutiHandler;
import it.unicam.cs.CityTourNet.handlers.ContestHandler;
import it.unicam.cs.CityTourNet.model.contenuto.Difficolta;
import it.unicam.cs.CityTourNet.model.contenuto.Itinerario;
import it.unicam.cs.CityTourNet.model.contenuto.POI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v0/contest")
public class ContestController {
    private final ContestHandler contestHandler;
    private final ContenutiHandler contenutiHandler;

    @Value("${photosResources.path}")
    private String photosPath;

    @Value("${videosResources.path}")
    private String videosPath;

    @Autowired
    public ContestController(ContestHandler contestHandler, ContenutiHandler contenutiHandler) {
        this.contestHandler = contestHandler;
        this.contenutiHandler = contenutiHandler;
    }

    @PostMapping(value = "/caricaPOI", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> caricaPOI(@RequestBody String nome, @RequestBody String descrizione,
                                            @RequestBody String usernameAutore,
                                            @RequestBody MultipartFile multimedia) {
        if (multimedia.isEmpty()) {
            return new ResponseEntity<>("File non fornito", HttpStatus.BAD_REQUEST);
        }
        String originalFilename = multimedia.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        if(this.getFilePath(extension) == null) {
            return new ResponseEntity<>("File non supportato", HttpStatus.BAD_REQUEST);
        }
        String path = this.getFilePath(extension) + originalFilename;
        File newFile = new File(path);
        try (OutputStream os = new FileOutputStream(newFile)) {
            os.write(multimedia.getBytes());
            if(!this.contestHandler.caricaPOIPerContest(new POI(nome, descrizione, usernameAutore, newFile))) {
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

    private String getFilePath(String extension) {
        if(extension.equals("jpg") || extension.equals("jpeg")
                || extension.equals("png") || extension.equals("gif")) {
            return this.photosPath;
        } else if(extension.equals("mp4")) {
            return this.videosPath;
        }
        return null;
    }

    @PostMapping("/caricaItinerario")
    public ResponseEntity<Object> caricaItinerario(@RequestBody String nome, @RequestBody String descrizione,
                                                   @RequestBody String usernameAutore,
                                                   @RequestBody List<Long> POIsPerItinerario,
                                                   @RequestBody Difficolta difficolta, @RequestBody int ore,
                                                   @RequestBody int minuti) {
        List<POI> POIsDaInserire = POIsPerItinerario.stream().map(this.contenutiHandler::getPOIByID).toList();
        if(POIsDaInserire.size() == POIsPerItinerario.size()) {
            Itinerario daAggiungere = new Itinerario(nome, descrizione, usernameAutore,
                    POIsDaInserire, difficolta, ore, minuti);
            if(!this.contestHandler.caricaItinerarioPerContest(daAggiungere)) {
                return new ResponseEntity<>("se vuoi aggiungere questo contenuto al contest," +
                        " prima rimuovi quello gia' inserito",
                        HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Itinerario salvato", HttpStatus.OK);
        }
        return new ResponseEntity<>("Hai fatto riferimento a POI inesistenti", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/eliminaContenutoContest")
    public ResponseEntity<Object> eliminaContenutoContest(@RequestBody String username,
                                                           @RequestBody String password) {
        if(this.contenutiHandler.getUtenteByUsername(username).getPassword().equals(password)) {
            this.contestHandler.eliminaContenutoPerContest(username);
            return new ResponseEntity<>("Contenuto di: " + username + " eliminato", HttpStatus.OK);
        }
        return new ResponseEntity<>("Username o password errati", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/creaContest")
    public ResponseEntity<Object> creaContest(@RequestBody LocalDateTime dataFine, @RequestBody String tematica,
                                              @RequestBody String username, @RequestBody String password) {
        if(this.contenutiHandler.getUtenteByUsername(username).getPassword().equals(password)) {
            if(!this.contestHandler.creaContest(dataFine,tematica,username)) {
                return new ResponseEntity<>("C'e' gia' un contest attivo", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Contest creato", HttpStatus.OK);
        }
        return new ResponseEntity<>("Username o password errati", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/selezionaOpzionePOI")
    public ResponseEntity<Object> selezionaOpzionePOI() {
        if(!this.contestHandler.isAttivo()) {
            this.contestHandler.selezionaOpzionePOI();
            return new ResponseEntity<>("Opzione POI attivata, " +
                    "opzione Itinerario disattivata", HttpStatus.OK);
        }
        return new ResponseEntity<>("Contest gia' attivo", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/selezionaOpzioneItinerario")
    public ResponseEntity<Object> selezionaOpzioneItinerario() {
        if(!this.contestHandler.isAttivo()) {
            this.contestHandler.selezionaOpzioneItinerario();
            return new ResponseEntity<>("Opzione Itinerario attivata, " +
                    "opzione POI disattivata", HttpStatus.OK);
        }
        return new ResponseEntity<>("Contest gia' attivo", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/selezionaOpzioneTuristi")
    public ResponseEntity<Object> selezionaOpzioneTuristi() {
        if(!this.contestHandler.isAttivo()) {
            boolean result = this.contestHandler.selezionaOpzioneTuristi();
            if(result) {
                return new ResponseEntity<>("Opzione turisti attivata", HttpStatus.OK);
            }
            return new ResponseEntity<>("Opzione turisti disattivata", HttpStatus.OK);
        }
        return new ResponseEntity<>("Contest gia' attivo", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/selezionaOpzioneTuristiAutenticati")
    public ResponseEntity<Object> selezionaOpzioneTuristiAutenticati() {
        if(!this.contestHandler.isAttivo()) {
            boolean result = this.contestHandler.selezionaOpzioneTuristiAutenticati();
            if(result) {
                return new ResponseEntity<>("Opzione turisti attivata", HttpStatus.OK);
            }
            return new ResponseEntity<>("Opzione turisti disattivata", HttpStatus.OK);
        }
        return new ResponseEntity<>("Contest gia' attivo", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/selezionaOpzioneContributors")
    public ResponseEntity<Object> selezionaOpzioneContributors() {
        if(!this.contestHandler.isAttivo()) {
            boolean result = this.contestHandler.selezionaOpzioneContributors();
            if(result) {
                return new ResponseEntity<>("Opzione turisti attivata", HttpStatus.OK);
            }
            return new ResponseEntity<>("Opzione turisti disattivata", HttpStatus.OK);
        }
        return new ResponseEntity<>("Contest gia' attivo", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/POIsContest")
    public ResponseEntity<Object> visualizzaPOIsContest() {
        if(this.contestHandler.isAttivo()) {
            return new ResponseEntity<>(this.contestHandler.getPOIsPartecipanti(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Contest non attivo", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/itinerariContest")
    public ResponseEntity<Object> visualizzaItinerariContest() {
        if(this.contestHandler.isAttivo()) {
            return new ResponseEntity<>(this.contestHandler.getItinerariPartecipanti(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Contest non attivo", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/aggiungiPartecipanti")
    public ResponseEntity<Object> aggiungiPartecipanti(@RequestBody String username,
                                                       @RequestBody String password) {
        if(this.contenutiHandler.getUtenteByUsername(username).getPassword().equals(password)) {
            if (this.contestHandler.isAttivo()) {
                if(this.contestHandler.addPartecipanti(username)) {
                    return new ResponseEntity<>("Partecipanti aggiunti", HttpStatus.OK);
                }
                return new ResponseEntity<>("Numero partecipanti insufficiente. Contest terminato",
                        HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Contest non attivo", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Username o password errati", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/inviaUtenteVincitore")
    public ResponseEntity<Object> inviaUtenteVincitore(@RequestBody String usernameAnimatore,
                                                       @RequestBody String passwordAnimatore,
                                                       @RequestBody String usernameVincitore,
                                                       @RequestBody String usernameGestore) {
        if(this.contenutiHandler.getUtenteByUsername(usernameAnimatore).getPassword().equals(passwordAnimatore)) {
            if (this.contestHandler.isAttivo()) {
                this.contestHandler.inviaUtenteVincitore(usernameVincitore, usernameAnimatore, usernameGestore);
                return new ResponseEntity<>("L'utente vincitore: " + usernameVincitore +
                        "ha ricevuto la notifica", HttpStatus.OK);
            }
            return new ResponseEntity<>("Contest non attivo", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Username o password errati", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/premiaVincitore")
    public ResponseEntity<Object> premiaVincitore(@RequestBody String usernameAnimatore,
                                                       @RequestBody String passwordAnimatore,
                                                       @RequestBody String usernameVincitore) {
        if(this.contenutiHandler.getUtenteByUsername(usernameAnimatore).getPassword().equals(passwordAnimatore)) {
            if (this.contestHandler.isAttivo()) {
                this.contestHandler.premiaVincitore(usernameAnimatore);
                return new ResponseEntity<>("L'utente vincitore: " + usernameVincitore +
                        "e' stato premiato. Il contest e' terminato", HttpStatus.OK);
            }
            return new ResponseEntity<>("Contest non attivo", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Username o password errati", HttpStatus.BAD_REQUEST);
    }



}
