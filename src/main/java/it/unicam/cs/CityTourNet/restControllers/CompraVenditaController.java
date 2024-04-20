package it.unicam.cs.CityTourNet.restControllers;

import it.unicam.cs.CityTourNet.handlers.CompraVenditaHandler;
import it.unicam.cs.CityTourNet.model.contenuto.ProdottoGadget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v0/compravendita")
public class CompraVenditaController {

    private final CompraVenditaHandler compraVenditaHandler;

    @Autowired
    public CompraVenditaController(CompraVenditaHandler compraVenditaHandler) {
        this.compraVenditaHandler = compraVenditaHandler;
    }

    @GetMapping("/prodotto")
    public ResponseEntity<Object> getProdottoGadget(@RequestParam long ID) {
        return new ResponseEntity<>(this.compraVenditaHandler.getProdottoGadget(ID), HttpStatus.OK);
    }

    @GetMapping("/prodotti")
    public ResponseEntity<Object> getProdottiGadget() {
        return new ResponseEntity<>(this.compraVenditaHandler.getProdottiGadget(), HttpStatus.OK);
    }

    @PutMapping("/acquista")
    public ResponseEntity<Object> acquistaProdotto(@RequestParam long ID,@RequestParam int numPezzi,
                                                   @RequestParam String username,@RequestParam String indirizzo){
        if(this.compraVenditaHandler.gestisciAcquistoProdottoGadget(ID, numPezzi, username, indirizzo)) {
            return new ResponseEntity<>("L'acquisto e' stato effettuato con successo", HttpStatus.OK);
        } else {
           return new ResponseEntity<>("L'acquisto non e' stato effettuato con successo", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/punti")
    public ResponseEntity<Object> getPuntiPersonali(@RequestParam String username) {
        return new ResponseEntity<>(this.compraVenditaHandler.getPuntiUtente(username), HttpStatus.OK);
    }

    @PostMapping("/vendi")
    public ResponseEntity<Object> vendiProdottoGadget(@RequestBody ProdottoGadget prodottoGadgetDaVendere){
        this.compraVenditaHandler.addProdottoGadget(prodottoGadgetDaVendere);
        return new ResponseEntity<>("Prodotto aggiunto con successo", HttpStatus.OK);
    }

    @DeleteMapping("/elimina")
    public ResponseEntity<Object> eliminaProdottoGadget(@RequestParam long ID){
        this.compraVenditaHandler.removeProdottoGadget(ID);
        return new ResponseEntity<>("Prodotto eliminato con successo", HttpStatus.OK);
    }

}
