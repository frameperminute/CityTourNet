package it.unicam.cs.CityTourNet.repositories;

import it.unicam.cs.CityTourNet.model.utente.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UtenteRepository extends JpaRepository<Utente, String> {
    List<Utente> findAllByTipoUtente(String tipoUtente);
}
