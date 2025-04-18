package com.skillsconnect.backend.models;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String detail;
    private LocalDateTime deadline;
    private String fileLocation;
    @Enumerated(EnumType.STRING)
    private Projectstatus status;
    @Enumerated(EnumType.STRING)
    private FileStatus fileStatus;
    @Column(updatable = false)
    private LocalDateTime created_at;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;
    private String categories;

    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Set<Bid> bids = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    public Project(){}

    public void setFileStatus(FileStatus fileStatus) {
        this.fileStatus = fileStatus;
    }

    public FileStatus getFileStatus() {
        return fileStatus;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public void setStatus(Projectstatus status) {
        this.status = status;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public void setBids(Set<Bid> bids) {
        this.bids = bids;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public Projectstatus getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public User getCreator() {
        return creator;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public String getCategories() {
        return categories;
    }

    public Set<Bid> getBids() {
        return bids;
    }
}
