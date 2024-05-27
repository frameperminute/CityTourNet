package it.unicam.cs.CityTourNet.restControllers;

import it.unicam.cs.CityTourNet.handlers.AuthHandler;
import it.unicam.cs.CityTourNet.utils.UtenteCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v0/auth")
public class AuthController {

    private final AuthHandler authHandler;

    @Autowired
    public AuthController(AuthHandler authHandler) {
        this.authHandler = authHandler;
    }

    @PutMapping("/richiediAutorizzazione")
    public ResponseEntity<Object> richiediAutorizzazione(@RequestParam String username) {
        if(this.authHandler.isContestAttivo()) {
            return new ResponseEntity<>("Al momento c'e' un contest attivo, " +
                    "percio' non puoi richiedere l'autorizzazione", HttpStatus.OK);
        }
        if(this.authHandler.richiediAutorizzazione(username)) {
            return new ResponseEntity<>("Autorizzazione eseguita con successo", HttpStatus.OK);
        }
        return new ResponseEntity<>("Non hai caricato abbastanza contenuti per richiedere l'autorizzazione",
                HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("/gestisciAutorizzazioni")
    public ResponseEntity<Object> gestisciAutorizzazioni(@RequestBody UtenteCredentials credentials) {
        if(this.authHandler.isGestore(credentials.username(), credentials.password())) {
            this.authHandler.gestisciAutorizzazioni();
            return new ResponseEntity<>("Autorizzazioni gestite", HttpStatus.OK);
        }
        return new ResponseEntity<>("Non hai i permessi per poter " +
                "eseguire questa funzione", HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("/gestisciEsitiNegativi")
    public ResponseEntity<Object> gestisciEsitiNegativi(@RequestParam String username,
                                                        @RequestParam String password,
                                                        @RequestParam String usernameContributorAut) {
        if(this.authHandler.isGestore(username, password)) {
            this.authHandler.gestisciEsitiNegativi(usernameContributorAut);
            return new ResponseEntity<>("Esiti negativi gestiti", HttpStatus.OK);
        }
        return new ResponseEntity<>("Non hai i permessi per poter " +
                "eseguire questa funzione", HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("/contenutiMinAuth")
    public ResponseEntity<Object> setContenutiMinimiAutorizzazione(@RequestParam String username,
                                                                   @RequestParam String password,
                                                                   @RequestParam Integer contMin) {
        if(this.authHandler.isGestore(username, password)){
            this.authHandler.setContenutiMinimiAutorizzazione(username, contMin);
            return new ResponseEntity<>("Numero minimo contenuti autorizzazione e' ora: "
                    + Math.abs(contMin), HttpStatus.OK);
        }
        return new ResponseEntity<>("Non hai i permessi per poter " +
                "eseguire questa funzione", HttpStatus.UNAUTHORIZED);
    }

}
