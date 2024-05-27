package it.unicam.cs.CityTourNet.restControllers;

import it.unicam.cs.CityTourNet.handlers.ContenutiHandler;
import it.unicam.cs.CityTourNet.handlers.UtentiHandler;
import it.unicam.cs.CityTourNet.model.contenuto.Itinerario;
import it.unicam.cs.CityTourNet.model.contenuto.POI;
import it.unicam.cs.CityTourNet.model.utente.Contributor;
import it.unicam.cs.CityTourNet.model.utente.ContributorAutorizzato;
import it.unicam.cs.CityTourNet.model.utente.Curatore;
import it.unicam.cs.CityTourNet.model.utente.Utente;
import it.unicam.cs.CityTourNet.utils.FileUtils;
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
@RequestMapping("/api/v0/contenuti")
public class ContenutiController extends FileUtils {

    private final ContenutiHandler contenutiHandler;
    
    private final UtentiHandler utentiHandler;

    @Autowired
    public ContenutiController(ContenutiHandler contenutiHandler, UtentiHandler utentiHandler) {
        this.contenutiHandler = contenutiHandler;
        this.utentiHandler = utentiHandler;
    }

    @GetMapping("/POIsPending")
    public ResponseEntity<Object> visualizzaPOIsInPending(@RequestParam String username){
        if(this.utentiHandler.getUtenteByUsername(username) instanceof Curatore) {
            return new ResponseEntity<>(this.contenutiHandler.getPOIsInPending(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Non sei autorizzato", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/itinerariPending")
    public ResponseEntity<Object> visualizzaItinerariInPending(@RequestParam String username){
        if(this.utentiHandler.getUtenteByUsername(username) instanceof Curatore) {
            return new ResponseEntity<>(this.contenutiHandler.getItinerariInPending(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Non sei autorizzato", HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("/caricaDefinitivamente")
    public ResponseEntity<Object> caricaDefinitivamente(@RequestParam String username,
                                                        @RequestParam long id){
        if(this.utentiHandler.getUtenteByUsername(username) instanceof Curatore) {
            if(this.contenutiHandler.caricaDefinitivamente(id)) {
                return new ResponseEntity<>("Trasferimento effettuato",HttpStatus.OK);
            }
            return new ResponseEntity<>("Contenuto con ID: " + id + "non presente",
                    HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>("Non sei autorizzato", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/POIs")
    public ResponseEntity<Object> visualizzaPOI(@RequestParam(required = false) String nome){
        return new ResponseEntity<>(this.contenutiHandler.getPOIS(nome), HttpStatus.OK);
    }

    @GetMapping("/itinerari")
    public ResponseEntity<Object> visualizzaItinerari(@RequestParam(required = false) String nome,
                                                      @RequestParam(required = false) Integer oreMax,
                                                      @RequestParam(required = false) String difficolta){
        return new ResponseEntity<>(this.contenutiHandler.getItinerari(nome ,oreMax,difficolta), HttpStatus.OK);
    }

    @GetMapping("/contenutiAutore")
    public ResponseEntity<Object> visualizzaContenutiByAutore(@RequestParam String usernameAutore){
        return new ResponseEntity<>(this.contenutiHandler.getContenutiByAutore(usernameAutore), HttpStatus.OK);
    }

    @GetMapping("/contenutiInPendingAutore")
    public ResponseEntity<Object> visualizzaContenutiInPendingByAutore(@RequestParam String usernameAutore){
        return new ResponseEntity<>(this.contenutiHandler
                .getContenutiInPendingByAutore(usernameAutore), HttpStatus.OK);
    }

    @PostMapping(value = "/caricaPOI", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> caricaPOI(@RequestParam MultipartFile file,
                                            @RequestParam String nome,
                                            @RequestParam String descrizione,
                                            @RequestParam String usernameAutore) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("File non fornito", HttpStatus.BAD_REQUEST);
        }
        Utente utente = this.utentiHandler.getUtenteByUsername(usernameAutore);
        if(utente == null || !utente.isLoggedIn()) {
            return new ResponseEntity<>("Non sei loggato", HttpStatus.UNAUTHORIZED);
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
            return this.scegliCaricamentoPOI(poi, utente);
        } catch (IOException e) {
            return new ResponseEntity<>("Errore durante il salvataggio del file",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<Object> scegliCaricamentoPOI(POI poi, Utente utente) {
        if(utente instanceof ContributorAutorizzato) {
            this.contenutiHandler.addPOI(poi);
            return new ResponseEntity<>("POI salvato", HttpStatus.OK);
        } else if(utente instanceof Contributor){
            this.contenutiHandler.addPOIInPending(poi);
            return new ResponseEntity<>("POI salvato in pending", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Non sei autorizzato", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/caricaItinerario")
    public ResponseEntity<Object> caricaItinerario(@RequestBody Itinerario itinerario) {
        List<POI> POIsDaInserire = itinerario.getIndiciPOIs().stream()
                .map(this.contenutiHandler::getPOIByID).filter(Objects::nonNull).toList();
        if(POIsDaInserire.size() == itinerario.getIndiciPOIs().size()) {
            Utente utente = this.utentiHandler.getUtenteByUsername(itinerario.getUsernameAutore());
            if(utente == null || !utente.isLoggedIn()) {
                return new ResponseEntity<>("Non sei loggato", HttpStatus.UNAUTHORIZED);
            }
            if(utente instanceof ContributorAutorizzato) {
                this.contenutiHandler.addItinerario(itinerario);
                return new ResponseEntity<>("Itinerario salvato", HttpStatus.OK);
            } else if(utente instanceof Contributor){
                this.contenutiHandler.addItinerarioInPending(itinerario);
                return new ResponseEntity<>("Itinerario salvato", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Non sei autorizzato", HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<>("Hai fatto riferimento a POI inesistenti", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/cancellaContenuto")
    public ResponseEntity<Object> cancellaContenuto(@RequestParam String username,
                                                    @RequestParam String password,
                                                    @RequestParam long ID) {
        Utente utente = this.utentiHandler.getUtenteByUsername(username);
        if(utente != null && utente.getPassword().equals(password)) {
            if(this.contenutiHandler.removeContenuto(ID)) {
                return new ResponseEntity<>("Contenuto eliminato", HttpStatus.OK);
            }
            return new ResponseEntity<>("Contenuto inesistente oppure Il POI e' utilizzato " +
                    "in uno o piu' itinerari e non puo' essere eliminato", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Non sei autorizzato", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/fileDownload")
    public ResponseEntity<Object> fileDownload(@RequestParam String filepath) {
        return super.fileDownload(filepath);
    }

    @PutMapping("/eseguiModifiche")
    public ResponseEntity<Object> eseguiModifiche(@RequestParam String username,
                                                  @RequestParam String password,
                                                  @RequestParam long ID,
                                                  @RequestParam String nome,
                                                  @RequestParam String descrizione) {
        Utente utente = this.utentiHandler.getUtenteByUsername(username);
        if(utente != null && utente.getPassword().equals(password)) {
            if(this.contenutiHandler.eseguiModifiche(nome, descrizione, ID)) {
                return new ResponseEntity<>("Modifiche eseguite", HttpStatus.OK);
            }
            return new ResponseEntity<>("Il contenuto non esiste", HttpStatus.OK);
        }
        return new ResponseEntity<>("Non sei autorizzato", HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("/annullaModifiche")
    public ResponseEntity<Object> annullaModifiche(@RequestParam String username,
                                                   @RequestParam String password,
                                                   @RequestParam long ID) {
        Utente utente = this.utentiHandler.getUtenteByUsername(username);
        if(utente != null && utente.getPassword().equals(password)) {
            if(this.contenutiHandler.annullaModifiche(ID)) {
                return new ResponseEntity<>("Modifiche annullate", HttpStatus.OK);
            }
            return new ResponseEntity<>("Il contenuto e' gia' alla sua prima versione " +
                    "oppure non sono piu' presenti alcuni dei POI a cui l'Itinerario si riferisce",
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Non sei autorizzato", HttpStatus.UNAUTHORIZED);
    }
}
