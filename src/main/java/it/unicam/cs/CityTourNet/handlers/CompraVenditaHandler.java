package it.unicam.cs.CityTourNet.handlers;

import it.unicam.cs.CityTourNet.model.Notifica;
import it.unicam.cs.CityTourNet.model.contenuto.ProdottoGadget;
import it.unicam.cs.CityTourNet.model.utente.*;
import it.unicam.cs.CityTourNet.repositories.ContenutoRepository;
import it.unicam.cs.CityTourNet.repositories.NotificaRepository;
import it.unicam.cs.CityTourNet.repositories.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CompraVenditaHandler {

    private ContenutoRepository contenutoRepository;
    private UtenteRepository utenteRepository;
    private NotificaRepository notificaRepository;

    @Autowired
    public CompraVenditaHandler(ContenutoRepository contenutoRepository,
                                UtenteRepository utenteRepository, NotificaRepository notificaRepository) {
        this.contenutoRepository = contenutoRepository;
        this.utenteRepository = utenteRepository;
        this.notificaRepository = notificaRepository;
    }

    public ProdottoGadget getProdottoGadget(long id){
        return (ProdottoGadget) this.contenutoRepository.getReferenceById(id);
    }

    public ContributorAutorizzato getVenditore(long id){
        ProdottoGadget daAcquistare = (ProdottoGadget) this.contenutoRepository.getReferenceById(id);
        String venditore = daAcquistare.getUsernameAutore();
        return (ContributorAutorizzato) this.utenteRepository.getReferenceById(venditore);
    }

    public int getPuntiUtente(String username){
        Utente utente = this.utenteRepository.getReferenceById(username);
        if(utente instanceof Turista) {
            return ((Turista) utente).getPunti();
        } else if (utente instanceof TuristaAutenticato) {
            return ((TuristaAutenticato) utente).getPunti();
        }
        return -1;
    }

    public boolean riduciNumeroPezziDisponibili(long id, int numPezzi) {
        ProdottoGadget daAcquistare = this.getProdottoGadget(id);
        daAcquistare.setNumPezzi(daAcquistare.getNumPezzi() - numPezzi);
        this.contenutoRepository.saveAndFlush(daAcquistare);
        return true;
    }

    public boolean gestisciAcquistoProdottoGadget(long ID, int numPezzi, String username) {
        Acquirente acquirente = (Acquirente) this.utenteRepository.getReferenceById(username);
        ProdottoGadget prodottoDaAcquistare = (ProdottoGadget) this.contenutoRepository.getReferenceById(ID);
        int totale = prodottoDaAcquistare.getPrezzo()*numPezzi;
        if(prodottoDaAcquistare.getNumPezzi() >= numPezzi) {
            if(acquirente.getPunti() >= totale) {
                this.riduciNumeroPezziDisponibili(ID, numPezzi);
                acquirente.setPunti(acquirente.getPunti() - totale);
                this.utenteRepository.saveAndFlush((Utente) acquirente);
                this.inviaNotificaAcquistoConfermato(prodottoDaAcquistare.getUsernameAutore(), username,
                        prodottoDaAcquistare.getNome(), numPezzi, totale);
                return true;
            } else {
                this.inviaNotificaPuntiInsufficienti(prodottoDaAcquistare.getUsernameAutore(),username);
                return false;
            }
        }
        this.inviaNotificaPezziInsufficienti(prodottoDaAcquistare.getUsernameAutore(), username);
        return false;
    }


    private boolean inviaNotificaAcquistoConfermato(String usernameAutore, String usernameAcquirente,
                                                    String nomeProdottoGadget, int numPezzi, int prezzo){
        String testo = "Hai acquistato " + numPezzi + " pezzi del prodotto: " + nomeProdottoGadget + ".\n"
                + "Il costo totale e': " + prezzo + "\n";
        Notifica notifica = new Notifica(usernameAutore, usernameAcquirente, testo);
        this.notificaRepository.saveAndFlush(notifica);
        return true;
    }

    private boolean inviaNotificaPezziInsufficienti(String usernameAutore, String usernameAcquirente) {
        String testo = "Il numero di pezzi selezionato non è disponibile";
        Notifica notifica = new Notifica(usernameAutore, usernameAcquirente, testo);
        this.notificaRepository.saveAndFlush(notifica);
        return true;
    }

    private boolean inviaNotificaPuntiInsufficienti(String usernameAutore, String usernameAcquirente) {
        String testo = "Non possiedi abbastanza punti per effettuare l'acquisto";
        Notifica notifica = new Notifica(usernameAutore, usernameAcquirente, testo);
        this.notificaRepository.saveAndFlush(notifica);
        return true;
    }

}
