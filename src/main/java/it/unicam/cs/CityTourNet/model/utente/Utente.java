package it.unicam.cs.CityTourNet.model.utente;

import java.util.regex.Pattern;

public abstract class Utente {
    private String username;
    private String email;
    private String password;

    public Utente(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
    public void setPassword(String password) {
        if(password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            this.password = password;
        }
    }

    public void setEmail(String email) {
        if(email.matches("^[\\w\\-.]+@([\\w-]+\\.)+[\\w-]{2,}$")) {
            this.email = email;
        }
    }



}
