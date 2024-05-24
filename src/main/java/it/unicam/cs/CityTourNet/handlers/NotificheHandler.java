package it.unicam.cs.CityTourNet.handlers;

import it.unicam.cs.CityTourNet.model.Notifica;
import it.unicam.cs.CityTourNet.repositories.NotificaRepository;
import it.unicam.cs.CityTourNet.repositories.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificheHandler {

    private final NotificaRepository notificaRepository;
    private final UtenteRepository utenteRepository;

    @Autowired
    public NotificheHandler(NotificaRepository notificaRepository, UtenteRepository utenteRepository) {
        this.notificaRepository = notificaRepository;
        this.utenteRepository = utenteRepository;
    }

    public void inviaNotifica(Notifica notifica) {
        if(this.utenteRepository.existsById(notifica.getUsernameMittente())
                && this.utenteRepository.existsById(notifica.getUsernameDestinatario())) {
            this.notificaRepository.saveAndFlush(notifica);
        }
    }

    public List<Notifica> leggiTutto(String destinatario) {
        if(this.utenteRepository.existsById(destinatario)) {
            List<Notifica> daLeggere = this.notificaRepository.findAll().stream()
                    .filter(n -> n.getUsernameDestinatario().equals(destinatario)).toList();
            if(!daLeggere.isEmpty()) {
                daLeggere.forEach(notifica -> {
                    notifica.setLetto(true);
                    this.notificaRepository.saveAndFlush(notifica);
                });
                return daLeggere;
            }
        }
        return new ArrayList<>();
    }

    public List<Notifica> leggiByTesto(String destinatario, String testo) {
        List<Notifica> daLeggere = this.leggiTutto(destinatario).stream()
                .filter(n -> n.leggi().contains(testo)).toList();
        if(!daLeggere.isEmpty()) {
            daLeggere.forEach(notifica -> {
                notifica.setLetto(true);
                this.notificaRepository.saveAndFlush(notifica);
            });
        }
        return daLeggere;
    }

    public List<Notifica> leggiNonAncoraLette(String destinatario) {
        List<Notifica> daLeggere = this.leggiTutto(destinatario).stream().filter(n -> !n.isLetto()).toList();
        if(!daLeggere.isEmpty()) {
            daLeggere.forEach(notifica -> {
                notifica.setLetto(true);
                this.notificaRepository.saveAndFlush(notifica);
            });
        }
        return daLeggere;
    }

    public void eliminaTutto(String destinatario) {
        this.notificaRepository.deleteAllInBatch(this.leggiTutto(destinatario));
    }

    public void eliminaByTesto(String destinatario, String testo) {
        this.notificaRepository.deleteAllInBatch(this.leggiByTesto(destinatario,testo));
    }

    public void eliminaGiaLette(String destinatario) {
        List<Notifica> giaLetti = this.leggiTutto(destinatario).stream().filter(Notifica::isLetto).toList();
        if(!giaLetti.isEmpty()) {
            this.notificaRepository.deleteAllInBatch(giaLetti);
        }
    }

}
