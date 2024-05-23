package it.unicam.cs.CityTourNet.handlers;

import it.unicam.cs.CityTourNet.model.contenuto.Contenuto;
import it.unicam.cs.CityTourNet.model.contenuto.Itinerario;
import it.unicam.cs.CityTourNet.model.contenuto.POI;
import it.unicam.cs.CityTourNet.repositories.ContenutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class ContenutiHandler {

    private final ContenutoRepository contenutoRepository;

    @Autowired
    public ContenutiHandler(ContenutoRepository contenutoRepository) {
        this.contenutoRepository = contenutoRepository;
    }

    public boolean addPOI(POI poi) {
        poi.setDefinitive(true);
        this.contenutoRepository.saveAndFlush(poi);
        return true;
    }

    public boolean addItinerario(Itinerario itinerario) {
        itinerario.setDefinitive(true);
        this.contenutoRepository.saveAndFlush(itinerario);
        return true;
    }

    public boolean addPOIInPending(POI poi) {
        poi.setInPending(true);
        this.contenutoRepository.saveAndFlush(poi);
        return true;
    }

    public boolean addItinerarioInPending(Itinerario itinerario) {
        itinerario.setInPending(true);
        this.contenutoRepository.saveAndFlush(itinerario);
        return true;
    }

    public List<Contenuto> getContenutiByAutore(String username){
        return this.contenutoRepository.findContenutiByUsernameAutore(username)
                .stream().filter(Contenuto::isDefinitive).toList();
    }

    public List<Contenuto> getContenutiInPendingByAutore(String username){
        return this.contenutoRepository.findContenutiByUsernameAutore(username)
                .stream().filter(Contenuto::isInPending).toList();
    }

    public POI getPOIByID(long id){
        if(this.contenutoRepository.existsById(id)) {
            return (POI) this.contenutoRepository.findById(id).get();
        }
        return null;
    }

    public List<POI> getPOIsInPending(){
        return this.contenutoRepository.findAll().stream()
                .filter(c -> c instanceof POI && c.isInPending())
                .map(c -> (POI) c)
                .toList();
    }

    public List<Itinerario> getItinerariInPending(){
        return this.contenutoRepository.findAll().stream()
                .filter(c -> c instanceof Itinerario && c.isInPending())
                .map(c -> (Itinerario) c)
                .toList();
    }

    public List<POI> getPOIS(){
        return this.contenutoRepository.findAll().stream()
                .filter(c -> c instanceof POI && c.isDefinitive())
                .map(c -> (POI) c)
                .toList();
    }

    public List<Itinerario> getItinerari(){
        return this.contenutoRepository.findAll().stream()
                .filter(c -> c instanceof Itinerario && c.isDefinitive())
                .map(c -> (Itinerario) c)
                .toList();
    }


    public boolean removeContenuto(long id){
        if(this.contenutoRepository.existsById(id)) {
            Contenuto daEliminare = this.contenutoRepository.findById(id).get();
            if(daEliminare instanceof POI) {
                File fileDaCancellare = new File(((POI) daEliminare).getFilepath());
                if (fileDaCancellare.exists()) {
                    fileDaCancellare.delete();
                }
            }
            this.contenutoRepository.deleteById(id);
        }
        return true;
    }

    public boolean caricaDefinitivamente(long id){
        if(this.contenutoRepository.existsById(id)) {
            Contenuto definitivo = this.contenutoRepository.findById(id).get();
            definitivo.setDefinitive(true);
            definitivo.setInPending(false);
            this.contenutoRepository.saveAndFlush(definitivo);
            return true;
        }
        return false;
    }
}
