package it.unicam.cs.CityTourNet.handlers;

import it.unicam.cs.CityTourNet.model.contenuto.*;
import it.unicam.cs.CityTourNet.repositories.ContenutoMementoRepository;
import it.unicam.cs.CityTourNet.repositories.ContenutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Comparator;
import java.util.List;

@Service
public class ContenutiHandler {

    private final ContenutoRepository contenutoRepository;
    private final ContenutoMementoRepository contenutoMementoRepository;

    @Autowired
    public ContenutiHandler(ContenutoRepository contenutoRepository, ContenutoMementoRepository contenutoMementoRepository) {
        this.contenutoRepository = contenutoRepository;
        this.contenutoMementoRepository = contenutoMementoRepository;
    }

    public void addPOI(POI poi) {
        poi.setDefinitive(true);
        this.contenutoRepository.saveAndFlush(poi);
        this.salvaStato(poi);
    }

    public void addItinerario(Itinerario itinerario) {
        itinerario.setDefinitive(true);
        this.contenutoRepository.saveAndFlush(itinerario);
        this.salvaStato(itinerario);
    }

    public void addPOIInPending(POI poi) {
        poi.setInPending(true);
        this.contenutoRepository.saveAndFlush(poi);
    }

    public void addItinerarioInPending(Itinerario itinerario) {
        itinerario.setInPending(true);
        this.contenutoRepository.saveAndFlush(itinerario);
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

    public List<POI> getPOIS(String nome){
        return this.contenutoRepository.findAll().stream()
                .filter(c -> nome == null || c.getNome().equalsIgnoreCase(nome))
                .filter(c -> c instanceof POI && c.isDefinitive())
                .map(c -> (POI) c)
                .toList();
    }

    public List<Itinerario> getItinerari(String nome, Integer oreMax, String difficolta){
        return this.contenutoRepository.findAll().stream()
                .filter(c -> nome == null || c.getNome().equalsIgnoreCase(nome))
                .filter(c -> c instanceof Itinerario && c.isDefinitive())
                .map(c -> (Itinerario) c)
                .filter(c -> oreMax == null || c.getOre() <= oreMax)
                .filter(c -> difficolta == null || c.getDifficolta().equalsIgnoreCase(difficolta))
                .toList();
    }


    public boolean removeContenuto(long id){
        if(this.contenutoRepository.existsById(id)) {
            Contenuto daEliminare = this.contenutoRepository.findById(id).get();
            if(daEliminare instanceof POI) {
                if(this.contenutoRepository.findAll().stream()
                        .noneMatch(i -> i instanceof Itinerario && ((Itinerario) i).getIndiciPOIs().contains(id))) {
                    File fileDaCancellare = new File(((POI) daEliminare).getFilepath());
                    if (fileDaCancellare.exists()) {
                        fileDaCancellare.delete();
                    }
                } else {
                    return false;
                }
            }
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

    public boolean caricaDefinitivamente(long id){
        if(this.contenutoRepository.existsById(id)) {
            Contenuto definitivo = this.contenutoRepository.findById(id).get();
            definitivo.setDefinitive(true);
            definitivo.setInPending(false);
            this.contenutoRepository.saveAndFlush(definitivo);
            this.salvaStato(definitivo);
            return true;
        }
        return false;
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

    public boolean eseguiModifiche(String nome, String descrizione, long id) {
        if(this.contenutoRepository.existsById(id)) {
            this.contenutoRepository.findById(id).get().setNome(nome);
            this.contenutoRepository.findById(id).get().setDescrizione(descrizione);
            this.salvaStato(this.contenutoRepository.findById(id).get());
            return true;
        }
        return false;
    }
}
