package dev.gagnon.bfpcapi.service.impl;

import dev.gagnon.bfpcapi.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Service
@Slf4j
public class MailgunEmailServiceImpl implements EmailService {

    @Value("${mailgun.api.key}")
    private String apiKey;

    @Value("${mailgun.domain}")
    private String domain;

    @Value("${mailgun.from.name}")
    private String fromName;

    @Value("${mailgun.from.email}")
    private String fromEmail;

    private final RestTemplate restTemplate;

    public MailgunEmailServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void sendEmail(String toName, String toEmail) {
        try {
            sendEmailInternal(toName, toEmail, "Test Email", "<p>This is a test email from Mailgun API</p>");
        } catch (Exception e) {
            log.error("Failed to send test email: {}", e.getMessage(), e);
        }
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String name) {
        String subject = "Welcome to Agro Smart Benue!";
        String htmlContent = buildWelcomeEmailBody(name);

        try {
            sendEmailInternal(name, toEmail, subject, htmlContent);
            log.info("Welcome email sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send welcome email", e);
        }
    }

    @Override
    public void sendVerificationEmail(String toEmail, String firstName, String verificationToken, String frontendUrl) {
        String subject = "Verify your email address";
        String verificationUrl = frontendUrl + "?token=" + verificationToken;
        String htmlContent = buildVerificationEmailTemplate(firstName, verificationUrl);

        try {
            sendEmailInternal(firstName, toEmail, subject, htmlContent);
            log.info("Verification email sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    @Override
    public void sendOTPEmail(String toEmail, String firstName, String otp) {
        String subject = "Your Verification Code";
        String htmlContent = buildOTPEmailTemplate(firstName, otp);

        try {
            sendEmailInternal(firstName, toEmail, subject, htmlContent);
            log.info("OTP email sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    @Override
    public void sendPasswordResetOTPEmail(String toEmail, String firstName, String otp) {
        String subject = "Password Reset Code";
        String htmlContent = buildPasswordResetOTPEmailTemplate(firstName, otp);

        try {
            sendEmailInternal(firstName, toEmail, subject, htmlContent);
            log.info("Password reset email sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    // ================== Helper Method ==================

    private void sendEmailInternal(String toName, String toEmail, String subject, String htmlContent) {
        String url = String.format("https://api.mailgun.net/v3/%s/messages", domain);

        // Create headers with Basic Auth
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String auth = "api:" + apiKey;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);

        // Create form data
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("from", String.format("%s <%s>", fromName, fromEmail));
        body.add("to", String.format("%s <%s>", toName, toEmail));
        body.add("subject", subject);
        body.add("html", htmlContent);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Email sent successfully to {} ({})", toName, toEmail);
            } else {
                log.error("Failed to send email. Status: {}, Response: {}", 
                    response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to send email via Mailgun");
            }
        } catch (Exception e) {
            log.error("Error sending email via Mailgun: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send email via Mailgun", e);
        }
    }

    // ================== HTML Templates ==================

    private String buildVerificationEmailTemplate(String firstName, String verificationUrl) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
        </head>
        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
            <div style="background-color: #16a34a; padding: 20px; text-align: center; border-radius: 10px 10px 0 0;">
                <h1 style="color: white; margin: 0;">Agro Smart Benue</h1>
            </div>
            <div style="background-color: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px;">
                <h2 style="color: #16a34a;">Welcome to Agro Smart Benue!</h2>
                <p>Hi <strong>%s</strong>,</p>
                <p>Thank you for registering with Agro Smart Benue. Please verify your email address to complete your registration.</p>
                <div style="text-align: center; margin: 30px 0;">
                    <a href="%s" style="background-color: #16a34a; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold;">Verify Email Address</a>
                </div>
                <p style="color: #666; font-size: 14px;">This verification link will expire in 24 hours.</p>
                <p style="color: #666; font-size: 14px;">If you didn't create an account, please ignore this email.</p>
                <hr style="border: none; border-top: 1px solid #ddd; margin: 30px 0;">
                <p style="color: #999; font-size: 12px; text-align: center;">
                    Best regards,<br>
                    <strong>Agro Smart Benue Team</strong>
                </p>
            </div>
        </body>
        </html>
        """.formatted(firstName, verificationUrl);
    }

    private String buildOTPEmailTemplate(String firstName, String otp) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
        </head>
        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
            <div style="background-color: #16a34a; padding: 20px; text-align: center; border-radius: 10px 10px 0 0;">
                <h1 style="color: white; margin: 0;">Agro Smart Benue</h1>
            </div>
            <div style="background-color: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px;">
                <h2 style="color: #16a34a;">Email Verification Code</h2>
                <p>Hi <strong>%s</strong>,</p>
                <p>Use the verification code below to complete your registration:</p>
                <div style="background-color: white; padding: 20px; text-align: center; margin: 30px 0; border-radius: 5px; border: 2px dashed #16a34a;">
                    <h1 style="color: #16a34a; font-size: 36px; letter-spacing: 5px; margin: 0;">%s</h1>
                </div>
                <p style="color: #666; font-size: 14px;">This code will expire in 10 minutes.</p>
                <p style="color: #666; font-size: 14px;">If you didn't request this code, please ignore this email.</p>
                <hr style="border: none; border-top: 1px solid #ddd; margin: 30px 0;">
                <p style="color: #999; font-size: 12px; text-align: center;">
                    Best regards,<br>
                    <strong>Agro Smart Benue Team</strong>
                </p>
            </div>
        </body>
        </html>
        """.formatted(firstName, otp);
    }

    private String buildPasswordResetOTPEmailTemplate(String firstName, String otp) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
        </head>
        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
            <div style="background-color: #dc2626; padding: 20px; text-align: center; border-radius: 10px 10px 0 0;">
                <h1 style="color: white; margin: 0;">Agro Smart Benue</h1>
            </div>
            <div style="background-color: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px;">
                <h2 style="color: #dc2626;">Password Reset Request</h2>
                <p>Hi <strong>%s</strong>,</p>
                <p>We received a request to reset your password. Use the code below to proceed:</p>
                <div style="background-color: white; padding: 20px; text-align: center; margin: 30px 0; border-radius: 5px; border: 2px dashed #dc2626;">
                    <h1 style="color: #dc2626; font-size: 36px; letter-spacing: 5px; margin: 0;">%s</h1>
                </div>
                <p style="color: #666; font-size: 14px;">This code will expire in 10 minutes.</p>
                <p style="color: #dc2626; font-size: 14px; font-weight: bold;">If you didn't request a password reset, please ignore this email and your password will remain unchanged.</p>
                <hr style="border: none; border-top: 1px solid #ddd; margin: 30px 0;">
                <p style="color: #999; font-size: 12px; text-align: center;">
                    Best regards,<br>
                    <strong>Agro Smart Benue Team</strong>
                </p>
            </div>
        </body>
        </html>
        """.formatted(firstName, otp);
    }

    private String buildWelcomeEmailBody(String name) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
        </head>
        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
            <div style="background-color: #16a34a; padding: 20px; text-align: center; border-radius: 10px 10px 0 0;">
                <h1 style="color: white; margin: 0;">Agro Smart Benue</h1>
            </div>
            <div style="background-color: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px;">
                <h2 style="color: #16a34a;">Welcome Aboard!</h2>
                <p>Hello <strong>%s</strong>,</p>
                <p>Welcome to Agro Smart Benue! We're excited to have you join our agricultural community.</p>
                <h3 style="color: #16a34a;">What you can do now:</h3>
                <ul style="line-height: 2;">
                    <li>🌾 Browse agricultural products</li>
                    <li>🛒 Place orders directly from farmers</li>
                    <li>🤝 Connect with sellers and buyers</li>
                    <li>📚 Get information about best farming practices</li>
                    <li>🌤️ Access weather forecasts for your area</li>
                    <li>📊 View market prices and trends</li>
                </ul>
                <div style="text-align: center; margin: 30px 0;">
                    <a href="https://your-app-url.com/dashboard" style="background-color: #16a34a; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold;">Go to Dashboard</a>
                </div>
                <hr style="border: none; border-top: 1px solid #ddd; margin: 30px 0;">
                <p style="color: #999; font-size: 12px; text-align: center;">
                    Best regards,<br>
                    <strong>Agro Smart Benue Support Team</strong>
                </p>
            </div>
        </body>
        </html>
        """.formatted(name);
    }
}
