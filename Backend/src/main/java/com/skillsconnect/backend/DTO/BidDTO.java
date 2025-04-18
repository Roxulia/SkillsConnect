package com.skillsconnect.backend.DTO;

import com.skillsconnect.backend.models.Bid;
import com.skillsconnect.backend.models.BidStatus;

public class BidDTO {

    private Long id;
    private String description;
    private Double amount;
    private String status;
    private Long pid;
    private Long bidderId;
    private String bidder;
    private String bidderWallet;

    public BidDTO(){}

    public BidDTO(Bid bid){
        this.description = bid.getDescription();
        this.bidder = bid.getBidder().getName();
        this.bidderId = bid.getBidder().getId();
        this.status = bid.getStatus().toString();
        this.amount = bid.getAmount();
        this.bidderWallet = bid.getBidder().getWallet();
        this.id = bid.getId();
        this.pid = bid.getProject().getId();

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBidderWallet() {
        return bidderWallet;
    }

    public void setBidderWallet(String bidderWallet) {
        this.bidderWallet = bidderWallet;
    }

    public String getDescription() {
        return description;
    }

    public Double getAmount() {
        return amount;
    }

    public Long getBidderId() {
        return bidderId;
    }

    public String getBidder() {
        return bidder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setBidder(String bidder) {
        this.bidder = bidder;
    }

    public void setBidderId(Long bidderId) {
        this.bidderId = bidderId;
    }

    public Long getPid() {
        return pid;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public void setAmount(Double amount) {
        this.amount = amount;
    }



    public void setPid(Long pid) {
        this.pid = pid;
    }
}
