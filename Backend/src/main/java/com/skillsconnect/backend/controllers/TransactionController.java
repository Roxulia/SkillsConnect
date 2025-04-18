package com.skillsconnect.backend.controllers;

import com.skillsconnect.backend.models.Transaction;
import com.skillsconnect.backend.models.TransactionStatus;
import com.skillsconnect.backend.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/transaction")
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<Transaction>> getAll()
    {
        return ResponseEntity.ok(transactionService.getAllTransaction());
    }

    @GetMapping("/project/{id}")
    public ResponseEntity<Transaction> getByProject(@PathVariable Long id)
    {
        return ResponseEntity.ok(transactionService.getFromProject(id));
    }

    @GetMapping("/{status}")
    public ResponseEntity<List<Transaction>> getByStatus(@PathVariable TransactionStatus status)
    {
        return ResponseEntity.ok(transactionService.getTransactionsByStatus(status));
    }
}
