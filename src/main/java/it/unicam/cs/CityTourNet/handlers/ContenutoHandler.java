package it.unicam.cs.CityTourNet.handlers;

import it.unicam.cs.CityTourNet.model.contenuto.Contenuto;
import it.unicam.cs.CityTourNet.model.contenuto.Itinerario;
import it.unicam.cs.CityTourNet.model.contenuto.POI;
import it.unicam.cs.CityTourNet.model.contenuto.ProdottoGadget;
import it.unicam.cs.CityTourNet.model.utente.Utente;
import it.unicam.cs.CityTourNet.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContenutoHandler {

    private final ContenutoRepository contenutoRepository;
    private final NotificaRepository notificaRepository;
    private final UtenteRepository utenteRepository;
    private final ContenutoInPendingRepository contenutoInPendingRepository;
    private final ContenutoContestRepository contenutoContestRepository;

    @Autowired
    public ContenutoHandler(ContenutoRepository contenutoRepository, NotificaRepository notificaRepository,
                            UtenteRepository utenteRepository,
                            ContenutoInPendingRepository contenutoInPendingRepository,
                            ContenutoContestRepository contenutoContestRepository) {
        this.contenutoRepository = contenutoRepository;
        this.notificaRepository = notificaRepository;
        this.utenteRepository = utenteRepository;
        this.contenutoInPendingRepository = contenutoInPendingRepository;
        this.contenutoContestRepository = contenutoContestRepository;
    }

    public boolean addPOI(POI poi) {
        this.contenutoRepository.save(poi);
        return true;
    }

    public boolean addItinerario(Itinerario itinerario) {
        this.contenutoRepository.save(itinerario);
        return true;
    }

    public boolean addPOIInPending(POI poi) {
        this.contenutoInPendingRepository.save(poi);
        return true;
    }

    public boolean addItinerarioInPending(Itinerario itinerario) {
        this.contenutoInPendingRepository.save(itinerario);
        return true;
    }

    public List<Contenuto> getContenutiByAutore(String username){
        return this.contenutoRepository.findContenutiByUsernameAutore(username);
    }

    public List<Contenuto> getContenutiInPendingByAutore(String username){
        return this.contenutoInPendingRepository.findContenutiByUsernameAutore(username);
    }

    public POI getPOIByID(long id){
          return (POI) this.contenutoRepository.getReferenceById(id);
    }

    public Itinerario getItinerarioByID(long id){
        return (Itinerario) this.contenutoRepository.getReferenceById(id);
    }

    public ProdottoGadget getProdottoGadgetByID(long id){
        return (ProdottoGadget) this.contenutoRepository.getReferenceById(id);
    }

    public POI getPOIInPendingByID(long id){
        return (POI) this.contenutoInPendingRepository.getReferenceById(id);
    }

    public Itinerario getItinerarioInPendingByID(long id){
        return (Itinerario) this.contenutoInPendingRepository.getReferenceById(id);
    }

    public List<Contenuto> getContenutiInPending(){
        return this.contenutoInPendingRepository.findAll();
    }

    public List<POI> getPOIS(){
        return this.contenutoRepository.findAll().stream()
                .filter(c -> c.getTipoContenuto().equals("POI"))
                .map(c -> (POI) c)
                .toList();
    }

    public List<Itinerario> getItinerari(){
        return this.contenutoRepository.findAll().stream()
                .filter(c -> c.getTipoContenuto().equals("Itinerario"))
                .map(c -> (Itinerario) c)
                .toList();
    }

    public Utente getUtente(long id){
        return this.utenteRepository
                .getReferenceById(this.contenutoRepository.getReferenceById(id).getUsernameAutore());
    }

    public boolean removeContenuto(long ID){
        this.contenutoRepository.deleteById(ID);
        return true;
    }





}
