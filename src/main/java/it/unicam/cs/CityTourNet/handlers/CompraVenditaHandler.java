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

    public List<ProdottoGadget> getProdottiGadget(String nome, Integer prezzoMax){
        return this.contenutoRepository.findAll()
                .stream()
                .filter(c -> nome == null || c.getNome().equalsIgnoreCase(nome))
                .filter(c -> c instanceof ProdottoGadget)
                .map(c -> (ProdottoGadget) c)
                .filter(c -> prezzoMax == null || c.getPrezzo() <= prezzoMax)
                .toList();
    }

    public void addProdottoGadget(ProdottoGadget gadget) {
        gadget.setDefinitive(true);
        this.contenutoRepository.saveAndFlush(gadget);
        this.salvaStato(gadget);
    }

    public boolean removeProdottoGadget(long id){
        if(this.contenutoRepository.existsById(id)) {
            Contenuto daEliminare = this.contenutoRepository.findById(id).get();
            List<ContenutoMemento> mementoStack = this.contenutoMementoRepository.findAll()
                    .stream()
                    .filter(m -> m.getIDContenuto() == daEliminare.getID())
                    .toList();
            this.contenutoMementoRepository.deleteAll(mementoStack);
            this.contenutoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public int getPuntiUtente(String username){
        Utente utente = this.utenteRepository.findById(username).get();
        if(utente instanceof TuristaAutenticato turistaAutenticato) {
            return turistaAutenticato.getPunti();
        }
        return -1;
    }

    private void riduciNumeroPezziDisponibili(ProdottoGadget daAcquistare, int numPezzi) {
        if(daAcquistare.getNumPezzi() - numPezzi == 0) {
            this.removeProdottoGadget(daAcquistare.getID());
        } else {
            daAcquistare.setNumPezzi(daAcquistare.getNumPezzi() - numPezzi);
            this.contenutoRepository.saveAndFlush(daAcquistare);
        }
    }

    /**
     * Verifica che il ProdottoGadget sia disponibile nella quantita' richiesta dal TuristaAutenticato e
     * che questi abbia un numero di punti sufficiente.
     * Se e' cosi', al ProdottoGadget viene sottratta la quantita' richiesta e al TuristaAutenticato viene
     * sottratta la somma prevista
     * @param id identificativo ProdottoGadget
     * @param numPezzi quantita' richiesta
     * @param username TuristaAutenticato acquirente
     * @param indirizzo indirizzo di spedizione
     * @return true se l'operazione e' andata a buon fine, false se non ci sono pezzi e/o punti sufficienti
     * a effettuare l'acquisto
     */
    public boolean gestisciAcquistoProdottoGadget(long id, int numPezzi,
                                                  String username, String indirizzo) {
        TuristaAutenticato acquirente = (TuristaAutenticato) this.utenteRepository.findById(username).get();
        ProdottoGadget prodottoDaAcquistare = (ProdottoGadget) this.contenutoRepository.findById(id).get();
        int totale = prodottoDaAcquistare.getPrezzo() * numPezzi;
        if(prodottoDaAcquistare.getNumPezzi() >= numPezzi) {
            if(acquirente.getPunti() >= totale) {
                this.riduciNumeroPezziDisponibili(prodottoDaAcquistare, numPezzi);
                acquirente.setPunti(acquirente.getPunti() - totale);
                this.utenteRepository.saveAndFlush(acquirente);
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

    /**
     * Cancella l'ultimo stato del Contenuto e rimette sulla piattaforma quello precedente
     * @param contenuto Contenuto di cui recuperare lo stato precedente
     * @return true se esiste uno stato precedente, false altrimenti
     */
    private boolean recuperaStato(Contenuto contenuto) {
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
