package dev.gagnon.bfpcapi.service.impl;

import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.Recipient;
import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.exceptions.MailerSendException;
import dev.gagnon.bfpcapi.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailerSendEmailServiceImpl implements EmailService {

    @Value("${MAIL_API_TOKEN}")
    private String apiToken;

    @Value("${MAIL_FROM_NAME}")
    private String fromName;

    @Value("${MAIL_FROM_ADDRESS}")
    private String fromAddress;

    private final MailerSend mailerSend;

    public MailerSendEmailServiceImpl() {
        this.mailerSend = new MailerSend();
    }

    @Override
    public void sendEmail(String toName, String toEmail) {
        try {
            sendEmailInternal(toName, toEmail, "Test Email", "<p>This is a test email from MailerSend API</p>");
        } catch (MailerSendException e) {
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
        } catch (MailerSendException e) {
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
        } catch (MailerSendException e) {
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
        } catch (MailerSendException e) {
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
        } catch (MailerSendException e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    // ================== Helper Method ==================

    private void sendEmailInternal(String toName, String toEmail, String subject, String htmlContent) throws MailerSendException {
        Email email = new Email();
        email.setFrom(fromName, fromAddress);
        email.addRecipient(toName, toEmail);
        email.setSubject(subject);
        email.setHtml(htmlContent);

        mailerSend.setToken(apiToken);
        MailerSendResponse response = mailerSend.emails().send(email);

        log.info("Email sent to {} ({}): {}", toName, toEmail, response.messageId);
    }

    // ================== HTML Templates ==================

    private String buildVerificationEmailTemplate(String firstName, String verificationUrl) {
        return """
        <html><body style='font-family: Arial, sans-serif;'>
        <h2>Welcome to Agro Smart Benue!</h2>
        <p>Hi %s,</p>
        <p>Click the button below to verify your email:</p>
        <a href='%s' style='background-color:#3498db;color:white;padding:10px 20px;text-decoration:none;border-radius:5px;'>Verify Email</a>
        <p>This link will expire in 24 hours.</p>
        <p>Best regards,<br>Agro Smart Benue Team</p>
        </body></html>
        """.formatted(firstName, verificationUrl);
    }

    private String buildOTPEmailTemplate(String firstName, String otp) {
        return """
        <html><body style='font-family: Arial, sans-serif;'>
        <h2>Email Verification Code</h2>
        <p>Hi %s, use the code below:</p>
        <h1 style='color:#2c3e50;'>%s</h1>
        <p>This code expires in 10 minutes.</p>
        <p>Best regards,<br>Agro Smart Benue Team</p>
        </body></html>
        """.formatted(firstName, otp);
    }

    private String buildPasswordResetOTPEmailTemplate(String firstName, String otp) {
        return """
        <html><body style='font-family: Arial, sans-serif;'>
        <h2>Password Reset Request</h2>
        <p>Hi %s,</p>
        <p>Use this code to reset your password:</p>
        <h1 style='color:#856404;'>%s</h1>
        <p>This code expires in 10 minutes.</p>
        <p>Best regards,<br>Agro Smart Benue Team</p>
        </body></html>
        """.formatted(firstName, otp);
    }

    private String buildWelcomeEmailBody(String name) {
        return """
        <html><body style='font-family: Arial, sans-serif;'>
        <h2>Hello, %s!</h2>
        <p>Welcome to Agro Smart Benue. You can now:</p>
        <ul>
            <li>Browse products</li>
            <li>Place orders</li>
            <li>Connect with sellers</li>
            <li>Get information about best farm practices</li>
        </ul>
        <p>Best Regards,<br>Agro Smart Benue Support Team</p>
        </body></html>
        """.formatted(name);
    }
}
