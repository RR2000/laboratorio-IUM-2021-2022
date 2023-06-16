package com.universita.laboratorioium.utils;

import java.io.Serializable;

public class User implements Serializable {
    String username;
    String name;
    String surname;
    Boolean admin;

    public User(String username, String name, String surname, Boolean admin) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.admin = admin;
    }

    public String getUsername(){
        return username;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Boolean isAdmin() {
        return admin;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", admin=" + admin +
                '}';
    }
}
