package com.mekongocop.authservice.dto;
import java.util.Set;

public class UserDTO {
    public UserDTO(Integer user_id, String username, String email, String password, String oauth_provider, String oauth_id, Set<String> roles) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.oauth_provider = oauth_provider;
        this.oauth_id = oauth_id;
        this.roles = roles;
    }
public UserDTO(){

}
    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOauth_provider() {
        return oauth_provider;
    }

    public void setOauth_provider(String oauth_provider) {
        this.oauth_provider = oauth_provider;
    }

    public String getOauth_id() {
        return oauth_id;
    }

    public void setOauth_id(String oauth_id) {
        this.oauth_id = oauth_id;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    private Integer user_id;
    private String username;
    private String email;
    private String password;
    private String oauth_provider;
    private String oauth_id;
    private Set<String> roles;
}
