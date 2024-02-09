package it.unicam.cs.CityTourNet.model;

import java.util.regex.Pattern;

public class Account {
    private String username;

    private String password;

    public Account(String username, String password) {
        this.username = username;
        if(Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                password)) {
            this.password = password;
        }
    }

    public String getUsername() {
        return username;
    }

    public void changeUsername(String username) {
        this.username = username;
    }

    public void changePassword(String oldPassword, String newPassword) {
        if(this.password.equals(oldPassword) && Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)" +
                                "(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", newPassword)) {
            this.password = newPassword;
        }
    }

}
