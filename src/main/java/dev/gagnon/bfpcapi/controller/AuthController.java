package dev.gagnon.bfpcapi.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import dev.gagnon.bfpcapi.data.model.User;
import dev.gagnon.bfpcapi.data.repository.UserRepository;
import dev.gagnon.bfpcapi.dto.request.*;
import dev.gagnon.bfpcapi.dto.response.BfpcApiResponse;
import dev.gagnon.bfpcapi.dto.response.ErrorResponse;
import dev.gagnon.bfpcapi.dto.response.LoginResponse;
import dev.gagnon.bfpcapi.dto.response.OTPResponse;
import dev.gagnon.bfpcapi.exception.BFPCBaseException;
import dev.gagnon.bfpcapi.exception.BusinessException;
import dev.gagnon.bfpcapi.exception.UserNotFoundException;
import dev.gagnon.bfpcapi.security.config.RsaKeyProperties;
import dev.gagnon.bfpcapi.security.data.models.SecureUser;
import dev.gagnon.bfpcapi.security.services.AuthService;
import dev.gagnon.bfpcapi.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;

import static dev.gagnon.bfpcapi.data.constants.UserStatus.VERIFIED;
import static dev.gagnon.bfpcapi.security.utils.SecurityUtils.JWT_PREFIX;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final AuthService authService;
    private final RsaKeyProperties rsaKeys;

    private final  HttpServletResponse response;

    private final UserRepository userRepository;

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(AUTHORIZATION) String token) {
        token = token.replace(JWT_PREFIX, "").strip();
        authService.blacklist(token);
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(()->new UserNotFoundException("user no found"));
            
//            if(!(user.getStatus() ==VERIFIED)) {
//                ErrorResponse errorResponse = ErrorResponse.builder()
//                        .responseTime(now())
//                        .isSuccessful(false)
//                        .error("AccountNotVerified")
//                        .message("Account not verified. Please check your email and verify your account.")
//                        .path("/api/auth/login")
//                        .build();
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
//            }
            
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail().toLowerCase(),
                            loginRequest.getPassword()
                    )
            );

            SecureUser userDetails = (SecureUser) authentication.getPrincipal();
            String token = generateToken(authentication);

            Cookie cookie = createCookie(token);
            response.addCookie(cookie);

            LoginResponse loginResponse = LoginResponse.builder()
                    .responseTime(now())
                    .message("Login successful")
                    .firstName(userDetails.getFirstName())
                    .lastName(userDetails.getLastName())
                    .email(userDetails.getUsername())
                    .mediaUrl(userDetails.getMediaUrl())
                    .roles(userDetails.getRoles())
                    .token(token)
                    .build();
            return ResponseEntity.ok(new BfpcApiResponse<>(true, loginResponse));
        } catch (BadCredentialsException e) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .responseTime(now())
                    .isSuccessful(false)
                    .error("UnsuccessfulAuthentication")
                    .message("Invalid credentials")
                    .path("/api/auth/login")
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (BusinessException e) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .responseTime(now())
                    .isSuccessful(false)
                    .error("BusinessException")
                    .message(e.getMessage())
                    .path("/api/auth/login")
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    private String generateToken(Authentication authentication) {
        Algorithm algorithm = Algorithm.RSA512(rsaKeys.publicKey(), rsaKeys.privateKey());
        Instant now = Instant.now();
        UserDetails principal = (UserDetails) authentication.getPrincipal();

        return JWT.create()
                .withIssuer("BFPCAuthToken")
                .withIssuedAt(now)
                .withExpiresAt(now.plus(24, HOURS))
                .withSubject(principal.getUsername())
                .withClaim("principal", principal.getUsername())
                .withClaim("credentials", authentication.getCredentials().toString())
                .withArrayClaim("roles", extractAuthorities(authentication.getAuthorities()))
                .sign(algorithm);
    }

    private String[] extractAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);
    }

    private static Cookie createCookie(String token) {
        Cookie cookie = new Cookie("BFPCAuthToken", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(86400);
        cookie.setSecure(true);
        return cookie;
    }


    @PutMapping("/change-password")
    public ResponseEntity<?>changePassword(@RequestBody PasswordRequest passwordRequest) {
        try {
            String response = userService.resetPassword(passwordRequest);
            return ResponseEntity.ok(response);
        }
        catch (BFPCBaseException e){
            return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        String email = principal.getName();
        log.info("name:{}",email);
        User user = userService.findByEmail(email);

        return ResponseEntity.ok(Map.of(
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "email", user.getEmail()
        ));
    }


    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam String token) {
        try {
            boolean isVerified = userService.verifyUser(token);
            if (isVerified) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Email verified successfully"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid or expired verification token"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Verification failed: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTP(@RequestBody OTPVerificationRequest request) {
        try {
            boolean isVerified = userService.verifyUserWithOTP(request.getEmail(), request.getOtp());
            
            OTPResponse response = OTPResponse.builder()
                    .success(isVerified)
                    .message(isVerified ? "Email verified successfully" : "Invalid OTP")
                    .responseTime(now())
                    .build();
            
            return ResponseEntity.ok(new BfpcApiResponse<>(true, response));
        } catch (BusinessException e) {
            OTPResponse response = OTPResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .responseTime(now())
                    .build();
            
            return ResponseEntity.badRequest().body(new BfpcApiResponse<>(false, response));
        } catch (Exception e) {
            log.error("OTP verification error: ", e);
            OTPResponse response = OTPResponse.builder()
                    .success(false)
                    .message("Verification failed: " + e.getMessage())
                    .responseTime(now())
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BfpcApiResponse<>(false, response));
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOTP(@RequestBody ResendOTPRequest request) {
        try {
            boolean sent = userService.resendOTP(request.getEmail());
            
            OTPResponse response = OTPResponse.builder()
                    .success(sent)
                    .message(sent ? "OTP sent successfully" : "Failed to send OTP")
                    .responseTime(now())
                    .build();
            
            return ResponseEntity.ok(new BfpcApiResponse<>(true, response));
        } catch (BusinessException e) {
            OTPResponse response = OTPResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .responseTime(now())
                    .build();
            
            return ResponseEntity.badRequest().body(new BfpcApiResponse<>(false, response));
        } catch (Exception e) {
            log.error("Resend OTP error: ", e);
            OTPResponse response = OTPResponse.builder()
                    .success(false)
                    .message("Failed to resend OTP: " + e.getMessage())
                    .responseTime(now())
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BfpcApiResponse<>(false, response));
        }
    }

    @PostMapping("/send-verification-otp")
    public ResponseEntity<?> sendVerificationOTP( @RequestBody ResendOTPRequest request) {
        try {
            userService.sendVerificationOTP(request.getEmail());
            
            OTPResponse response = OTPResponse.builder()
                    .success(true)
                    .message("Verification OTP sent successfully")
                    .responseTime(now())
                    .build();
            
            return ResponseEntity.ok(new BfpcApiResponse<>(true, response));
        } catch (BusinessException e) {
            OTPResponse response = OTPResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .responseTime(now())
                    .build();
            
            return ResponseEntity.badRequest().body(new BfpcApiResponse<>(false, response));
        } catch (Exception e) {
            log.error("Send verification OTP error: ", e);
            OTPResponse response = OTPResponse.builder()
                    .success(false)
                    .message("Failed to send verification OTP: " + e.getMessage())
                    .responseTime(now())
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BfpcApiResponse<>(false, response));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            boolean sent = userService.sendPasswordResetOTP(request.getEmail());
            
            OTPResponse response = OTPResponse.builder()
                    .success(sent)
                    .message(sent ? "Password reset code sent successfully" : "Failed to send password reset code")
                    .responseTime(now())
                    .build();
            
            return ResponseEntity.ok(new BfpcApiResponse<>(true, response));
        } catch (BusinessException e) {
            OTPResponse response = OTPResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .responseTime(now())
                    .build();
            
            return ResponseEntity.badRequest().body(new BfpcApiResponse<>(false, response));
        } catch (Exception e) {
            log.error("Forgot password error: ", e);
            OTPResponse response = OTPResponse.builder()
                    .success(false)
                    .message("Failed to send password reset code: " + e.getMessage())
                    .responseTime(now())
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BfpcApiResponse<>(false, response));
        }
    }

    @PostMapping("/verify-password-reset-otp")
    public ResponseEntity<?> verifyPasswordResetOTP(@RequestBody OTPVerificationRequest request) {
        try {
            boolean isVerified = userService.verifyPasswordResetOTP(request.getEmail(), request.getOtp());
            
            OTPResponse response = OTPResponse.builder()
                    .success(isVerified)
                    .message(isVerified ? "OTP verified successfully" : "Invalid OTP")
                    .responseTime(now())
                    .build();
            
            return ResponseEntity.ok(new BfpcApiResponse<>(true, response));
        } catch (BusinessException e) {
            OTPResponse response = OTPResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .responseTime(now())
                    .build();
            
            return ResponseEntity.badRequest().body(new BfpcApiResponse<>(false, response));
        } catch (Exception e) {
            log.error("Verify password reset OTP error: ", e);
            OTPResponse response = OTPResponse.builder()
                    .success(false)
                    .message("Verification failed: " + e.getMessage())
                    .responseTime(now())
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BfpcApiResponse<>(false, response));
        }
    }

    @PostMapping("/reset-password-with-otp")
    public ResponseEntity<?> resetPasswordWithOTP(@RequestBody ForgotPasswordOTPRequest request) {
        try {
            boolean isReset = userService.resetPasswordWithOTP(request.getEmail(), request.getOtp(), request.getNewPassword());
            
            OTPResponse response = OTPResponse.builder()
                    .success(isReset)
                    .message(isReset ? "Password reset successfully" : "Failed to reset password")
                    .responseTime(now())
                    .build();
            
            return ResponseEntity.ok(new BfpcApiResponse<>(true, response));
        } catch (BusinessException e) {
            OTPResponse response = OTPResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .responseTime(now())
                    .build();
            
            return ResponseEntity.badRequest().body(new BfpcApiResponse<>(false, response));
        } catch (Exception e) {
            log.error("Reset password with OTP error: ", e);
            OTPResponse response = OTPResponse.builder()
                    .success(false)
                    .message("Failed to reset password: " + e.getMessage())
                    .responseTime(now())
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BfpcApiResponse<>(false, response));
        }
    }

}