package com.example.delgado_rodrigue_mobile_app_dev_project.data.databases;

public class User {
    private int id;
    private String name;
    private String lastname;

    public User(int id, String name, String lastname) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
    }

    public int getUserID() { return id; }
    public void setUserID(int id) { this.id = id; }

    public String getUserName() { return name; }
    public void setUserName(String name) { this.name = name; }

    public String getUserLastname() { return lastname; }
    public void setUserLastname(String lastname) { this.lastname = lastname; }

    public String getDisplayName() { return name + " " + lastname; }
}
