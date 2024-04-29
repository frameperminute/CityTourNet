package it.unicam.cs.CityTourNet.restControllers;

import it.unicam.cs.CityTourNet.handlers.UtentiHandler;
import it.unicam.cs.CityTourNet.model.utente.*;
import it.unicam.cs.CityTourNet.utils.UtenteCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v0/utenti")
public class UtentiController {

    private final UtentiHandler utentiHandler;

    @Autowired
    public UtentiController(UtentiHandler utentiHandler) {
        this.utentiHandler = utentiHandler;
    }

    @PostMapping("/iscrivitiTurista")
    public ResponseEntity<Object> iscrizioneTurista(@RequestBody Turista turista) {
        if(this.utentiHandler.iscrivi(turista)) {
            return new ResponseEntity<>("Iscrizione effettuata", HttpStatus.OK);
        }
        return new ResponseEntity<>("Non tutti i campi sono inseriti correttamente" +
                " o hai inserito un username di un utente gia' registrato", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/iscrivitiContributor")
    public ResponseEntity<Object> iscrizioneContributor(@RequestBody Contributor contributor) {
        if(this.utentiHandler.iscrivi(contributor)) {
            return new ResponseEntity<>("Iscrizione effettuata", HttpStatus.OK);
        }
        return new ResponseEntity<>("Non tutti i campi sono inseriti correttamente" +
                " o hai inserito un username di un utente gia' registrato", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/iscrivitiAnimatore")
    public ResponseEntity<Object> iscrizioneAnimatore(@RequestBody Animatore animatore) {
        if(this.utentiHandler.iscriviUtentiUnici(animatore)) {
            return new ResponseEntity<>("Iscrizione effettuata", HttpStatus.OK);
        }
        return new ResponseEntity<>("Non tutti i campi sono inseriti correttamente" +
                " o hai inserito un username di un utente gia' registrato " +
                "oppure e' gia' presente un Animatore", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/iscrivitiCuratore")
    public ResponseEntity<Object> iscrizioneCuratore(@RequestBody Curatore curatore) {
        if(this.utentiHandler.iscriviUtentiUnici(curatore)) {
            return new ResponseEntity<>("Iscrizione effettuata", HttpStatus.OK);
        }
        return new ResponseEntity<>("Non tutti i campi sono inseriti correttamente" +
                " o hai inserito un username di un utente gia' registrato " +
                "oppure e' gia' presente un Curatore", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/iscrivitiGestore")
    public ResponseEntity<Object> iscrizioneGestore(@RequestBody GestoreDellaPiattaforma gestore) {
        if(this.utentiHandler.iscriviUtentiUnici(gestore)) {
            return new ResponseEntity<>("Iscrizione effettuata", HttpStatus.OK);
        }
        return new ResponseEntity<>("Non tutti i campi sono inseriti correttamente" +
                " o hai inserito un username di un utente gia' registrato " +
                "oppure e' gia' presente un Gestore", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/annullaIscrizione")
    public ResponseEntity<Object> annullaIscrizione(@RequestBody UtenteCredentials utenteCredentials) {
        if(this.utentiHandler.annullaIscrizione(utenteCredentials)) {
            return new ResponseEntity<>("Iscrizione annullata", HttpStatus.OK);
        }
        return new ResponseEntity<>("Username e/o password errati", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UtenteCredentials utenteCredentials) {
        if(this.utentiHandler.login(utenteCredentials)) {
            return new ResponseEntity<>("Login effettuato con successo", HttpStatus.OK);
        }
        return new ResponseEntity<>("Username e/o password errati", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/logout")
    public ResponseEntity<Object> logout(@RequestBody UtenteCredentials utenteCredentials) {
        if(this.utentiHandler.logout(utenteCredentials)) {
            return new ResponseEntity<>("Logout effettuato con successo", HttpStatus.OK);
        }
        return new ResponseEntity<>("Username e/o password errati", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/cambiaPassword")
    public ResponseEntity<Object> cambiaPassword(@RequestParam String username,
                                                 @RequestParam String oldPassword,
                                                 @RequestParam String newPassword) {
        if(this.utentiHandler.cambiaPassword(username, oldPassword, newPassword)) {
            return new ResponseEntity<>("cambio password effettuato", HttpStatus.OK);
        }
        return new ResponseEntity<>("Username e/o password errati", HttpStatus.BAD_REQUEST);
    }
}
