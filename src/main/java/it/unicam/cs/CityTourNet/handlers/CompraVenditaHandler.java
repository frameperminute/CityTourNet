package it.unicam.cs.CityTourNet.handlers;

import it.unicam.cs.CityTourNet.model.contenuto.ProdottoGadget;
import it.unicam.cs.CityTourNet.model.utente.ContributorAutorizzato;
import it.unicam.cs.CityTourNet.model.utente.Turista;
import it.unicam.cs.CityTourNet.model.utente.TuristaAutenticato;
import it.unicam.cs.CityTourNet.model.utente.Utente;
import it.unicam.cs.CityTourNet.repositories.ContenutoRepository;
import it.unicam.cs.CityTourNet.repositories.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CompraVenditaHandler {

    private ContenutoRepository contenutoRepository;
    private UtenteRepository utenteRepository;

    @Autowired
    public CompraVenditaHandler(ContenutoRepository contenutoRepository, UtenteRepository utenteRepository) {
        this.contenutoRepository = contenutoRepository;
        this.utenteRepository = utenteRepository;
    }

    public List<ProdottoGadget> getProdottiGadget(){
        return this.contenutoRepository
                .findProdottiGadgetByTipo("ProdottoGadget")
                .stream()
                .map(contenuto -> (ProdottoGadget) contenuto)
                .toList();
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

    public boolean removeProdottoGadget(long id) {
        this.contenutoRepository.deleteById(id);
        return true;
    }

    public boolean addProdottoGadget(ProdottoGadget gadget) {
        this.contenutoRepository.save(gadget);
        return true;
    }


}
