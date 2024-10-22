package com.mekongocop.mekongocopserver.service;

import com.mekongocop.mekongocopserver.dto.ChatAttachmentDTO;
import com.mekongocop.mekongocopserver.entity.ChatAttachment;
import com.mekongocop.mekongocopserver.entity.ChatMessage;
import com.mekongocop.mekongocopserver.repository.ChatAttachmentRepository;
import com.mekongocop.mekongocopserver.repository.ChatMessageRepository;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class ChatAttachmentService {
    @Autowired
    private ChatAttachmentRepository chatAttachmentRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    public ChatAttachment convertToEntity(ChatAttachmentDTO attachmentDTO, ChatMessage chatMessage) {
        ChatAttachment attachment = new ChatAttachment();
        attachment.setAttachment_id(attachmentDTO.getAttachment_id());  // Nếu ID không tự động được sinh ra
        attachment.setChatMessage(chatMessage);  // Liên kết file đính kèm với tin nhắn
        attachment.setFile_url(attachmentDTO.getFile_url());  // Đường dẫn tới file đính kèm// Loại file (ảnh, video, v.v.)
        return attachment;
    }

    public ChatAttachmentDTO convertToDTO(ChatAttachment attachment) {
        ChatAttachmentDTO attachmentDTO = new ChatAttachmentDTO();
        attachmentDTO.setAttachment_id(attachment.getAttachment_id());
        attachmentDTO.setMessage_id(attachment.getChatMessage().getMessage_id());
        attachmentDTO.setFile_url(attachment.getFile_url());
        return attachmentDTO;
    }

    @Async
    @Transactional// Đánh dấu phương thức là bất đồng bộ
    public CompletableFuture<ChatAttachmentDTO> uploadMessageImage(MultipartFile file, int messageId, String token) {
        try {
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            // Upload ảnh lên Cloudinary
            Map<String, Object> uploadResult = cloudinaryService.uploadMessageImage(file, userId, messageId);

            // Lấy URL của hình ảnh sau khi upload
            String imageUrl = (String) uploadResult.get("secure_url");

            // Lấy tin nhắn liên quan từ DB
            ChatMessage chatMessage = chatMessageRepository.findById(messageId)
                    .orElseThrow(() -> new RuntimeException("Message not found"));

            // Tạo và lưu ChatAttachment vào DB
            ChatAttachment chatAttachment = new ChatAttachment();
            chatAttachment.setChatMessage(chatMessage);
            chatAttachment.setFile_url(imageUrl);
            chatAttachmentRepository.save(chatAttachment);

            // Chuyển đổi ChatAttachment thành ChatAttachmentDTO
            ChatAttachmentDTO attachmentDto = new ChatAttachmentDTO();
            attachmentDto.setAttachment_id(chatAttachment.getAttachment_id());
            attachmentDto.setMessage_id(messageId);
            attachmentDto.setFile_url(imageUrl);

            // Trả về kết quả bất đồng bộ
            return CompletableFuture.completedFuture(attachmentDto);
        } catch (IOException e) {
            throw new RuntimeException("Error uploading image", e);
        }
    }


    // Logic lấy tệp đính kèm theo ID
    public ChatAttachmentDTO getAttachmentById(int attachmentId) {
        ChatAttachment attachment = chatAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        // Chuyển đổi ChatAttachment thành ChatAttachmentDTO
        ChatAttachmentDTO attachmentDto = new ChatAttachmentDTO();
        attachmentDto.setAttachment_id(attachment.getAttachment_id());
        attachmentDto.setMessage_id(attachment.getChatMessage().getMessage_id());
        attachmentDto.setFile_url(attachment.getFile_url());

        return attachmentDto;
    }

}