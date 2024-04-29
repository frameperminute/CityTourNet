package it.unicam.cs.CityTourNet.handlers;

import it.unicam.cs.CityTourNet.model.utente.*;
import it.unicam.cs.CityTourNet.repositories.UtenteRepository;
import it.unicam.cs.CityTourNet.utils.UtenteCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UtentiHandler {

    private final UtenteRepository utenteRepository;

    @Autowired
    public UtentiHandler(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    public boolean iscrivi(Utente utente) {
        if(utente.getEmail() != null && utente.getPassword() != null
                && !this.utenteRepository.existsById(utente.getUsername())) {
            switch(utente.getClass().getSimpleName()) {
                case "Turista" : this.utenteRepository.saveAndFlush((Turista) utente);break;
                case "Contributor" : this.utenteRepository.saveAndFlush((Contributor) utente);break;
                default: return false;
            }
            return true;
        }
        return false;
    }

    public boolean iscriviUtentiUnici(Utente utente) {
        if(utente.getEmail() != null && utente.getPassword() != null
                && !this.utenteRepository.existsById(utente.getUsername())
                && this.utenteRepository.findAllByTipoUtente(utente.getClass().getSimpleName()).isEmpty()) {
            switch(utente.getClass().getSimpleName()) {
                case "Animatore" : this.utenteRepository.saveAndFlush((Animatore) utente);break;
                case "Curatore" : this.utenteRepository.saveAndFlush((Curatore) utente);break;
                case "GestoreDellaPiattaforma" : this.utenteRepository
                        .saveAndFlush((GestoreDellaPiattaforma) utente);break;
                default: return false;
            }
            return true;
        }
        return false;
    }

    public boolean annullaIscrizione(UtenteCredentials credentials) {
        if(this.utenteRepository.existsById(credentials.username())) {
            Utente daEliminare = this.utenteRepository.findById(credentials.username()).get();
            if (daEliminare.getPassword().equals(credentials.password())) {
                this.utenteRepository.deleteById(credentials.username());
                return true;
            }
        }
        return false;
    }

    public boolean login(UtenteCredentials credentials) {
        Utente daLoggare = this.utenteRepository.findById(credentials.username()).get();
        if(daLoggare.getPassword().equals(credentials.password())) {
            daLoggare.setLoggedIn(true);
            this.utenteRepository.saveAndFlush(daLoggare);
            return true;
        }
        return false;
    }


    public boolean logout(UtenteCredentials credentials) {
        Utente daUnloggare = this.utenteRepository.findById(credentials.username()).get();
        if(daUnloggare.getPassword().equals(credentials.password())) {
            daUnloggare.setLoggedIn(false);
            this.utenteRepository.saveAndFlush(daUnloggare);
            return true;
        }
        return false;
    }

    public Utente getUtenteByUsername(String username) {
        if(utenteRepository.existsById(username)){
            return utenteRepository.findById(username).get();
        }
        return null;
    }

    public boolean cambiaPassword(String username, String oldPassword, String newPassword) {
        Utente daCambiare = null;
        if(this.utenteRepository.existsById(username)) {
            daCambiare = this.utenteRepository.findById(username).get();
        }
        if(daCambiare != null && daCambiare.getPassword().equals(oldPassword)) {
            daCambiare.setPassword(newPassword);
            this.utenteRepository.saveAndFlush(daCambiare);
            return true;
        }
        return false;
    }
}
