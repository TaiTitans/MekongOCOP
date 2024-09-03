package com.mekongocop.mekongocopserver.service;

import com.mekongocop.mekongocopserver.common.LoginRequest;
import com.mekongocop.mekongocopserver.dto.UserDTO;
import com.mekongocop.mekongocopserver.entity.Role;
import com.mekongocop.mekongocopserver.entity.User;
import com.mekongocop.mekongocopserver.repository.RoleRepository;
import com.mekongocop.mekongocopserver.repository.UserRepository;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private OTPService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserDTO convertUserToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public User convertUserDTOToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    public void updateEmail(UserDTO userDTO, String otpInput, int id) {
        try {
            if (userRepository.existsById(id)) {
                Optional<User> optionalUser = userRepository.findById(id);
                if (!optionalUser.isPresent()) {
                    throw new IllegalArgumentException("User not found");
                }
                User user = optionalUser.get();
                User existingEmail = userRepository.findByEmail(userDTO.getEmail());
                if (existingEmail != null) {
                    throw new IllegalArgumentException("Email already exists");
                }

                String otp = otpService.getOTPFromRedis(userDTO.getEmail());
                if (otp == null || !otp.equals(otpInput)) {
                    throw new IllegalArgumentException("Invalid OTP");
                }
                user.setEmail(userDTO.getEmail());
                userRepository.save(user);

            }
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }


    public void registerUser(UserDTO userDTO, String otpInput) {
        try {
            // Kiểm tra xem tên đăng nhập đã tồn tại chưa
            User existingUsername = userRepository.findByUsername(userDTO.getUsername());
            if (existingUsername != null) {
                throw new IllegalArgumentException("Username already registered");
            }

            // Kiểm tra xem email đã tồn tại chưa
            User existingUserEmail = userRepository.findByEmail(userDTO.getEmail());
            if (existingUserEmail != null) {
                throw new IllegalArgumentException("Email already registered");
            }

            // Validate OTP
            String otp = otpService.getOTPFromRedis(userDTO.getEmail());
            if (otp == null || !otp.equals(otpInput)) {
                throw new IllegalArgumentException("Invalid OTP");
            }

            // Chuyển đổi UserDTO thành User
            User user = convertUserDTOToUser(userDTO);

            // Tìm vai trò mặc định và gán nó cho người dùng
            Role defaultRole = roleRepository.findByRolename("ROLE_CUSTOMER");
            user.getRoles().add(defaultRole);

            // Mã hóa mật khẩu và lưu người dùng
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }
    public void resetPassword(String token, String oldPassword, String newPassword) {
        try {
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userRepository.save(user);
                } else {
                    throw new IllegalArgumentException("Old password does not match");
                }
            } else {
                throw new IllegalArgumentException("User not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }

    public void login(LoginRequest loginRequest, HttpServletResponse response) {
        try{
            User user = userRepository.findByUsername(loginRequest.getUsername());
            if(user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                UserDTO userDTO = convertUserToUserDTO(user);
                String accessToken = jwtTokenProvider.generateAccessToken(userDTO);
                String refreshToken = jwtTokenProvider.generateRefreshToken(userDTO);
                String username = user.getUsername();

                //AccessToken
                Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
                accessTokenCookie.setHttpOnly(true);
                accessTokenCookie.setSecure(true);
                accessTokenCookie.setMaxAge(3600);
                response.addCookie(accessTokenCookie);
                //RefreshToken
                Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
                refreshTokenCookie.setHttpOnly(true);
                refreshTokenCookie.setSecure(true);
                refreshTokenCookie.setMaxAge(259200);
                response.addCookie(refreshTokenCookie);
                //Username
                Cookie usernameCookie = new Cookie("userName", username);
                usernameCookie.setHttpOnly(true);
                usernameCookie.setSecure(true);
                usernameCookie.setPath("/");
                usernameCookie.setMaxAge(259200);
                response.addCookie(usernameCookie);
            }else{
                throw new IllegalArgumentException("User not found");
            }
        }catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }

}
