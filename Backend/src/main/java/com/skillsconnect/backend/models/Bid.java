package com.skillsconnect.backend.models;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private Double amount;
    private LocalDateTime created_at;
    @Enumerated(EnumType.STRING)
    private BidStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidder_id")
    private User bidder;

    public Bid(){}

    public void setStatus(BidStatus status) {
        this.status = status;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setBidder(User bidder) {
        this.bidder = bidder;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public BidStatus getStatus() {
        return status;
    }

    public Double getAmount() {
        return amount;
    }

    public User getBidder() {
        return bidder;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }
}
