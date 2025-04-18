package com.skillsconnect.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AdminService {

    private final String adminWallet = "0xF575928f07AEf422ac74213904d7afab12A2ea9C";

    public boolean isAdmin(String wallet)
    {
        return Objects.equals(wallet,adminWallet);
    }

    public String getAdminWallet() {
        return adminWallet;
    }
}
