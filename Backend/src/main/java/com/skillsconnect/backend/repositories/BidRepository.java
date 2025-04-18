package com.skillsconnect.backend.repositories;

import com.skillsconnect.backend.models.Bid;
import com.skillsconnect.backend.models.BidStatus;
import com.skillsconnect.backend.models.Project;
import com.skillsconnect.backend.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    @Query("SELECT b FROM Bid b JOIN b.project p WHERE p.id = :pid ")
    List<Bid> findByProjectId(@Param("pid") Long id);

    @Query("SELECT u FROM Bid b JOIN b.project p JOIN b.bidder u WHERE p.id = :pid ")
    List<User> findBidderByProjectId(@Param("pid") Long id);

    List<Bid> findByProject(Project project);

    @Query("SELECT b.project FROM Bid b JOIN b.bidder u WHERE u.id = :uid")
    List<Project> getProjectBiddedByUserId(@Param("uid") Long id);

    @Query("SELECT b.project FROM Bid b JOIN b.bidder u WHERE u.id = :uid AND b.status ='ACCEPTED'")
    List<Project> getProjectAcceptedByUserId(@Param("uid") Long id);

    @Query("SELECT p FROM Bid b JOIN b.bidder u JOIN b.project p WHERE u.id = :uid AND b.status = 'ACCEPTED' AND p.status = 'FINISHED'")
    List<Project> getProjectFinishedByUserId(@Param("uid") Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Bid b SET b.amount = :amount WHERE b.id = :id")
    void updateAmount( @Param("id") Long id,@Param("amount") Double amount);

    @Transactional
    @Modifying
    @Query("UPDATE Bid b SET b.status = :status WHERE b.id = :id")
    void updateBidStatus( @Param("id") Long id,@Param("status") BidStatus status);

    @Query("SELECT b FROM Bid b WHERE b.status = :status")
    List<Bid> getBidByStatus(@Param("status") BidStatus status);

    @Query("SELECT b FROM Bid b JOIN b.project p WHERE p.id = :pid AND b.status = :status")
    List<Bid> getBidFromProjectByStatus(@Param("pid") Long id,@Param("status") BidStatus status);

    @Query("SELECT b FROM Bid b JOIN b.project p JOIN b.bidder u WHERE p.id = :pid AND u.id = :uid")
    Optional<Bid> getBidForUserOfProject(@Param("pid")Long pid,@Param("uid")Long uid);

    @Transactional
    @Modifying
    @Query("UPDATE Bid b SET b.description = :des WHERE b.id = :id")
    void updateDescription(@Param("id")Long id,@Param("des") String des);
}
