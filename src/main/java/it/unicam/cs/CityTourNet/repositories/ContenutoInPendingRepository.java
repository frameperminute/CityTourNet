package it.unicam.cs.CityTourNet.repositories;

import it.unicam.cs.CityTourNet.model.contenuto.Contenuto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContenutoInPendingRepository extends JpaRepository<Contenuto, Long> {
}