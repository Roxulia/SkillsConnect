package com.skillsconnect.backend.repositories;


import com.skillsconnect.backend.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByWallet(String wallet);
    boolean existsByWallet(String wallet);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.name = :name WHERE u.id = :id")
    void updateName(@Param("id") Long id,@Param("name") String name);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.email = :email WHERE u.id = :id")
    void updateEmail(@Param("id") Long id,@Param("email") String email);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.ph_number = :ph WHERE u.id = :id")
    void updatePh(@Param("id") Long id,@Param("ph") String ph);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.profile_img = :url WHERE u.id = :id")
    void updateProfileImage(@Param("id") Long id,@Param("url") String url);

}
