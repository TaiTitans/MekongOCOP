package com.mekongocop.userservice.dto;

import jakarta.validation.constraints.Size;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;

public class UserProfileDTO {
    public int profileId;
    public int user_id;
    public String full_name;

    @NotNull
    public LocalDate birthday;
    @Size(max=200, message = "Max length 200")
    public String bio;

}