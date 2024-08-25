package com.mekongocop.userservice.service;
import com.mekongocop.userservice.common.IpAddressInfo;
import com.mekongocop.userservice.entity.User;
import com.mekongocop.userservice.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OTPService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private IpAddressService ipAddressService;

    private final JedisPool jedisPool;
    private final long OTP_EXPIRATION_MINUTES = 3;
    private final int MAX_OTP_SEND_COUNT_PER_IP = 5;

    @Autowired
    public OTPService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public String generateOTP() {
        // Tạo mã OTP ngẫu nhiên, ví dụ: 123456
        return String.format("%06d", new Random().nextInt(900000) + 100000);
    }

    public void saveOTP(String email, String otp) {
        try (Jedis jedis = jedisPool.getResource()) {
            // Lưu mã OTP và thời gian hết hạn vào Redis
            jedis.setex(email, (int) TimeUnit.MINUTES.toSeconds(OTP_EXPIRATION_MINUTES), otp);
        }
    }

    public String getOTPFromRedis(String email) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(email);
        }
    }
    public void sendOTPForRegistration(String email, HttpServletRequest httpRequest) {
        // Kiểm tra email đã đăng ký chưa
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email is already registered");
        }

        // Kiểm tra giới hạn số lần gửi OTP từ IP address
        checkIPAddressLimit(httpRequest);

        // Tạo và lưu OTP
        String otp = generateOTP();
        saveOTP(email, otp);

        // Gửi OTP đến email
        emailService.sendOTPEmail(email, otp);
    }

    public void sendOTPForChangeEmail(String currentEmail, String newEmail, HttpServletRequest httpRequest) {
        // Kiểm tra email hiện tại có đăng ký không
        User user = userRepository.findByEmail(currentEmail);
        if (user == null) {
            throw new IllegalArgumentException("Current email is not registered");
        }

        // Kiểm tra email mới đã đăng ký chưa
        if (userRepository.findByEmail(newEmail) != null) {
            throw new IllegalArgumentException("New email is already registered");
        }

        // Kiểm tra giới hạn số lần gửi OTP từ IP address
        checkIPAddressLimit(httpRequest);

        // Tạo và lưu OTP
        String otp = generateOTP();
        saveOTP(newEmail, otp);

        // Gửi OTP đến email
        emailService.sendOTPEmail(newEmail, otp);
    }

    public void sendOTPForForgotPassword(String email, HttpServletRequest httpRequest) {
        // Kiểm tra email có đăng ký không
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Email is not registered");
        }

        // Kiểm tra giới hạn số lần gửi OTP từ IP address
        checkIPAddressLimit(httpRequest);

        // Tạo và lưu OTP
        String otp = generateOTP();
        saveOTP(email, otp);

        // Gửi OTP đến email
        emailService.sendOTPEmail(email, otp);
    }
    public boolean isOTPValid(String email, String otp) {
        // Kiểm tra xem mã OTP có hợp lệ và chưa hết hạn không
        try (Jedis jedis = jedisPool.getResource()) {
            String storedOtp = jedis.get(email);
            return storedOtp != null && storedOtp.equals(otp);
        }
    }

    public void checkIPAddressLimit(HttpServletRequest request) {
        // Get IP address information
        String ipAddress = getClientIpAddress(request);
        IpAddressInfo ipAddressInfo = ipAddressService.getIpAddressInfo(ipAddress);
        if (ipAddressInfo != null && ipAddressInfo.sendCount >= MAX_OTP_SEND_COUNT_PER_IP) {
            throw new IllegalArgumentException("Maximum number of OTP send requests reached for this IP address");
        }

        // Update IP address information
        int newSendCount = ipAddressInfo != null ? ipAddressInfo.sendCount + 1 : 1;
        String lastSendTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ipAddressService.saveIpAddressInfo(ipAddress, newSendCount, lastSendTime);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }


}