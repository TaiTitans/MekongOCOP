package com.mekongocop.userservice.repository;

public interface EmailSender {
    void sendOTPEmail(String to, String otp);
}