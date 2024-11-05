package com.mekongocop.mekongocopserver.dto;

import com.mekongocop.mekongocopserver.dto.VouchersDTOs.VoucherUserDTO;
import lombok.Data;

import java.util.Set;
@Data
public class UserDTO {
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
    private Set<VoucherUserDTO> vouchers;
}
