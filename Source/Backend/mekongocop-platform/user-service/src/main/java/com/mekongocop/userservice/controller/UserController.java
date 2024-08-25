package com.mekongocop.userservice.controller;

import com.mekongocop.userservice.common.LoginRequest;
import com.mekongocop.userservice.common.LoginResponse;
import com.mekongocop.userservice.common.StatusResponse;
import com.mekongocop.userservice.dto.UserDTO;
import com.mekongocop.userservice.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<StatusResponse<String>> registerUser(@RequestBody UserDTO userDTO, @RequestParam String otp){
        try{
            userService.registerUser(userDTO, otp);
            return ResponseEntity.ok(new StatusResponse<>("Success", "User registered successfully", null));
        } catch(Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "An unexpected error occurred", null));
        }
    }


    @PatchMapping("/user/email/{id}")
    public ResponseEntity<StatusResponse<UserDTO>> updateEmail(@PathVariable int id, @RequestBody UserDTO userDTO, @RequestParam String otp){
        try{
            userService.updateEmail(userDTO, otp, id);
            return ResponseEntity.ok(new StatusResponse<>("Success", "User updated successfully", userDTO));
        } catch(Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "An unexpected error occurred", null));
        }
    }


    @PatchMapping("/user/password/{id}")
    public ResponseEntity<StatusResponse<String>> resetPassword(@PathVariable int id, @RequestParam String oldPassword, @RequestParam String newPassword){
        try{
            userService.resetPassword(oldPassword, newPassword, id);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Password updated successfully", null));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "An unexpected error occurred", null));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<StatusResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        try{
            userService.login(loginRequest, response);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Login successful", null));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StatusResponse<>("Error", "An unexpected error occurred", null));
        }
    }

}