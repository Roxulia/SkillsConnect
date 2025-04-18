package com.skillsconnect.backend.services;


import com.skillsconnect.backend.models.Bid;
import com.skillsconnect.backend.models.Project;
import com.skillsconnect.backend.models.Transaction;
import com.skillsconnect.backend.models.TransactionStatus;
import com.skillsconnect.backend.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getAllTransaction(){
        return transactionRepository.findAll();
    }

    public List<Transaction> getTransactionsByStatus(TransactionStatus status)
    {
        return transactionRepository.getByStatus(status);
    }

    public Transaction getFromProject(Long id)
    {
        return transactionRepository.getByProjectId(id);
    }

    //mock smart contract function
    public Transaction createTransaction(Project p, Bid bid,String trxHash)
    {
        Transaction t = new Transaction();
        t.setProject(p);
        t.setStatus(TransactionStatus.PENDING);
        t.setTrxHash(trxHash);
        return transactionRepository.save(t);

    }

    public void updateStatus(Long id,TransactionStatus status)
    {
        transactionRepository.updateStatus(id,status);
    }

    public String refundTransaction(Long id)
    {
        Transaction t = transactionRepository.getByProjectId(id);
        System.out.println(t.getTrxHash());
        t.setStatus(TransactionStatus.REFUNDED);
        transactionRepository.save(t);
        return "Success";
    }

    public String succeedTransaction(Long id)
    {
        Transaction t = transactionRepository.getByProjectId(id);
        System.out.println(t.getTrxHash());
        t.setStatus(TransactionStatus.SUCCEED);
        transactionRepository.save(t);
        return "Success";

    }

}
