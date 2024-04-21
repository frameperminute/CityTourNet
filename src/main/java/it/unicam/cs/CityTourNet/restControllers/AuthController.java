package it.unicam.cs.CityTourNet.restControllers;

import it.unicam.cs.CityTourNet.handlers.AuthHandler;
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

    @PutMapping("/richiediAutenticazione")
    public ResponseEntity<Object> richiediAutenticazione(@RequestBody String username) {
        if(this.authHandler.richiediAutenticazione(username)) {
            return new ResponseEntity<>("Autenticazione eseguita con successo", HttpStatus.OK);
        }
        return new ResponseEntity<>("Non hai abbastanza punti per richiedere l'autenticazione",
                HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("/richiediAutorizzazione")
    public ResponseEntity<Object> richiediAutorizzazione(@RequestBody String username) {
        if(this.authHandler.richiediAutorizzazione(username)) {
            return new ResponseEntity<>("Autorizzazione eseguita con successo", HttpStatus.OK);
        }
        return new ResponseEntity<>("Non hai caricato abbastanza contenuti per richiedere l'autorizzazione",
                HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("/eliminaAutenticazione")
    public ResponseEntity<Object> eliminaAutenticazioni(@RequestBody String username,
                                                        @RequestBody String password,
                                                        @RequestBody String usernameTuristaAut) {
        if(this.authHandler.isGestore(username, password)) {
            this.authHandler.eliminaAutenticazioni();
            return new ResponseEntity<>("Autenticazioni eliminate", HttpStatus.OK);
        }
        return new ResponseEntity<>("Non hai i permessi per poter " +
                "eseguire questa funzione", HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("/gestisciAutorizzazioni")
    public ResponseEntity<Object> gestisciAutorizzazioni(@RequestBody String username,
                                                         @RequestBody String password) {
        if(this.authHandler.isGestore(username, password)) {
            this.authHandler.gestisciAutorizzazioni();
            return new ResponseEntity<>("Autorizzazioni gestite", HttpStatus.OK);
        }
        return new ResponseEntity<>("Non hai i permessi per poter " +
                "eseguire questa funzione", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/gestisciEsitiNegativi")
    public ResponseEntity<Object> gestisciEsitiNegativi(@RequestBody String username,
                                                        @RequestBody String password,
                                                        @RequestBody String usernameContributorAut) {
        if(this.authHandler.isGestore(username, password)) {
            this.authHandler.gestisciEsitiNegativi(usernameContributorAut);
            return new ResponseEntity<>("Esiti negativi gestiti", HttpStatus.OK);
        }
        return new ResponseEntity<>("Non hai i permessi per poter " +
                "eseguire questa funzione", HttpStatus.UNAUTHORIZED);
    }

}
