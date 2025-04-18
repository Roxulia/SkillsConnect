package com.skillsconnect.backend.controllers;


import com.skillsconnect.backend.DTO.RatingDTO;
import com.skillsconnect.backend.models.Rating;
import com.skillsconnect.backend.models.User;
import com.skillsconnect.backend.services.AdminService;
import com.skillsconnect.backend.services.RatingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("api/rating")
@CrossOrigin(origins = "*")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private AdminService adminService;

    @GetMapping
    public ResponseEntity<?> getAll(HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null || !adminService.isAdmin(u.getWallet()))
        {
            return ResponseEntity.status(401).body("Must be Admin");
        }
        return ResponseEntity.ok(ratingService.getAll());
    }

    @GetMapping("/{id}/rated")
    public ResponseEntity<?> getAllByRated(@PathVariable Long id,HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged In");
        }
        return ResponseEntity.ok(ratingService.getAllByRated(id));
    }

    @GetMapping("/{id}/rater")
    public ResponseEntity<?> getAllByRater(@PathVariable Long id,HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged In");
        }
        if(Objects.equals(u.getId(),id) || adminService.isAdmin(u.getWallet()))
        {
            return ResponseEntity.ok(ratingService.getAllByRater(id));
        }
        return ResponseEntity.status(401).body("Not Authorized");
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRating(@RequestBody RatingDTO ratingDTO,HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged In");
        }
        ratingDTO.setRater_id(u.getId());
        String response = ratingService.createRating(ratingDTO);
        if(Objects.equals(response,"Success"))
        {
            return ResponseEntity.ok("Successfully rated");
        }
        return ResponseEntity.status(401).body(response);
    }
}
