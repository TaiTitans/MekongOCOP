package com.mekongocop.mekongocopserver.controller;

import com.mekongocop.mekongocopserver.dto.ChatSessionsDTO;
import com.mekongocop.mekongocopserver.entity.ChatSessions;
import com.mekongocop.mekongocopserver.entity.Store;
import com.mekongocop.mekongocopserver.repository.StoreRepository;
import com.mekongocop.mekongocopserver.service.ChatSessionsService;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import com.mekongocop.mekongocopserver.util.TokenExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class ChatSessionsController {

    @Autowired
    private ChatSessionsService chatSessionsService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private StoreRepository storeRepository;

    @PostMapping("common/chatSessions/create")
    public ResponseEntity<ChatSessionsDTO> createChatSession(@RequestParam int user_id, @RequestParam int store_id) {
        // Tạo phiên chat
        ChatSessions session = chatSessionsService.createSession(user_id, store_id);

        // Chuyển đổi từ Entity sang DTO
        ChatSessionsDTO sessionDTO = chatSessionsService.convertToDTO(session);

        // Trả về DTO dưới dạng JSON với mã trạng thái 201 Created
        return new ResponseEntity<>(sessionDTO, HttpStatus.CREATED);
    }

    @GetMapping("/user/chatSessions")
    public ResponseEntity<List<ChatSessionsDTO>> getUserChatSessions(@RequestHeader("Authorization") String token) {
        // Extract userId from the token
        String validToken = TokenExtractor.extractToken(token);
        int userId = jwtTokenProvider.getUserIdFromToken(validToken);

        // Retrieve chat sessions for the user and convert to DTO
        List<ChatSessionsDTO> sessions = chatSessionsService.findByUserId(userId);

        return new ResponseEntity<>(sessions, HttpStatus.OK);
    }

    @GetMapping("/seller/chatSessions/store")
    public ResponseEntity<List<ChatSessionsDTO>> getStoreChatSessions(@RequestHeader("Authorization") String token) {
        // Extract userId from the token
        String validToken = TokenExtractor.extractToken(token);
        int userId = jwtTokenProvider.getUserIdFromToken(validToken);

        // Find the store using the userId
        Optional<Store> store = storeRepository.findByUserId(userId);

        // Check if the store is present
        if (!store.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Return 404 if no store is found
        }

        // Retrieve chat sessions for the store and convert to DTO
        List<ChatSessionsDTO> sessions = chatSessionsService.findByStoreId(store.get().getStore_id());

        return new ResponseEntity<>(sessions, HttpStatus.OK);
    }
}
