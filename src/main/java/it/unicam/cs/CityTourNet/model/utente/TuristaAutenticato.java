package it.unicam.cs.CityTourNet.model.utente;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor(force = true)
@DiscriminatorValue("TuristaAutenticato")
public class TuristaAutenticato extends Utente{
    private int punti;
    public TuristaAutenticato(String username, String email, String password) {
        super(username, email, password);
    }
}
