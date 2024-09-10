package com.mekongocop.mekongocopserver.repository;

public interface EmailSender {
    void sendOTPEmail(String to, String otp);
}
