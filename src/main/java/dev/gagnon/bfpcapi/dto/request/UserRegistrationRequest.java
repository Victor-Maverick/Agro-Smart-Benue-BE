package dev.gagnon.bfpcapi.dto.request;

import lombok.Data;

@Data
public class UserRegistrationRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
}