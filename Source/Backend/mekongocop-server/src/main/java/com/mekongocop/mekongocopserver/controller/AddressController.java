package com.mekongocop.mekongocopserver.controller;

import com.mekongocop.mekongocopserver.common.StatusResponse;
import com.mekongocop.mekongocopserver.dto.AddressDTO;
import com.mekongocop.mekongocopserver.service.AddressService;
import com.mekongocop.mekongocopserver.service.UserService;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import com.mekongocop.mekongocopserver.util.TokenExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/common")
public class AddressController {
    @Autowired
    private AddressService addressService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/address")
    public ResponseEntity<StatusResponse<AddressDTO>> createAddress(@RequestBody AddressDTO addressDTO, @RequestHeader("Authorization") String token) {
        try{
            String validToken = TokenExtractor.extractToken(token);
            if(!jwtTokenProvider.validateToken(token)){
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token is error", null));
            }
            AddressDTO created = addressService.addAddress(addressDTO, validToken);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Address created", created));

        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new StatusResponse<>("Error", e.getMessage(), null));
        } catch (Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "An unexpected error occurred.", null));
        }
    }


    @PatchMapping("/address/{id}")
    public ResponseEntity<StatusResponse<AddressDTO>> updateAddress(@PathVariable int id, @RequestBody AddressDTO addressDTO, @RequestHeader("Authorization") String token) {
        try {
                String validToken = TokenExtractor.extractToken(token);
                if(!jwtTokenProvider.validateToken(token)){
                    return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token is error", null));
                }
                addressService.updateAddress(id,addressDTO ,validToken);
                return ResponseEntity.ok(new StatusResponse<>("Success", "Address modified", null));

        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new StatusResponse<>("Error", e.getMessage(), null));
        } catch (Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "An unexpected error occurred.", null));
        }
    }


    @DeleteMapping("/address/{id}")
    public ResponseEntity<StatusResponse<Void>> deleteAddress(@PathVariable int id, @RequestHeader("Authorization") String token) {
        try{
            String validToken = TokenExtractor.extractToken(token);
            if(!jwtTokenProvider.validateToken(token)){
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token is error", null));
            }
            addressService.deleteAddress(validToken, id);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Address deleted", null));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", e.getMessage(), null));
        }
    }


    @GetMapping("/address")
    public ResponseEntity<StatusResponse<List<AddressDTO>>> getAddress(@RequestHeader("Authorization") String token) {
        try{
            String validToken = TokenExtractor.extractToken(token);
            if(!jwtTokenProvider.validateToken(token)){
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token is error", null));
            }
            List<AddressDTO> addressDTOS = addressService.getAllAddresses(validToken);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Address List", addressDTOS));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", e.getMessage(), null));
        }
    }



}
