package it.unicam.cs.CityTourNet.model.contenuto;

public abstract class Contenuto {
    private String nome;
    private String descrizione;
    private final String usernameAutore;

    public Contenuto(String nome, String descrizione, String usernameAutore) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.usernameAutore = usernameAutore;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getUsernameAutore() {
        return usernameAutore;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }


}
