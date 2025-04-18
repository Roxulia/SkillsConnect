package com.skillsconnect.backend.models;
import jakarta.persistence.*;

import java.util.*;


@Entity
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @ManyToMany(mappedBy = "skills")
    private Set<User> users = new HashSet<>();

    public Skill(){}

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
