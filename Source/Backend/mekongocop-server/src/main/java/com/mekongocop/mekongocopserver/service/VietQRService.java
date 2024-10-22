package com.mekongocop.mekongocopserver.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class VietQRService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${API_VIETQR}")
    private String apiKey;
    @Value("${CLIENT_VIETQR}")
    private String clientKey;

    private static final String API_URL = "https://api.vietqr.io/v2/generate";

    public String processVietQRPayment(BigDecimal totalPrice, String accountName, String accountNumber, String addInfo) {
        try {
            // Tạo headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("X-Client-Type", "WEB");
            headers.set("X-Client-Version", "1.0.0");
            headers.set("X-Client-Id", clientKey);

            // Tạo body cho yêu cầu POST
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("accountNo", accountNumber);
            requestBody.put("accountName", accountName);
            requestBody.put("acqId", "970422");
            requestBody.put("amount", totalPrice);
            requestBody.put("addInfo", addInfo);
            requestBody.put("template", "X13NcbJ");
            requestBody.put("embedImage", true);

            // Đóng gói body và headers
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Gửi yêu cầu tới API VietQR
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

            // Xử lý phản hồi
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode responseJson = new ObjectMapper().readTree(response.getBody());
                String qrCodeUrl = responseJson.path("data").path("qrDataURL").asText();
                System.out.println("VietQR API Response: " + response.getBody());
                return qrCodeUrl;
            } else {
                return null;
            }
        } catch (Exception e) {
            System.err.println("VietQR payment failed: " + e.getMessage());
            return null;
        }
    }
}