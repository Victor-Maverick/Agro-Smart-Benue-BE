package dev.gagnon.bfpcapi.service;

import dev.gagnon.bfpcapi.data.model.User;
import dev.gagnon.bfpcapi.dto.request.PasswordRequest;
import dev.gagnon.bfpcapi.dto.request.UserRegistrationRequest;
import dev.gagnon.bfpcapi.dto.response.RegisterResponse;
import dev.gagnon.bfpcapi.dto.response.UserResponse;
import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;


public interface UserService {
    boolean verifyUser(String token);
    RegisterResponse registerUser(UserRegistrationRequest request) throws MessagingException, UnsupportedEncodingException;
    void resendVerificationEmail(String email) throws MessagingException, UnsupportedEncodingException;
    
    // OTP-based verification methods
    boolean verifyUserWithOTP(String email, String otp);
    boolean resendOTP(String email);
    void sendVerificationOTP(String email);
    
    User findByEmail(String username);
    UserResponse getProfileFor(String email);

    boolean isUserVerified(String email);
    String resetPassword(PasswordRequest passwordRequest);

    boolean existsById(Long userId);
    boolean existsByEmail(String email);

    String deleteUser(Long id);

    String disableUser(Long id);

    String registerAdmin(UserRegistrationRequest request);
    
    // Forgot password methods
    boolean sendPasswordResetOTP(String email);
    boolean verifyPasswordResetOTP(String email, String otp);
    boolean resetPasswordWithOTP(String email, String otp, String newPassword);

    String deleteUserByEmail(String email);

    String uploadPhoto(String email, MultipartFile image);
    
    // Profile-related methods
    User getUserByEmail(String email);
    Long getUserIdByEmail(String email);
}
