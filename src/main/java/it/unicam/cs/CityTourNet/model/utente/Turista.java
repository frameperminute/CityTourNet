package it.unicam.cs.CityTourNet.model.utente;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor(force = true)
@DiscriminatorValue("Turista")
public class Turista extends Utente implements Acquirente{
    private int punti;
    public Turista(String username, String email, String password) {
        super(username, email, password);
        this.punti = 0;
    }
}
