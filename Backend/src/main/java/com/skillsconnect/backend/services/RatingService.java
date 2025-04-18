package com.skillsconnect.backend.services;


import com.skillsconnect.backend.DTO.RatingDTO;
import com.skillsconnect.backend.models.*;
import com.skillsconnect.backend.repositories.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BidService bidService;

    @Autowired
    private ProjectService projectService;

    public String createRating(RatingDTO ratingDTO)
    {
        User rater = userService.getUserById(ratingDTO.getRater_id());
        User rated = userService.getUserById(ratingDTO.getRated_id());
        Float rating = ratingDTO.getRating();
        String comment = ratingDTO.getComment();
        Project p = projectService.getById(ratingDTO.getProject_id());
        if(p.getStatus() == Projectstatus.FINISHED || p.getStatus() == Projectstatus.REFUNDED)
        {
            User creator = p.getCreator();
            User bidder = bidService.getStatusFromProject(BidStatus.ACCEPTED, ratingDTO.getProject_id()).get(0).getBidder();
            if(!Objects.equals(creator.getId(),rater.getId()) && !Objects.equals(bidder.getId(),rater.getId()))
            {
                return "Not Authorized";
            }
            if(!Objects.equals(creator.getId(),rated.getId()) && !Objects.equals(bidder.getId(),rated.getId()))
            {
                return "Not Authorized";
            }
            Rating r = new Rating(rater,rated,rating,comment);
            ratingRepository.save(r);
            return "Success";
        }
        return "Project is not finished";

    }

    public Float getAverageRating(Long uid)
    {
        Float rating =  ratingRepository.getAverageRatingByUser(uid);
        if (rating == null)
        {
            return 0.0F;
        }
        return rating;
    }

    public List<Rating> getAll()
    {
        return ratingRepository.findAll();
    }

    public List<Rating> getAllByRater(Long id)
    {
        return ratingRepository.getAllRatedByUser(id);
    }

    public List<Rating> getAllByRated(Long id)
    {
        return ratingRepository.getAllRatingByUser(id);
    }
}
