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
import it.unicam.cs.CityTourNet.utils.UtenteCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
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
public class ContestController {

    private final ContestHandler contestHandler;

    private final ContenutiHandler contenutiHandler;

    private final AuthHandler authHandler;

    private final UtentiHandler utentiHandler;

    @Value("${photosResources.path}")
    private String photosPath;

    @Value("${videosResources.path}")
    private String videosPath;

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
        POI poi = new POI(nome, descrizione, usernameAutore);
        if (file.isEmpty()) {
            return new ResponseEntity<>("File non fornito", HttpStatus.BAD_REQUEST);
        }
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        if(this.getFilePath(extension) == null) {
            return new ResponseEntity<>("File non supportato", HttpStatus.BAD_REQUEST);
        }
        String path = this.getFilePath(extension) + originalFilename;
        poi.setFilepath(path);
        File newFile = new File(path);
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

    private String getFilePath(String extension) {
        switch (extension) {
            case ".jpg", ".jpeg", ".png", ".gif" -> {
                return this.photosPath;
            }
            case ".mp4" -> {
                return this.videosPath;
            }
            default -> {
                return null;
            }
        }
    }

    @GetMapping("/fileDownload")
    public ResponseEntity<Object> fileDownload(@RequestParam String filepath) {
        File file = new File(filepath);
        String extension = file.getName().substring(file.getName().lastIndexOf('.'));
        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            HttpHeaders header = new HttpHeaders();
            header.add("Content-disposition",String.format("attachment; filename=\"%s\"",
                    file.getName()));
            header.add("Cache-control","no-cache, no-store, must-revalidate");
            header.add("Pragma", "no-cache");
            header.add("Expires","0");
            return ResponseEntity.ok().headers(header).contentLength(file.length())
                    .contentType(MediaType.parseMediaType(this.getMediaType(extension))).body(resource);
        } catch (FileNotFoundException e) {
            return new ResponseEntity<>("File non trovato",HttpStatus.NOT_FOUND);
        }
    }

    private String getMediaType(String extension) {
        return switch (extension) {
            case ".jpeg", ".jpg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".gif" -> "image/gif";
            case ".mp4" -> "video/mp4";
            default -> "";
        };
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

    @DeleteMapping("/eliminaContenutoContest")
    public ResponseEntity<Object> eliminaContenutoContest(@RequestBody UtenteCredentials credentials) {
        if(!this.contestHandler.isAttivo()) {
            return new ResponseEntity<>("Contest non attivo", HttpStatus.BAD_REQUEST);
        }
        Utente utente = this.utentiHandler.getUtenteByUsername(credentials.username());
        if(utente != null && utente.getPassword().equals(credentials.password())) {
            this.contestHandler.eliminaContenutoPerContest(credentials.username());
            return new ResponseEntity<>("Contenuto di: " + credentials.username()
                    + " eliminato", HttpStatus.OK);
        }
        return new ResponseEntity<>("Username o password errati", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/crea")
    public ResponseEntity<Object> creaContest(@RequestParam String dataFine,
                                              @RequestParam String tematica,
                                              @RequestParam String usernameAutore) {
        Utente utente = this.utentiHandler.getUtenteByUsername(usernameAutore);
        if(utente != null && utente.getClass().getSimpleName().equals("Animatore")) {
            ConcreteContest contest = new ConcreteContest(dataFine,tematica,usernameAutore);
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
                return new ResponseEntity<>("Opzione turisti autenticati attivata", HttpStatus.OK);
            }
            return new ResponseEntity<>("Opzione turisti autenticati disattivata", HttpStatus.OK);
        }
        return new ResponseEntity<>("Contest gia' attivo", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/selezionaOpzioneContributors")
    public ResponseEntity<Object> selezionaOpzioneContributors() {
        if(!this.contestHandler.isAttivo()) {
            boolean result = this.contestHandler.selezionaOpzioneContributors();
            if(result) {
                return new ResponseEntity<>("Opzione contributors attivata", HttpStatus.OK);
            }
            return new ResponseEntity<>("Opzione contributors disattivata", HttpStatus.OK);
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
    public ResponseEntity<Object> aggiungiPartecipanti(@RequestBody UtenteCredentials credentials) {
        Utente utente = this.utentiHandler.getUtenteByUsername(credentials.username());
        if(utente != null && utente.getPassword().equals(credentials.password()) && utente instanceof Animatore) {
            if (this.contestHandler.isAttivo()) {
                if(this.contestHandler.addPartecipanti(credentials.username())) {
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
        if(animatore != null && animatore.getPassword().equals(password) && animatore instanceof Animatore) {
            if (this.contestHandler.isAttivo()) {
                this.contestHandler.premiaVincitore(username,vincitore);
                this.authHandler.setContestAttivo(false);
                return new ResponseEntity<>("Il vincitore e' : " + usernameVincitore +
                        "\nIl contest e' terminato", HttpStatus.OK);
            }
            return new ResponseEntity<>("Contest non attivo", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Username o password errati", HttpStatus.BAD_REQUEST);
    }

}
