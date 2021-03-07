package com.example.delgado_rodrigue_mobile_app_dev_project.data.databases;

public class Account {
    private int id;
    private String name;
    private double amount;
    private String iban;
    private String currency;

    public Account(int id, String name, double amount, String iban, String currency) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.iban = iban;
        this.currency = currency;
    }

    public int getAccountID() { return id; }
    public void setAccountID(int id) { this.id = id; }

    public String getAccountName() { return name; }
    public void setAccountName(String name) { this.name = name; }

    public double getAccountAmount() { return amount; }
    public void setAccountAmount(double amount) { this.amount = amount; }

    public String getAccountIBAN() { return iban; }
    public void setAccountIBAN(String iban) { this.iban = iban; }

    public String getAccountCurrency() { return currency; }
    public void setAccountCurrency(String currency) { this.currency = currency; }

    public String getDisplayableString() {
        return name + " (ID:" + String.valueOf(id) + ")\n" +
            "IBAN: " + iban + "\n" +
            "Balance: " + String.valueOf(amount) + " " + currency + "\n";
    }
}