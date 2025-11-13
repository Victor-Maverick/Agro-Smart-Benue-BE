package dev.gagnon.bfpcapi.service.impl;

import com.cloudinary.Cloudinary;
import dev.gagnon.bfpcapi.data.constants.Role;
import dev.gagnon.bfpcapi.data.constants.UserStatus;
import dev.gagnon.bfpcapi.data.model.User;
import dev.gagnon.bfpcapi.data.model.VerificationToken;
import dev.gagnon.bfpcapi.data.repository.UserRepository;
import dev.gagnon.bfpcapi.data.repository.VerificationTokenRepository;
import dev.gagnon.bfpcapi.dto.request.PasswordRequest;
import dev.gagnon.bfpcapi.dto.request.UserRegistrationRequest;
import dev.gagnon.bfpcapi.dto.response.RegisterResponse;
import dev.gagnon.bfpcapi.dto.response.UserResponse;
import dev.gagnon.bfpcapi.exception.BusinessException;
import dev.gagnon.bfpcapi.exception.UserNotFoundException;
import dev.gagnon.bfpcapi.service.EmailService;
import dev.gagnon.bfpcapi.service.OTPService;
import dev.gagnon.bfpcapi.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static dev.gagnon.bfpcapi.data.constants.Role.ADMIN;
import static dev.gagnon.bfpcapi.data.constants.UserStatus.*;
import static dev.gagnon.bfpcapi.utils.ServiceUtils.getMediaUrl;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final VerificationTokenRepository tokenRepository;
    private final OTPService otpService;
    private final Cloudinary cloudinary;

    @Override
    public RegisterResponse registerUser(UserRegistrationRequest request) throws MessagingException, UnsupportedEncodingException {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UsernameNotFoundException("user exists with email");
        }

        User user = User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .status(PENDING)
                .payoutAmount(new BigDecimal(0))
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        user.setRoles(new HashSet<>());
        user.getRoles().add(Role.FARMER);
        User savedUser = userRepository.save(user);

        // Create OTP token and send OTP email
        VerificationToken verificationToken = otpService.createOTPToken(savedUser);
        emailService.sendOTPEmail(savedUser.getEmail(), savedUser.getFirstName(), verificationToken.getOtp());
        RegisterResponse response = modelMapper.map(savedUser, RegisterResponse.class);
        response.setMessage("User registered successfully.");
        return response;
    }


    @Override
    public boolean verifyUser(String token) {
        Optional<VerificationToken> verificationToken = tokenRepository.findByToken(token);
        if (verificationToken.isEmpty()) {
            return false;
        }
        VerificationToken vToken = verificationToken.get();
        if (vToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }
        User user = vToken.getUser();
        user.setStatus(UserStatus.VERIFIED);
        userRepository.save(user);
        tokenRepository.delete(vToken);
        emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());
        return true;
    }

    @Override
    @Transactional
    public void resendVerificationEmail(String email) throws MessagingException, UnsupportedEncodingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));

        if (user.getStatus() == VERIFIED) {
            throw new BusinessException("This account has already been verified.");
        }

        // Delete existing token if any
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        // Create new verification token
        VerificationToken newToken = new VerificationToken(user);
        tokenRepository.save(newToken);
        // Send verification email
        emailService.sendVerificationEmail(user.getEmail(), user.getFirstName(), newToken.getToken(), "https://marketplace.bdic.ng/register/emailVerification");
    }

    @Override
    public boolean verifyUserWithOTP(String email, String otp) {
        return otpService.validateOTP(email, otp);
    }

    @Override
    public boolean resendOTP(String email) {
        return otpService.resendOTP(email);
    }

    @Override
    @Transactional
    public void sendVerificationOTP(String email) {
        log.info("Attempting to send verification OTP for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));

        log.info("Found user: id={}, email={}", user.getId(), user.getEmail());

        if (user.getStatus() == VERIFIED) {
            throw new BusinessException("This account has already been verified.");
        }

        // Create OTP token and send OTP email
        try {
            VerificationToken verificationToken = otpService.createOTPToken(user);
            log.info("Created OTP token for user: {}", user.getEmail());

            emailService.sendOTPEmail(user.getEmail(), user.getFirstName(), verificationToken.getOtp());
            log.info("Successfully sent verification OTP to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification OTP to {}: {}", user.getEmail(), e.getMessage(), e);
            throw new BusinessException("Failed to send verification OTP: " + e.getMessage());
        }
    }

    @Override
    public User findByEmail(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found."));
    }

    @Override
    public UserResponse getProfileFor(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
        return new UserResponse(user);
    }


    @Override
    public boolean isUserVerified(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return user.getStatus() == VERIFIED;
    }

    @Override
    public String resetPassword(PasswordRequest passwordRequest) {
        User user = userRepository.findByEmail(passwordRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!passwordEncoder.matches(passwordRequest.getOldPassword(), user.getPassword())) {
            throw new BusinessException("Passwords do not match");
        }
        user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        userRepository.save(user);
        return "Password reset successful";
    }

    @Override
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public String deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);
        userRepository.delete(user);

        return "user deleted";
    }

    @Override
    public String disableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
        user.setStatus(INACTIVE);
        userRepository.save(user);
        return "user deleted";
    }

    @Override
    public String registerAdmin(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UsernameNotFoundException("user exists with email");
        }

        User.UserBuilder userBuilder = User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .status(VERIFIED)
                .password(passwordEncoder.encode(request.getPassword()));
        User user = userBuilder.build();
        user.setRoles(new HashSet<>());
        user.getRoles().add(ADMIN);
        userRepository.save(user);
        return "User registered successfully.";
    }


    @Override
    public boolean sendPasswordResetOTP(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));

        if (!(user.getStatus() == VERIFIED)) {
            throw new BusinessException("Account not verified. Please verify your account first.");
        }

        return otpService.resendPasswordResetOTP(email);
    }

    @Override
    public boolean verifyPasswordResetOTP(String email, String otp) {
        return otpService.validatePasswordResetOTP(email, otp);
    }

    @Override
    public boolean resetPasswordWithOTP(String email, String otp, String newPassword) {
        // First verify the OTP
        if (!otpService.validatePasswordResetOTP(email, otp)) {
            return false;
        }

        // Update the password
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Clean up the token after successful password reset
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        return true;
    }


    @Override
    public String deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);
        userRepository.delete(user);
        return "user deleted";
    }

    @Override
    public String uploadPhoto(String email, MultipartFile image) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        String imageUrl = getMediaUrl(image, cloudinary.uploader());
        user.setMediaUrl(imageUrl);
        userRepository.save(user);
        return "photo updated successfully";
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public Long getUserIdByEmail(String email) {
        User user = getUserByEmail(email);
        return user.getId();
    }
}
