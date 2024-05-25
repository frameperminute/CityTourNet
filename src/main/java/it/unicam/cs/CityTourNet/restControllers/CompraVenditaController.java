package it.unicam.cs.CityTourNet.restControllers;

import it.unicam.cs.CityTourNet.handlers.CompraVenditaHandler;
import it.unicam.cs.CityTourNet.handlers.UtentiHandler;
import it.unicam.cs.CityTourNet.model.contenuto.ProdottoGadget;
import it.unicam.cs.CityTourNet.model.utente.ContributorAutorizzato;
import it.unicam.cs.CityTourNet.model.utente.Utente;
import it.unicam.cs.CityTourNet.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
@RequestMapping("/api/v0/compravendita")
public class CompraVenditaController extends FileUtils {

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
    public ResponseEntity<Object> getProdottiGadget(@RequestParam(required = false) String nome,
                                                    @RequestParam(required = false) Integer prezzoMax) {
        return new ResponseEntity<>(this.compraVenditaHandler.getProdottiGadget(nome, prezzoMax), HttpStatus.OK);
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
        Utente utente = this.utentiHandler.getUtenteByUsername(usernameAutore);
        if(!(utente instanceof ContributorAutorizzato)){
            return new ResponseEntity<>("Non sei autorizzato", HttpStatus.UNAUTHORIZED);
        }
        String path = super.controllaFile(file);
        if(path.equals("File non trovato") || path.equals("File non supportato")) {
            return new ResponseEntity<>(path, HttpStatus.BAD_REQUEST);
        }
        File newFile = new File(path);
        ProdottoGadget daVendere = new ProdottoGadget(nome,descrizione,usernameAutore,prezzo,numPezzi);
        daVendere.setFilepath(path);
        try (OutputStream os = new FileOutputStream(newFile)) {
            os.write(file.getBytes());
            this.compraVenditaHandler.addProdottoGadget(daVendere);
            return new ResponseEntity<>("Prodotto aggiunto con successo", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Errore durante il salvataggio del file",
                    HttpStatus.INTERNAL_SERVER_ERROR);
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
        if(utente instanceof ContributorAutorizzato && utente.getPassword().equals(password)) {
            if(this.compraVenditaHandler.removeProdottoGadget(id)) {
                return new ResponseEntity<>("Prodotto eliminato con successo", HttpStatus.OK);
            }
            return new ResponseEntity<>("Prodotto non presente", HttpStatus.BAD_REQUEST);
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
