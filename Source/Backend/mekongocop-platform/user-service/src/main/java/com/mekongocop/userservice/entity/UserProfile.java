package com.mekongocop.userservice.entity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
public class UserProfile {
    public UserProfile(int profile_id, User user_id, String full_name, String bio, LocalDate birthday, char sex, String profile_picture) {
        this.profile_id = profile_id;
        this.user_id = user_id;
        this.full_name = full_name;
        this.bio = bio;
        this.birthday = birthday;
        this.sex = sex;
        this.profile_picture = profile_picture;
    }

    public int getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(int profile_id) {
        this.profile_id = profile_id;
    }

    public User getUser_id() {
        return user_id;
    }

    public void setUser_id(User user_id) {
        this.user_id = user_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int profile_id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user_id;

    private String full_name;
    private String bio;
    private LocalDate birthday;

    @Column(nullable = false, length = 1)
    private char sex;
    private String profile_picture;

    public UserProfile() {

    }
}