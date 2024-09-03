package com.mekongocop.mekongocopserver.common;

public class SendOTPRequest {
    public SendOTPRequest(String email, boolean isRegister) {
        this.email = email;
        this.isRegister = isRegister;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isRegister() {
        return isRegister;
    }

    public void setRegister(boolean register) {
        isRegister = register;
    }

    private String email;
    private boolean isRegister;

}
