package com.skillsconnect.backend.models;
import jakarta.persistence.*;
import java.util.*;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String TrxHash;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    @OneToOne(mappedBy = "transaction")
    private Project project;

    public Transaction(){}

    public void setTrxHash(String trxHash) {
        this.TrxHash = trxHash;
    }

    public String getTrxHash() {
        return TrxHash;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public TransactionStatus getStatus() {
        return status;
    }
}
