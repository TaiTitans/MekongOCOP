package com.mekongocop.mekongocopserver.controller;

import com.mekongocop.mekongocopserver.common.LoginRequest;
import com.mekongocop.mekongocopserver.common.LoginResponse;
import com.mekongocop.mekongocopserver.common.StatusResponse;
import com.mekongocop.mekongocopserver.dto.UserDTO;
import com.mekongocop.mekongocopserver.service.UserService;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import com.mekongocop.mekongocopserver.util.TokenExtractor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    @PostMapping("/register")
    public ResponseEntity<StatusResponse<String>> registerUser(@RequestBody UserDTO userDTO, @RequestParam String otp){
        try{
            userService.registerUser(userDTO, otp);
            return ResponseEntity.ok(new StatusResponse<>("Success", "User registered successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new StatusResponse<>("Error", e.getMessage(), null));
        }catch(Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "An unexpected error occurred", null));
        }
    }


    @PatchMapping("/user/email/{id}")
    public ResponseEntity<StatusResponse<UserDTO>> updateEmail(@RequestHeader("Authorization") String authHeader, @PathVariable int id, @RequestBody UserDTO userDTO, @RequestParam String otp){
        try{
            String token = TokenExtractor.extractToken(authHeader);
            if (token == null) {
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Invalid token format", null));
            }
            if(jwtTokenProvider.validateToken(token)) {
                userService.updateEmail(userDTO, otp, id);
                return ResponseEntity.ok(new StatusResponse<>("Success", "User updated successfully", userDTO));
            }else{
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token is not valid", null));
            }

        } catch(Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "An unexpected error occurred", null));
        }
    }


    @PatchMapping("/user/password")
    public ResponseEntity<StatusResponse<String>> resetPassword(@RequestHeader("Authorization") String authHeader, @RequestParam String oldPassword, @RequestParam String newPassword) {
        try {
            String token = TokenExtractor.extractToken(authHeader);
            if (token == null) {
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Invalid token format", null));
            }
            if (jwtTokenProvider.validateToken(token)) {
                userService.resetPassword(token, oldPassword, newPassword);
                return ResponseEntity.ok(new StatusResponse<>("Success", "Password updated successfully", null));
            } else {
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token is not valid", null));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new StatusResponse<>("Error", e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "An unexpected error occurred", null));
        }
    }

    @PostMapping("user/password/forgot")
    public ResponseEntity<StatusResponse<String>> forgotPassword(@RequestParam String email, HttpServletRequest httpRequest) {
        try{
            userService.forgotPassword(email, httpRequest);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Password forgot successfully", null));
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new StatusResponse<>("Error", e.getMessage(), null));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", e.getMessage(), null));
        }
    }

    @PatchMapping("user/password/renew")
    public ResponseEntity<StatusResponse<String>> renewPassword(@RequestParam String email, @RequestParam String otp, @RequestParam String newPassword) {
        try {
            userService.renewPassword(email, otp, newPassword);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Password renew successfully", null));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new StatusResponse<>("Error", e.getMessage(), null));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", e.getMessage(), null));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<StatusResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        try{
            userService.login(loginRequest, response);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Login successful", null));
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new StatusResponse<>("Error", e.getMessage(), null));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "An unexpected error occurred.", null));
        }
    }

}
