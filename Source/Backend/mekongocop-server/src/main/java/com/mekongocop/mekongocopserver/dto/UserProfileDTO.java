package com.mekongocop.mekongocopserver.dto;

import jakarta.validation.constraints.Size;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public class UserProfileDTO {
    public int profileId;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int user_id;
    public String full_name;

    @NotNull
    public LocalDate birthday;
    @NotNull
    public char sex;
    @Size(max=200, message = "Max length 200")
    public String bio;

}
