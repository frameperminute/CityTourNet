package it.unicam.cs.CityTourNet.repositories;

import it.unicam.cs.CityTourNet.model.contenuto.ContenutoMemento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContenutoMementoRepository extends JpaRepository<ContenutoMemento, Long> {
}
