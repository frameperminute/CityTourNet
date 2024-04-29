package it.unicam.cs.CityTourNet.restControllers;

import it.unicam.cs.CityTourNet.handlers.NotificheHandler;
import it.unicam.cs.CityTourNet.handlers.UtentiHandler;
import it.unicam.cs.CityTourNet.model.Notifica;
import it.unicam.cs.CityTourNet.model.utente.Utente;
import it.unicam.cs.CityTourNet.utils.UtenteCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v0/notifiche")
public class NotificheController {

    private final NotificheHandler notificheHandler;

    private final UtentiHandler utentiHandler;

    @Autowired
    public NotificheController(NotificheHandler notificheHandler, UtentiHandler utentiHandler) {
        this.notificheHandler = notificheHandler;
        this.utentiHandler = utentiHandler;
    }

    @PostMapping("/invia")
    public ResponseEntity<Object> invia(@RequestBody Notifica notifica) {
        if(notifica != null) {
            this.notificheHandler.inviaNotifica(notifica);
            return new ResponseEntity<>("Notifica inviata", HttpStatus.OK);
        }
        return new ResponseEntity<>("Notifica non valida", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/leggiTutto")
    public ResponseEntity<Object> leggiTutto(@RequestBody UtenteCredentials credentials) {
        Utente utente = this.utentiHandler.getUtenteByUsername(credentials.username());
        if(utente != null && utente.getPassword().equals(credentials.password())) {
            List<Notifica> notifiche = this.notificheHandler.leggiTutto(credentials.username());
            if (!notifiche.isEmpty()) {
                List<String> messaggi = notifiche.stream().map(Notifica::leggi).toList();
                return new ResponseEntity<>(messaggi, HttpStatus.OK);
            }
            return new ResponseEntity<>("Nessun messaggio presente",HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Non sei autorizzato", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/leggiByTesto")
    public ResponseEntity<Object> leggiByTesto(@RequestParam String username,
                                               @RequestParam String password,
                                               @RequestParam String testo) {
        Utente utente = this.utentiHandler.getUtenteByUsername(username);
        if(utente != null && utente.getPassword().equals(password)) {
            List<Notifica> notifiche = this.notificheHandler.leggiByTesto(username, testo);
            if (!notifiche.isEmpty()) {
                List<String> messaggi = notifiche.stream().map(Notifica::leggi).toList();
                return new ResponseEntity<>(messaggi, HttpStatus.OK);
            }
            return new ResponseEntity<>("Nessun messaggio presente", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Non sei autorizzato", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/leggiNonAncoraLette")
    public ResponseEntity<Object> leggiNonAncoraLette(@RequestBody UtenteCredentials credentials) {
        Utente utente = this.utentiHandler.getUtenteByUsername(credentials.username());
        if(utente != null && utente.getPassword().equals(credentials.password())) {
            List<Notifica> notifiche = this.notificheHandler.leggiNonAncoraLette(credentials.username());
            if(!notifiche.isEmpty()) {
                List<String> messaggi = notifiche.stream().map(Notifica::leggi).toList();
                return new ResponseEntity<>(messaggi, HttpStatus.OK);
            }
            return new ResponseEntity<>("Nessun messaggio presente",HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Non sei autorizzato", HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/eliminaTutto")
    public ResponseEntity<Object> eliminaTutto(@RequestBody UtenteCredentials credentials) {
        Utente utente = this.utentiHandler.getUtenteByUsername(credentials.username());
        if(utente != null && utente.getPassword().equals(credentials.password())) {
            this.notificheHandler.eliminaTutto(credentials.username());
            return new ResponseEntity<>("Tutti i messaggi sono stati eliminati", HttpStatus.OK);
        }
        return new ResponseEntity<>("Non sei autorizzato", HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/eliminaByTesto")
    public ResponseEntity<Object> eliminaByTesto(@RequestParam String username,
                                                 @RequestParam String password,
                                                 @RequestParam String testo){
        Utente utente = this.utentiHandler.getUtenteByUsername(username);
        if(utente != null && utente.getPassword().equals(password)) {
            this.notificheHandler.eliminaByTesto(username, testo);
            return new ResponseEntity<>("Messaggi contenenti testo: " + testo + " eliminati", HttpStatus.OK);
        }
        return new ResponseEntity<>("Non sei autorizzato",HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/eliminaGiaLette")
    public ResponseEntity<Object> eliminaGiaLette(@RequestBody UtenteCredentials credentials) {
        Utente utente = this.utentiHandler.getUtenteByUsername(credentials.username());
        if(utente != null && utente.getPassword().equals(credentials.password())) {
            this.notificheHandler.eliminaGiaLette(credentials.username());
            return new ResponseEntity<>("Messaggi gia' letti eliminati", HttpStatus.OK);
        }
        return new ResponseEntity<>("Non sei autorizzato",HttpStatus.UNAUTHORIZED);
    }

}
