package com.csye6225.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;



@Entity
@JsonIgnoreProperties(value = {"id"},
        allowGetters = true)
public class User {


    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    private String password;
    @Column(unique = true)
    private String email;


    public User() {}

    public User(String password, String email) {

        this.setPassword(password);
        this.setEmail(email);
    }

    public String getEmail() {
        return email;
    }

    public long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

   
}