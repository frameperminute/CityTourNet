package it.unicam.cs.CityTourNet.repositories;

import it.unicam.cs.CityTourNet.model.utente.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Utente, String> {
}
