package com.skillsconnect.backend.repositories;

import com.skillsconnect.backend.models.Project;
import com.skillsconnect.backend.models.Projectstatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE Project p SET p.status = :status WHERE p.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") Projectstatus status);

    @Transactional
    @Modifying
    @Query("UPDATE Project p SET p.name = :name WHERE p.id = :id")
    void updateName(@Param("id") Long id,@Param("name") String name);

    @Transactional
    @Modifying
    @Query("UPDATE Project p SET p.detail = :detail WHERE p.id = :id")
    void updateDetail(@Param("id") Long id,@Param("detail") String detail);

    @Transactional
    @Modifying
    @Query("UPDATE Project p SET p.deadline = :deadline WHERE p.id = :id")
    void updateDeadline(@Param("id") Long id,@Param("deadline") LocalDateTime deadline);

    @Transactional
    @Modifying
    @Query("UPDATE Project p SET p.fileLocation = :url WHERE p.id = :id")
    void updateFileLocation(@Param("id") Long id,@Param("url") String url);

    @Query("SELECT p FROM Project p  WHERE p.status = :status ")
    List<Project> getProjectByStatus(@Param("status") Projectstatus status);

    @Query("SELECT p FROM Project p JOIN p.creator u WHERE p.status = :status AND u.id != :id")
    List<Project> getProjectByStatusAndUID(@Param("status") Projectstatus status,@Param("id") Long id);

    @Query("SELECT p FROM Project p JOIN p.creator u WHERE u.id= :uid")
    List<Project> getByCreatorId(@Param("uid")Long id);

    @Query("SELECT p FROM Project p JOIN p.creator u WHERE u.id= :uid and p.status = 'ONGOING'")
    List<Project> getOngoingByCreator(@Param("uid") Long id);

    @Query("SELECT p FROM Project p JOIN p.creator u WHERE u.id= :uid and p.status = 'CHECKING'")
    List<Project> getCheckingByCreator(@Param("uid") Long id);
}
