package it.unicam.cs.CityTourNet.handlers;

import it.unicam.cs.CityTourNet.model.Notifica;
import it.unicam.cs.CityTourNet.model.contenuto.Contenuto;
import it.unicam.cs.CityTourNet.model.contenuto.ContenutoMemento;
import it.unicam.cs.CityTourNet.model.contenuto.ProdottoGadget;
import it.unicam.cs.CityTourNet.model.utente.*;
import it.unicam.cs.CityTourNet.repositories.ContenutoMementoRepository;
import it.unicam.cs.CityTourNet.repositories.ContenutoRepository;
import it.unicam.cs.CityTourNet.repositories.NotificaRepository;
import it.unicam.cs.CityTourNet.repositories.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;


@Service
public class CompraVenditaHandler {

    private final ContenutoRepository contenutoRepository;
    private final UtenteRepository utenteRepository;
    private final NotificaRepository notificaRepository;
    private final ContenutoMementoRepository contenutoMementoRepository;

    @Autowired
    public CompraVenditaHandler(ContenutoRepository contenutoRepository, UtenteRepository utenteRepository,
                                NotificaRepository notificaRepository,
                                ContenutoMementoRepository contenutoMementoRepository) {
        this.contenutoRepository = contenutoRepository;
        this.utenteRepository = utenteRepository;
        this.notificaRepository = notificaRepository;
        this.contenutoMementoRepository = contenutoMementoRepository;
    }

    public ProdottoGadget getProdottoGadget(long id){
        if(this.contenutoRepository.existsById(id)) {
            return (ProdottoGadget) this.contenutoRepository.findById(id).get();
        }
        return null;
    }

    public List<ProdottoGadget> getProdottiGadget(){
        return this.contenutoRepository.findAll()
                .stream()
                .filter(c -> c instanceof ProdottoGadget)
                .map(c -> (ProdottoGadget) c)
                .toList();
    }

    public void addProdottoGadget(ProdottoGadget gadget) {
        gadget.setDefinitive(true);
        this.contenutoRepository.saveAndFlush(gadget);
        this.salvaStato(gadget);
    }

    public void removeProdottoGadget(long id){
        if(this.contenutoRepository.existsById(id)) {
            Contenuto daEliminare = this.contenutoRepository.findById(id).get();
            List<ContenutoMemento> mementoStack = this.contenutoMementoRepository.findAll()
                    .stream()
                    .filter(m -> m.getIDContenuto() == daEliminare.getID())
                    .toList();
            this.contenutoMementoRepository.deleteAll(mementoStack);
            this.contenutoRepository.deleteById(id);
        }
    }

    public int getPuntiUtente(String username){
        Utente utente = this.utenteRepository.findById(username).get();
        if(utente instanceof TuristaAutenticato) {
            return ((TuristaAutenticato) utente).getPunti();
        }
        return -1;
    }

    private void riduciNumeroPezziDisponibili(long id, int numPezzi) {
        ProdottoGadget daAcquistare = this.getProdottoGadget(id);
        daAcquistare.setNumPezzi(daAcquistare.getNumPezzi() - numPezzi);
        this.contenutoRepository.saveAndFlush(daAcquistare);
    }

    public boolean gestisciAcquistoProdottoGadget(long id, int numPezzi,
                                                  String username, String indirizzo) {
        TuristaAutenticato acquirente = (TuristaAutenticato) this.utenteRepository.findById(username).get();
        ProdottoGadget prodottoDaAcquistare = (ProdottoGadget) this.contenutoRepository.findById(id).get();
        int totale = prodottoDaAcquistare.getPrezzo()*numPezzi;
        if(prodottoDaAcquistare.getNumPezzi() >= numPezzi) {
            if(acquirente.getPunti() >= totale) {
                this.riduciNumeroPezziDisponibili(id, numPezzi);
                acquirente.setPunti(acquirente.getPunti() - totale);
                this.utenteRepository.saveAndFlush((Utente) acquirente);
                this.inviaNotificaAcquistoConfermato(prodottoDaAcquistare.getUsernameAutore(), username,
                        prodottoDaAcquistare.getNome(), numPezzi, totale, indirizzo);
                return true;
            } else {
                this.inviaNotificaPuntiInsufficienti(prodottoDaAcquistare.getUsernameAutore(),username);
                return false;
            }
        }
        this.inviaNotificaPezziInsufficienti(prodottoDaAcquistare.getUsernameAutore(), username);
        return false;
    }


    private void inviaNotificaAcquistoConfermato(String usernameAutore,
                                                 String usernameAcquirente,
                                                 String nomeProdottoGadget, int numPezzi,
                                                 int prezzo, String indirizzo){
        String testo = "Hai acquistato " + numPezzi + " pezzi del prodotto: " + nomeProdottoGadget + ".\n"
                + "Il costo totale e': " + prezzo + "\n" +
                "Il prodotto verra' spedito a breve a questo indirizzo: " + indirizzo;
        Notifica notifica = new Notifica(usernameAutore, usernameAcquirente, testo);
        this.notificaRepository.saveAndFlush(notifica);
    }

    private void inviaNotificaPezziInsufficienti(String usernameAutore,
                                                 String usernameAcquirente) {
        String testo = "Il numero di pezzi selezionato non è disponibile";
        Notifica notifica = new Notifica(usernameAutore, usernameAcquirente, testo);
        this.notificaRepository.saveAndFlush(notifica);
    }

    private void inviaNotificaPuntiInsufficienti(String usernameAutore,
                                                 String usernameAcquirente) {
        String testo = "Non possiedi abbastanza punti per effettuare l'acquisto";
        Notifica notifica = new Notifica(usernameAutore, usernameAcquirente, testo);
        this.notificaRepository.saveAndFlush(notifica);
    }

    private void salvaStato(Contenuto contenuto){
        this.contenutoMementoRepository.saveAndFlush(contenuto.createMemento());
    }

    public boolean recuperaStato(Contenuto contenuto) {
        List<ContenutoMemento> mementoStack = this.contenutoMementoRepository.findAll()
                .stream().filter(m -> m.getIDContenuto() == contenuto.getID())
                .sorted(Comparator.comparingLong(ContenutoMemento::getId))
                .toList();
        if (mementoStack.size() > 1) {
            ContenutoMemento daRecuperare = mementoStack.get(mementoStack.size() - 2);
            ContenutoMemento daEliminare = mementoStack.get(mementoStack.size() - 1);
            contenuto.restoreMemento(daRecuperare);
            this.contenutoMementoRepository.deleteById(daEliminare.getId());
            return true;
        }
        return false;
    }

    public boolean annullaModifiche(long id) {
        if(this.contenutoRepository.existsById(id)) {
            return this.recuperaStato(this.contenutoRepository.findById(id).get());
        }
        return false;
    }

    public boolean eseguiModifiche(int prezzo, int numPezzi, long id) {
        if(this.contenutoRepository.existsById(id)) {
            if(this.contenutoRepository.findById(id).get() instanceof ProdottoGadget prodottoGadget) {
                prodottoGadget.setPrezzo(prezzo);
                prodottoGadget.setNumPezzi(numPezzi);
                this.salvaStato(prodottoGadget);
                return true;
            }
        }
        return false;
    }

}
