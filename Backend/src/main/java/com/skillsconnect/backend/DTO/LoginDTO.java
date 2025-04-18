package com.skillsconnect.backend.DTO;

public class LoginDTO {
    private  String  wallet;

    public LoginDTO(){}

    public  LoginDTO(String wallet)
    {
        this.wallet = wallet;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }
}
