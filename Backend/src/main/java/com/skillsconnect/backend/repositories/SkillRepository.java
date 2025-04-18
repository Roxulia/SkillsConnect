package com.skillsconnect.backend.repositories;

import com.skillsconnect.backend.models.Skill;
import com.skillsconnect.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface SkillRepository extends JpaRepository<Skill,Long> {
    @Query("SELECT s.name FROM Skill s JOIN s.users u WHERE u.id = :uid")
    List<String> getSkillsByUserId(@Param("uid") Long id);

    @Query("SELECT s.users FROM Skill s WHERE s.id = :sid")
    List<User> getUsersBySkillId(@Param("sid") Long id);

    @Query("SELECT s.name FROM Skill s")
    List<String> getAllSkillName();
}
