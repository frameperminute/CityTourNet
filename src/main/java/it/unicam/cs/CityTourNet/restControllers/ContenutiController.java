package it.unicam.cs.CityTourNet.restControllers;

import it.unicam.cs.CityTourNet.handlers.ContenutiHandler;
import it.unicam.cs.CityTourNet.model.contenuto.Difficolta;
import it.unicam.cs.CityTourNet.model.contenuto.Itinerario;
import it.unicam.cs.CityTourNet.model.contenuto.POI;
import it.unicam.cs.CityTourNet.model.utente.ContributorAutorizzato;
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
import java.util.List;

@RestController
@RequestMapping("/api/v0/contenuti")
public class ContenutiController {
    private final ContenutiHandler contenutiHandler;

    @Value("${photosResources.path}")
    private String photosPath;

    @Value("${videosResources.path}")
    private String videosPath;

    @Autowired
    public ContenutiController(ContenutiHandler contenutiHandler) {
        this.contenutiHandler = contenutiHandler;
    }

    @GetMapping("/pending")
    public ResponseEntity<Object> visualizzaContenutiInPending(){
        return new ResponseEntity<>(this.contenutiHandler.getContenutiInPending(), HttpStatus.OK);
    }

    @GetMapping("/POIs")
    public ResponseEntity<Object> visualizzaPOI(){
        return new ResponseEntity<>(this.contenutiHandler.getPOIS(), HttpStatus.OK);
    }

    @GetMapping("/itinerari")
    public ResponseEntity<Object> visualizzaItinerari(){
        return new ResponseEntity<>(this.contenutiHandler.getItinerari(), HttpStatus.OK);
    }

    @GetMapping("/contenutiAutore")
    public ResponseEntity<Object> visualizzaContenutiByAutore(@RequestBody String usernameAutore){
        return new ResponseEntity<>(this.contenutiHandler.getContenutiByAutore(usernameAutore), HttpStatus.OK);
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
            if(this.contenutiHandler.getUtenteByUsername(usernameAutore) instanceof ContributorAutorizzato) {
                this.contenutiHandler.addPOI(new POI(nome, descrizione, usernameAutore, newFile));
            } else {
                this.contenutiHandler.addPOIInPending(new POI(nome, descrizione, usernameAutore, newFile));
            }
            return new ResponseEntity<>("POI salvato", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Errore durante il salvataggio del file", HttpStatus.INTERNAL_SERVER_ERROR);
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
            if(this.contenutiHandler.getUtenteByUsername(usernameAutore) instanceof ContributorAutorizzato) {
                this.contenutiHandler.addItinerario(new Itinerario(nome, descrizione, usernameAutore,
                        POIsDaInserire, difficolta, ore, minuti));
            } else {
                this.contenutiHandler.addItinerarioInPending(new Itinerario(nome, descrizione, usernameAutore,
                        POIsDaInserire, difficolta, ore, minuti));
            }
            return new ResponseEntity<>("Itinerario salvato", HttpStatus.OK);
        }
        return new ResponseEntity<>("Hai fatto riferimento a POI inesistenti", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/cancellaContenuto")
    public ResponseEntity<Object> deleteContenuto(@RequestBody long ID) {
        this.contenutiHandler.removeContenuto(ID);
        this.contenutiHandler.removeContenuto(ID);
        return new ResponseEntity<>("Contenuto eliminato", HttpStatus.OK);
    }
}
