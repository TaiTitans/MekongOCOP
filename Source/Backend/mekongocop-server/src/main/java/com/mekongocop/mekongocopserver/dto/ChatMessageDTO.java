package com.mekongocop.mekongocopserver.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

public class ChatMessageDTO {
    public ChatMessageDTO(int message_id, int session_id, int store_id, int sender_id, String message_content, Timestamp created_at, boolean is_read) {
        this.message_id = message_id;
        this.session_id = session_id;
        this.store_id = store_id;
        this.sender_id = sender_id;
        this.message_content = message_content;
        this.created_at = created_at;
        this.is_read = is_read;
    }

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public int getSession_id() {
        return session_id;
    }

    public void setSession_id(int session_id) {
        this.session_id = session_id;
    }

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public String getMessage_content() {
        return message_content;
    }

    public void setMessage_content(String message_content) {
        this.message_content = message_content;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public boolean isIs_read() {
        return is_read;
    }

    public void setIs_read(boolean is_read) {
        this.is_read = is_read;
    }

    private int message_id;
    private int session_id;
    private int store_id;
    private int sender_id;    // ID của người gửi (user hoặc seller)
    private String message_content;
    private Timestamp created_at;  // Thời gian gửi tin nhắn
    private boolean is_read;    // Trạng thái đã đọc của tin nhắn

    public ChatMessageDTO(){}

}
