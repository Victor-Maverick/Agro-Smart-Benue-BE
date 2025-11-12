package dev.gagnon.bfpcapi.security.services;


import dev.gagnon.bfpcapi.data.model.User;
import dev.gagnon.bfpcapi.data.repository.UserRepository;
import dev.gagnon.bfpcapi.security.data.models.SecureUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(()-> new UsernameNotFoundException("Invalid username or password"));
        log.info("User found with email: {}", user.getEmail());
        return new SecureUser(user);
    }
}
