package it.unicam.cs.CityTourNet.restControllers;

import it.unicam.cs.CityTourNet.handlers.CompraVenditaHandler;
import it.unicam.cs.CityTourNet.handlers.UtentiHandler;
import it.unicam.cs.CityTourNet.model.contenuto.ProdottoGadget;
import it.unicam.cs.CityTourNet.model.utente.Contributor;
import it.unicam.cs.CityTourNet.model.utente.Utente;
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

@RestController
@RequestMapping("/api/v0/compravendita")
public class CompraVenditaController {

    private final CompraVenditaHandler compraVenditaHandler;

    private final UtentiHandler utentiHandler;

    @Value("${photosResources.path}")
    private String photosPath;

    @Value("${videosResources.path}")
    private String videosPath;

    @Autowired
    public CompraVenditaController(CompraVenditaHandler compraVenditaHandler, UtentiHandler utentiHandler) {
        this.compraVenditaHandler = compraVenditaHandler;
        this.utentiHandler = utentiHandler;
    }

    @GetMapping("/prodotto")
    public ResponseEntity<Object> getProdottoGadget(@RequestParam long id) {
        return new ResponseEntity<>(this.compraVenditaHandler.getProdottoGadget(id), HttpStatus.OK);
    }

    @GetMapping("/prodotti")
    public ResponseEntity<Object> getProdottiGadget() {
        return new ResponseEntity<>(this.compraVenditaHandler.getProdottiGadget(), HttpStatus.OK);
    }

    @PutMapping("/acquista")
    public ResponseEntity<Object> acquistaProdotto(@RequestParam long id,
                                                   @RequestParam int numPezzi,
                                                   @RequestParam String username,
                                                   @RequestParam String indirizzo){
        if(this.compraVenditaHandler.gestisciAcquistoProdottoGadget(id, numPezzi, username, indirizzo)) {
            return new ResponseEntity<>("L'acquisto e' stato effettuato con successo", HttpStatus.OK);
        } else {
           return new ResponseEntity<>("L'acquisto non e' stato effettuato con successo",
                   HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/punti")
    public ResponseEntity<Object> getPuntiPersonali(@RequestParam String username) {
        return new ResponseEntity<>(this.compraVenditaHandler.getPuntiUtente(username), HttpStatus.OK);
    }

    @PostMapping(value = "/vendi", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> vendiProdottoGadget(@RequestParam MultipartFile file,
                                                      @RequestParam String nome,
                                                      @RequestParam String descrizione,
                                                      @RequestParam String usernameAutore,
                                                      @RequestParam int prezzo,
                                                      @RequestParam int numPezzi){
        ProdottoGadget daVendere = new ProdottoGadget(nome,descrizione,usernameAutore,prezzo,numPezzi);
        if (file.isEmpty()) {
            return new ResponseEntity<>("File non fornito", HttpStatus.BAD_REQUEST);
        }
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        if(this.getFilePath(extension) == null) {
            return new ResponseEntity<>("File non supportato", HttpStatus.BAD_REQUEST);
        }
        String path = this.getFilePath(extension) + originalFilename;
        daVendere.setFilepath(path);
        File newFile = new File(path);
        Utente utente = this.utentiHandler.getUtenteByUsername(daVendere.getUsernameAutore());
        try (OutputStream os = new FileOutputStream(newFile)) {
            os.write(file.getBytes());
            if(utente instanceof Contributor){
                this.compraVenditaHandler.addProdottoGadget(daVendere);
                return new ResponseEntity<>("Prodotto aggiunto con successo", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Non sei autorizzato", HttpStatus.UNAUTHORIZED);
            }
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

    @DeleteMapping("/elimina")
    public ResponseEntity<Object> eliminaProdottoGadget(@RequestParam String username,
                                                        @RequestParam String password,
                                                        @RequestParam long id){
        Utente utente = this.utentiHandler.getUtenteByUsername(username);
        if(utente == null || !utente.isLoggedIn()) {
            return new ResponseEntity<>("Non sei loggato", HttpStatus.BAD_REQUEST);
        }
        if(utente.getPassword().equals(password)) {
            this.compraVenditaHandler.removeProdottoGadget(id);
            return new ResponseEntity<>("Prodotto eliminato con successo", HttpStatus.OK);
        }
        return new ResponseEntity<>("Non sei autorizzato", HttpStatus.UNAUTHORIZED);
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

    @PutMapping("/eseguiModifiche")
    public ResponseEntity<Object> eseguiModifiche(@RequestParam String username,
                                                  @RequestParam String password,
                                                  @RequestParam long ID,
                                                  @RequestParam int prezzo,
                                                  @RequestParam int numPezzi) {
        Utente utente = this.utentiHandler.getUtenteByUsername(username);
        if(utente != null && utente.getPassword().equals(password)) {
            if(this.compraVenditaHandler.eseguiModifiche(prezzo, numPezzi, ID)) {
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
            if(this.compraVenditaHandler.annullaModifiche(ID)) {
                return new ResponseEntity<>("Modifiche annullate", HttpStatus.OK);
            }
            return new ResponseEntity<>("Il contenuto e' gia' alla sua prima versione", HttpStatus.OK);
        }
        return new ResponseEntity<>("Non sei autorizzato", HttpStatus.UNAUTHORIZED);
    }
}
