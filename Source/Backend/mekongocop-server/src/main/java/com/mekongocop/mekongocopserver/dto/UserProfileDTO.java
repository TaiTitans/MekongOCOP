package com.mekongocop.mekongocopserver.dto;

import jakarta.validation.constraints.Size;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public class UserProfileDTO {
    public int profileId;
    public int user_id;
    public String full_name;

    @NotNull
    public LocalDate birthday;
    @NotNull
    public char sex;
    @Size(max=200, message = "Max length 200")
    public String bio;

}
