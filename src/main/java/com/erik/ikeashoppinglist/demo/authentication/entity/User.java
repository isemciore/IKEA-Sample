package com.erik.ikeashoppinglist.demo.authentication.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(min=3, max=255, message = "username between 3 and 255 letters")
    @Column(unique = true, nullable = false)
    private String username;

    @Size(min=1, message="Password mus tbe at least 1 letter")
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    List<Role> roles;

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
