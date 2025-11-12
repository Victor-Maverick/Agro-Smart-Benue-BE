package dev.gagnon.bfpcapi.dto.response;

import dev.gagnon.bfpcapi.data.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime createdAt;
    private List<String> roles;
    private String phone;
    private String imageUrl;
    public UserResponse(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName =user.getLastName();
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt();
        this.roles = Collections.singletonList(String.valueOf(user.getRoles().stream().toList()));
        this.phone = user.getPhone();
        this.imageUrl = (user.getMediaUrl() != null) ? user.getMediaUrl() : "";    }
}
