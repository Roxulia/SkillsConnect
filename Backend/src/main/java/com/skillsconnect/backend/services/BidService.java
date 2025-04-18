package com.skillsconnect.backend.services;

import com.skillsconnect.backend.DTO.BidDTO;
import com.skillsconnect.backend.models.*;
import com.skillsconnect.backend.repositories.BidRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BidService {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    public List<Bid> getAllBid()
    {
        return bidRepository.findAll();
    }

    public Bid getById(Long id)
    {
        return bidRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Cant find Bid with Id : "+id));
    }

    public List<Project> getProjectsAcceptedByUser(Long id)
    {
        return bidRepository.getProjectAcceptedByUserId(id);
    }

    public String createBid(BidDTO bidDTO)
    {
        Optional<Bid> existing = bidRepository.getBidForUserOfProject(bidDTO.getPid(),bidDTO.getBidderId());
        if(existing.isPresent()){
            return updateBid(existing.get().getId(), bidDTO.getDescription(),bidDTO.getAmount());
        }
        Project p = projectService.getById(bidDTO.getPid());
        User u = userService.getUserById(bidDTO.getBidderId());
        try
        {
            Bid b = new Bid();
            b.setDescription(bidDTO.getDescription());
            b.setAmount(bidDTO.getAmount());
            b.setBidder(u);
            b.setProject(p);
            b.setStatus(BidStatus.PENDING);
            b.setCreated_at(LocalDateTime.now());
            bidRepository.save(b);
            return "Success";
        } catch (Exception e) {
            return "Fail";
        }
    }

    public String updateBid(Long bid,String des,Double amount)
    {

        try
        {
            Bid b = getById(bid);
            b.setAmount(amount);
            b.setDescription(des);
            bidRepository.save(b);
            return "Success";
        } catch (Exception e) {
            return "Fail";
        }

    }

    public String updateAmount(Long id,Double amount)
    {
        try
        {
            bidRepository.updateAmount(id,amount);
            return "Success";
        } catch (Exception e) {
            return "Fail";
        }

    }

    public List<Project> getProjectFinishedByUser(Long id)
    {
        return bidRepository.getProjectFinishedByUserId(id);
    }

    public List<User> getBiddersByProjectId(Long projectId) {
        return bidRepository.findBidderByProjectId(projectId);
    }

    public void updateDescription(Long id,String des)
    {
        bidRepository.updateDescription(id,des);
    }

    public List<Bid> getBidsOnProject(Long id)
    {
        return bidRepository.findByProjectId(id);
    }

    public List<Project> getProjectsBiddedFromUser(Long id)
    {
        return bidRepository.getProjectBiddedByUserId(id);
    }

    public List<Bid> getAcceptedFromProject(Long id)
    {
        return bidRepository.getBidFromProjectByStatus(id,BidStatus.ACCEPTED);
    }

    public List<Bid> getByStatus(BidStatus status)
    {
        return bidRepository.getBidByStatus(status);
    }

    public List<Bid> getStatusFromProject(BidStatus status,Long pid)
    {
        return bidRepository.getBidFromProjectByStatus(pid,status);
    }

    public boolean acceptedBid(Long id) {
        List<Bid> b = bidRepository.getBidFromProjectByStatus(id, BidStatus.ACCEPTED);
        return !b.isEmpty();
    }

    public String setAccepted(Long bid,Long pid,String trxHash)
    {

        if(!acceptedBid(pid) && projectService.isStatus(pid, Projectstatus.HIRING))
        {
            bidRepository.updateBidStatus(bid,BidStatus.ACCEPTED);
            List<Bid> bids = bidRepository.getBidFromProjectByStatus(pid,BidStatus.PENDING);
            for(Bid b : bids)
            {
                bidRepository.updateBidStatus(b.getId(),BidStatus.REJECTED);
            }
            return projectService.setOngoing(pid,getById(bid),trxHash);
        }
        return "Fail";
    }
}
