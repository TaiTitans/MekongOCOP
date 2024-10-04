package com.mekongocop.mekongocopserver.dto;

import lombok.Data;

@Data
public class AddressDTO {
    private int addressId;
    private String addressDescription;
    private int userId;
}
