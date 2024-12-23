package com.mekongocop.mekongocopserver.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mekongocop.mekongocopserver.common.LoginRequest;
import com.mekongocop.mekongocopserver.dto.UserDTO;
import com.mekongocop.mekongocopserver.dto.VouchersDTOs.VoucherDTO;
import com.mekongocop.mekongocopserver.dto.VouchersDTOs.VoucherUserDTO;
import com.mekongocop.mekongocopserver.entity.Role;
import com.mekongocop.mekongocopserver.entity.User;
import com.mekongocop.mekongocopserver.repository.RoleRepository;
import com.mekongocop.mekongocopserver.repository.UserProfileRepository;
import com.mekongocop.mekongocopserver.repository.UserRepository;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Autowired
    private UserProfileRepository profileRepository;

    @Autowired
    private JedisPool jedisPool;
    public static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserDTO convertUserToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();

        userDTO.setUser_id(user.getUser_id());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setOauth_provider(user.getOauth_provider());
        userDTO.setOauth_id(user.getOauth_id());

        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getRole_name)
                .collect(Collectors.toSet());
        userDTO.setRoles(roleNames);

        // Chuyển đổi voucher sang Set<VoucherUserDTO>
        Set<VoucherUserDTO> vouchers = user.getVoucherUsers()
                .stream()
                .map(voucherUser -> {
                    VoucherUserDTO voucherUserDTO = new VoucherUserDTO();
                    voucherUserDTO.setId(voucherUser.getId());
                    voucherUserDTO.setVoucher_id(voucherUser.getVoucher().getVoucher_id());
                    voucherUserDTO.setUser_id(voucherUser.getUser().getUser_id());
                    voucherUserDTO.setUsageCount(voucherUser.getUsage_count());
                    return voucherUserDTO;
                })
                .collect(Collectors.toSet());
        userDTO.setVouchers(vouchers);

        return userDTO;
    }

    public User convertUserDTOToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    public void updateEmail(String token,UserDTO userDTO, String otpInput) {
        try {
            int userId = jwtTokenProvider.getUserIdFromToken(token);

            if (userRepository.existsById(userId)) {
                Optional<User> optionalUser = userRepository.findById(userId);
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
            Role defaultRole = roleRepository.findByRolename("ROLE_BUYER");
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
    // Reset Password from Profile
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
    // Renew password from Login Page
    public void renewPassword(String email, String otp, String newPassword) {
        try{
            User user = userRepository.findByEmail(email);
            if(user == null) {
                throw new IllegalArgumentException("User not found");
            }
            if(otpService.isOTPValid(email, otp)){
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
            }else{
                throw new IllegalArgumentException("Invalid OTP");
            }
        }catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }

   public void forgotPassword(String email, HttpServletRequest httpRequest){
        try{
            String emailUser = String.valueOf(userRepository.findByEmail(email));
            if(emailUser != null){
                otpService.sendOTPForForgotPassword(email, httpRequest);
            }else {
                throw new IllegalArgumentException("Invalid email");
            }
        }catch (Exception e){
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
                accessTokenCookie.setPath("/");
                accessTokenCookie.setMaxAge(3600);
                response.addCookie(accessTokenCookie);
                //RefreshToken
                Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
                refreshTokenCookie.setPath("/");
                refreshTokenCookie.setMaxAge(259200);
                response.addCookie(refreshTokenCookie);
                //Username
                Cookie usernameCookie = new Cookie("userName", username);
                usernameCookie.setPath("/");
                usernameCookie.setMaxAge(259200);

                boolean hasProfile = profileRepository.existsByUser(user);
                Cookie hasProfileCookie = new Cookie("hasProfile", String.valueOf(hasProfile));
                hasProfileCookie.setPath("/");
                hasProfileCookie.setMaxAge(259200); // 3 days
                response.addCookie(hasProfileCookie);
                response.addCookie(usernameCookie);
            }else{
                throw new IllegalArgumentException("Tên đăng nhập hoặc mật khẩu sai");
            }
        }catch (IllegalArgumentException e) {
            throw e;
        }catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }
    public String refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        // Lấy refresh token từ header Authorization
        String refreshToken = request.getHeader("Authorization");
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);  // Lấy token sau "Bearer "
        }

        System.out.println("Received refresh token: " + refreshToken);

        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            User user = userRepository.findByUsername(username);

            if (user != null) {
                UserDTO userDTO = convertUserToUserDTO(user);
                String newAccessToken = jwtTokenProvider.generateAccessToken(userDTO);

                // Thêm cookie mới cho access token
                Cookie accessTokenCookie = new Cookie("accessToken", newAccessToken);
                accessTokenCookie.setPath("/");
                accessTokenCookie.setMaxAge(3600); // 1 hour
                response.addCookie(accessTokenCookie);

                return newAccessToken; // Trả về access token mới nếu cần
            } else {
                throw new IllegalArgumentException("Invalid refresh token.");
            }
        } else {
            throw new IllegalArgumentException("Invalid refresh token.");
        }
    }

    private String getRefreshTokenFromCookies(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


    public Long getTotalUsers() {
        return userRepository.countAllUsers();
    }

    public Long getUsersWithRoleBuyer() {
        return userRepository.countUsersWithRoleBuyer();
    }

    public Long getUsersWithRoleSeller() {
        return userRepository.countUsersWithRoleSeller();
    }
    public List<UserDTO> getAllUsers() {
        List<User> userList = userRepository.findAll();
        return convertUserListToUserDTOList(userList);
    }
    private List<UserDTO> convertUserListToUserDTOList(List<User> userList) {
        return userList.stream().map(this::convertUserToUserDTO).collect(Collectors.toList());
    }


    public List<UserDTO> getUsersWithPagination(int page) {
        int size = 50; // Default page size
        int offset = (page - 1) * size;
        String cacheKey = "users_page_" + page + "_size_" + size;

        ObjectMapper objectMapper = new ObjectMapper();

        try (Jedis jedis = jedisPool.getResource()) {
            // Check Redis cache
            String cachedData = jedis.get(cacheKey);
            if (cachedData != null) {
                // Deserialize data from JSON
                return objectMapper.readValue(cachedData, new TypeReference<List<UserDTO>>() {});
            }

            // If not in cache, query the database
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<User> userPage = userRepository.findAll(pageable);
            List<User> userList = userPage.getContent();

            // Log the size of the user list fetched from the database
            System.out.println("Fetched " + userList.size() + " users from the database.");

            // Convert to UserDTO
            List<UserDTO> userDTOs = convertUserListToUserDTOList(userList);

            // Log the size of the user DTO list
            System.out.println("Converted to " + userDTOs.size() + " UserDTOs.");

            // Save to Redis as JSON with a 10-minute expiration
            jedis.setex(cacheKey, 600, objectMapper.writeValueAsString(userDTOs));

            return userDTOs;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


}
