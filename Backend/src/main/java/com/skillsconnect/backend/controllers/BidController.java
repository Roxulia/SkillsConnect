package com.skillsconnect.backend.controllers;


import com.skillsconnect.backend.DTO.BidDTO;
import com.skillsconnect.backend.DTO.ProjectDTO;
import com.skillsconnect.backend.models.Bid;
import com.skillsconnect.backend.models.BidStatus;
import com.skillsconnect.backend.models.Project;
import com.skillsconnect.backend.models.User;
import com.skillsconnect.backend.services.BidService;
import com.skillsconnect.backend.services.ProjectService;
import com.skillsconnect.backend.services.RecommendationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("api/bid")
@CrossOrigin(origins = "*")
public class BidController {

    @Autowired
    private BidService bidService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<List<Bid>> getAll()
    {
        return ResponseEntity.ok(bidService.getAllBid());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bid> getById(@PathVariable Long id)
    {
        return ResponseEntity.ok(bidService.getById(id));
    }

    @PutMapping("/project/{id}")
    public ResponseEntity<List<BidDTO>> getByProject(@PathVariable Long id)
    {
        List<Bid> bids=bidService.getBidsOnProject(id);
        List<BidDTO> bidDTOList = new ArrayList<>();
        for(Bid b : bids)
        {
            bidDTOList.add(new BidDTO(b));
        }
        return ResponseEntity.ok(bidDTOList);
    }

    @GetMapping("/{status}")
    public ResponseEntity<List<Bid>> getByStatus(@PathVariable BidStatus status)
    {
        return ResponseEntity.ok(bidService.getByStatus(status));
    }

    @GetMapping("/{status}/{id}")
    public ResponseEntity<List<Bid>> getByStatusFromProject(@PathVariable BidStatus status,@PathVariable Long id)
    {
        return ResponseEntity.ok(bidService.getStatusFromProject(status,id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBid(@RequestBody BidDTO bidDTO, HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        User creator = projectService.getById(bidDTO.getPid()).getCreator() ;
        if(Objects.equals(u.getId(),creator.getId()))
        {
            return ResponseEntity.status(401).body("Cant bid to own project");
        }
        bidDTO.setBidderId(u.getId());
        String response = bidService.createBid(bidDTO);
        if(Objects.equals(response,"Fail"))
        {
            return ResponseEntity.status(401).body("Something went wrong");
        }
        return ResponseEntity.ok("Bidded Successfully");
    }

    @PutMapping("/{id}/amount")
    public ResponseEntity<?> updateAmount(@PathVariable Long id,@RequestParam("amount") Double amount,HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        User bidder = bidService.getById(id).getBidder();
        if(!Objects.equals(u.getId(),bidder.getId()))
        {
            return ResponseEntity.status(401).body("Not Authorized");
        }
        String response = bidService.updateAmount(id,amount);
        if(Objects.equals(response,"Fail"))
        {
            return ResponseEntity.status(401).body("Something went wrong");
        }
        return ResponseEntity.ok("Changed Successfully");
    }

    @PutMapping("/{id}/description")
    public ResponseEntity<?> updateDescripton(@PathVariable Long id,@RequestParam String des,HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        User bidder = bidService.getById(id).getBidder();
        if(!Objects.equals(u.getId(),bidder.getId()))
        {
            return ResponseEntity.status(401).body("Not Authorized");
        }
        bidService.updateDescription(id,des);
        return ResponseEntity.ok("Changed Successfully");
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<?> setAccepted(@PathVariable Long id,@RequestParam String trxHash,HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        Bid bid = bidService.getById(id);
        User creator = bid.getProject().getCreator() ;
        Project p = bid.getProject();
        if(!Objects.equals(u.getId(),creator.getId()))
        {
            return ResponseEntity.status(401).body("Not Authorized");
        }
        String response = bidService.setAccepted(id,p.getId(),trxHash);
        if(Objects.equals(response,"Fail"))
        {
            return ResponseEntity.status(401).body("Something went wrong");
        }
        return ResponseEntity.ok("Accepted Successfully");
    }

    @PutMapping("/{id}/recommended")
    public ResponseEntity<?> getRecommendedBidder(@PathVariable Long id,HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        User creator = projectService.getById(id).getCreator() ;
        if(!Objects.equals(u.getId(),creator.getId()))
        {
            return ResponseEntity.status(401).body("Not Authorized");
        }
        return ResponseEntity.ok("Accepted Successfully");
    }
}
