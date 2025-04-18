package com.skillsconnect.backend.repositories;

import com.skillsconnect.backend.models.Project;
import com.skillsconnect.backend.models.Transaction;
import com.skillsconnect.backend.models.TransactionStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Transaction t SET t.TrxHash = :trx WHERE t.id= :id")
    void updateTrxHash( @Param("id") Long id,@Param("trx")String trx);

    @Transactional
    @Modifying
    @Query("UPDATE Transaction t SET t.project = :p WHERE t.id= :id")
    void updateProject( @Param("id") Long id,@Param("p")Project p);

    @Transactional
    @Modifying
    @Query("UPDATE Transaction t SET t.status = :status WHERE t.id= :id")
    void updateStatus( @Param("id") Long id,@Param("status") TransactionStatus status);

    @Query("SELECT t FROM Transaction t WHERE t.status = :status")
    List<Transaction> getByStatus(@Param("status") TransactionStatus status);

    @Query("SELECT p.transaction FROM Project p WHERE p.id = :pid")
    Transaction getByProjectId(@Param("pid") Long id);
}
