package aut.ap.model;

import aut.ap.framework.IdEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User extends IdEntity {
    @Basic(optional = false)
    private String name;

    @Basic(optional = false)
    @Column(unique = true)
    private String email;

    @Basic(optional = false)
    private String password;

    public User() {
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String getPassword() {
        return password;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{\n" +
                "\tid='" + id + "'\n" +
                "\tname='" + name + "'\n" +
                "\temail='" + email + "'\n" +
                '}';
    }
}

