package com.mekongocop.mekongocopserver.controller;

import com.mekongocop.mekongocopserver.dto.ChatAttachmentDTO;
import com.mekongocop.mekongocopserver.service.ChatAttachmentService;
import com.mekongocop.mekongocopserver.util.TokenExtractor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/common/chatAttachment")
public class ChatAttachmentController {
    @Autowired
    private ChatAttachmentService chatAttachmentService;


    @PostMapping("/upload/{messageId}")
    public CompletableFuture<ResponseEntity<ChatAttachmentDTO>> uploadMessageImage(
            @RequestPart("file") MultipartFile file,
            @PathVariable("messageId") int messageId, @RequestHeader("Authorization") String token) {
String validToken = TokenExtractor.extractToken(token);
        return chatAttachmentService.uploadMessageImage(file, messageId, validToken)
                .thenApply(attachmentDto -> new ResponseEntity<>(attachmentDto, HttpStatus.OK))
                .exceptionally(ex -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }


}
