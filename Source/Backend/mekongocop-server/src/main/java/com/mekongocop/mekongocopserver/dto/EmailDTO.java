package com.mekongocop.mekongocopserver.dto;

public class EmailDTO {
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EmailDTO(String email) {
        this.email = email;
    }

    private String email;

}