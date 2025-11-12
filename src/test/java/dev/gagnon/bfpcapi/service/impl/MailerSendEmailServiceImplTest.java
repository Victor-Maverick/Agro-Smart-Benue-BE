package dev.gagnon.bfpcapi.service.impl;

import dev.gagnon.bfpcapi.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest

public class MailerSendEmailServiceImplTest {
    @Autowired
    private EmailService emailService;

    @Test
    public void sendEmailTest() {
        emailService.sendEmail("Aquiba", "victormsonter@agrosmartbenue.com");
    }
}