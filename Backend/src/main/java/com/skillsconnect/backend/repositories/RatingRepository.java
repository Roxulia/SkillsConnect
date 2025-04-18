package com.skillsconnect.backend.repositories;

import com.skillsconnect.backend.models.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating,Long> {
    @Query("SELECT AVG(r.rating) FROM Rating r JOIN r.rated u WHERE u.id = :uid")
    Float getAverageRatingByUser(@Param("uid") Long uid);

    @Query("SELECT r FROM Rating r JOIN r.rated u WHERE u.id = :uid")
    List<Rating> getAllRatingByUser(@Param("uid") Long uid);

    @Query("SELECT r FROM Rating r JOIN r.rater u WHERE u.id = :uid")
    List<Rating> getAllRatedByUser(@Param("uid") Long uid);
}
