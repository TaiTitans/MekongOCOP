package com.mekongocop.authservice.repository;

public interface EmailSender {
    void sendOTPEmail(String to, String otp);
}
