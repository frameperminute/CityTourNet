package it.unicam.cs.CityTourNet.repositories;

import it.unicam.cs.CityTourNet.model.contenuto.Contenuto;
import it.unicam.cs.CityTourNet.model.contenuto.ProdottoGadget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContenutoRepository extends JpaRepository<Contenuto, Long> {
    List<Contenuto> findProdottiGadgetByTipo(String tipo);
    List<Contenuto> findContenutiByUsernameAutore(String username);
}
