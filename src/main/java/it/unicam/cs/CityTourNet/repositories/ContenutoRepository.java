package it.unicam.cs.CityTourNet.repositories;

import it.unicam.cs.CityTourNet.model.contenuto.Contenuto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContenutoRepository extends JpaRepository<Contenuto, Long> {
    List<Contenuto> findContenutiByUsernameAutore(String username);
}
