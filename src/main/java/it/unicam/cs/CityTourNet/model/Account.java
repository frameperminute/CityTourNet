package it.unicam.cs.CityTourNet.model;

import java.util.regex.Pattern;

public class Account {
    private String username;

    private String password;

    private final AccountType accountType;

    public Account(AccountType accountType, String username, String password) {
        this.accountType = accountType;
        this.username = username;
        if(Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                password)) {
            this.password = password;
        }
    }

    public String getUsername() {
        return username;
    }
    public AccountType getAccountType() {
        return accountType;
    }

    public boolean isPasswordCorrect(String password) {
        return this.password.equals(password);
    }

}
