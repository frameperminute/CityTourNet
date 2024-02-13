package it.unicam.cs.CityTourNet.model;

import java.util.regex.Pattern;

public class Account {
    private final String username;

    private final String password;

    private final AccountType accountType;

    public Account(AccountType accountType, String username, String password) {
        this.accountType = accountType;
        this.username = username;
        if(Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                password)) {
            this.password = password;
        } else {
            this.password = "";
        }
    }

    public String getUsername(String password) {
        if(this.isPasswordCorrect(password)) {
            return username;
        }
        return null;
    }
    public AccountType getAccountType() {
        return accountType;
    }

    public boolean isPasswordCorrect(String password) {
        return this.password.equals(password);
    }

}
