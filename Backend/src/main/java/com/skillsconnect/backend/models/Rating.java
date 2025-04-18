package com.skillsconnect.backend.models;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rater_id")
    private User rater;

    @ManyToOne
    @JoinColumn(name = "rated_id")
    private User rated;

    private Float rating;
    private String comments;
    private LocalDateTime timestamp;

    public Rating() {}

    public Rating(User rater,User rated,Float rating,String comment)
    {
        this.rater = rater;
        this.rated = rated;
        this.rating = rating;
        this.comments = comment;
        this.timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public User getRated() {
        return rated;
    }

    public User getRater() {
        return rater;
    }

    public Float getRating() {
        return rating;
    }

    public Long getId() {
        return id;
    }

    public String getComments() {
        return comments;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setRated(User rated) {
        this.rated = rated;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public void setRater(User rater) {
        this.rater = rater;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
