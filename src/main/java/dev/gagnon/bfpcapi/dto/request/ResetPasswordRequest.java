package dev.gagnon.bfpcapi.dto.request;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
}
