package com.mekongocop.userservice.dto;

import java.util.Set;

public class UserDTO {
    public Integer user_id;
    public String username;
    public String email;
    public String password;
    public String oauth_provider;
    public String oauth_id;
    public Set<String> roles;
}