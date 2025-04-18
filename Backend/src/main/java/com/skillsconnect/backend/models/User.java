package com.skillsconnect.backend.models;

import jakarta.persistence.*;

import java.util.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,unique = true)
    private String wallet;
    private String name;
    private String email;
    private String ph_number;
    private String profile_img;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_skills",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills = new HashSet<>();
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Project> projectsCreated = new HashSet<>();
    @OneToMany(mappedBy = "bidder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Bid> projectsBidded = new HashSet<>();
    @OneToMany(mappedBy = "rater",cascade = CascadeType.ALL)
    private List<Rating> ratingGiven = new ArrayList<>();
    @OneToMany(mappedBy = "rated",cascade = CascadeType.ALL)
    private List<Rating> ratingGot = new ArrayList<>();

    public User() {}

    public User(String wallet){
        this.wallet = wallet;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public void setPh_number(String ph_number) {
        this.ph_number = ph_number;
    }

    public void setSkills(Set<Skill> skills) {
        this.skills = skills;
    }

    public void setProjectsBidded(Set<Bid> projectsBidded) {
        this.projectsBidded = projectsBidded;
    }

    public void setProjectsCreated(Set<Project> projectsCreated) {
        this.projectsCreated = projectsCreated;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public String getName() {
        return name;
    }

    public List<Rating> getRatingGiven() {
        return ratingGiven;
    }

    public List<Rating> getRatingGot() {
        return ratingGot;
    }

    public void setRatingGiven(List<Rating> ratingGiven) {
        this.ratingGiven = ratingGiven;
    }

    public void setRatingGot(List<Rating> ratingGot) {
        this.ratingGot = ratingGot;
    }

    public Set<Bid> getProjectsBidded() {
        return projectsBidded;
    }

    public String getEmail() {
        return email;
    }

    public Set<Project> getProjectsCreated() {
        return projectsCreated;
    }

    public Set<Skill> getSkills() {
        return skills;
    }

    public String getPh_number() {
        return ph_number;
    }

    public String getWallet() {
        return wallet;
    }

    public Long getId() {
        return id;
    }

    public String getProfile_img() {
        return profile_img;
    }
}
