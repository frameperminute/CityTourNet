package it.unicam.cs.CityTourNet.model.utente;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_utente", discriminatorType = DiscriminatorType.STRING)
@NoArgsConstructor(force = true)
@Getter
public abstract class Utente {
    @Id
    private String username;
    private String email;
    private String password;
    @Column(name = "tipo_utente", insertable = false, updatable = false)
    protected String tipoUtente;
    @Setter
    private boolean isLoggedIn;

    public Utente(String username, String email, String password) {
        this.username = username;
        this.setEmail(email);
        this.setPassword(password);
        this.isLoggedIn = false;
    }

    public void setPassword(String password) {
        if(password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!#$%&'(),./:;?@_])" +
                "[A-Za-z\\d!#$%&'(),./:;?@_]{8,}$")) {
            this.password = password;
        }
    }

    public void setEmail(String email) {
        if(email.matches("^[\\w\\-.]+@([\\w-]+\\.)+[\\w-]{2,}$")) {
            this.email = email;
        }
    }

}
