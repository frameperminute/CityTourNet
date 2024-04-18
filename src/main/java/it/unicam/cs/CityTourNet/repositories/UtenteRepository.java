package it.unicam.cs.CityTourNet.repositories;


import it.unicam.cs.CityTourNet.model.contenuto.ProdottoGadget;
import it.unicam.cs.CityTourNet.model.utente.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UtenteRepository extends JpaRepository<Utente, String> {
    List<Utente> findByTipoUtente(String tipoUtente);
}
