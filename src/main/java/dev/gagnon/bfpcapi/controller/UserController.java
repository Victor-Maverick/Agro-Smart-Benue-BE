package dev.gagnon.bfpcapi.controller;

import dev.gagnon.bfpcapi.data.model.User;
import dev.gagnon.bfpcapi.data.model.UserProfile;
import dev.gagnon.bfpcapi.data.repository.UserRepository;
import dev.gagnon.bfpcapi.dto.request.UserProfileRequest;
import dev.gagnon.bfpcapi.dto.request.UserRegistrationRequest;
import dev.gagnon.bfpcapi.dto.response.BfpcApiResponse;
import dev.gagnon.bfpcapi.dto.response.UserResponse;
import dev.gagnon.bfpcapi.exception.BFPCBaseException;
import dev.gagnon.bfpcapi.service.UserProfileService;
import dev.gagnon.bfpcapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserProfileService userProfileService;

    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequest request) {
        try{
            var response = userService.registerUser(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),BAD_REQUEST);
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            userService.resendVerificationEmail(email);
            return ResponseEntity.ok("Verification email has been resent.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    @PutMapping(value = "/upload-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPhoto(@RequestParam String email, MultipartFile image){
        try{
            String response = userService.uploadPhoto(email,image);
            return new ResponseEntity<>(response, OK);
        }
        catch (BFPCBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }


    @PostMapping("/add-admin")
    public ResponseEntity<?>addAdmin(@RequestBody UserRegistrationRequest request) {
        try{
            String response = userService.registerAdmin(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),BAD_REQUEST);
        }
    }


    @GetMapping("/get-profile")
    public ResponseEntity<?>getUserProfile(@RequestParam String email){
        try{
            UserResponse response = userService.getProfileFor(email);
            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(){
        List<User>users = userRepository.findAll();
        List<UserResponse> userResponses = users.stream()
            .map(UserResponse::new)
            .toList();
        return new ResponseEntity<>(userResponses, OK);
    }
    @GetMapping("/allCount")
    public ResponseEntity<?> getAllUsersCount(){
        List<User>users = userRepository.findAll();
        return ResponseEntity.ok(users.size());
    }

    @GetMapping("/isUserValid/{userId}")
    public boolean isUserValid(@PathVariable Long userId) {
        return userService.existsById(userId);
    }
    @GetMapping("/exists")
    public boolean isUserValid(@RequestParam String email) {
        return userService.existsByEmail(email); // returns true/false
    }

    @DeleteMapping("/delete-user")
    public ResponseEntity<?>deleteUser(@RequestParam Long id){
        try{
            String response = userService.deleteUser(id);
            return new ResponseEntity<>(response, OK);
        }
        catch (BFPCBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete-by-email")
    public ResponseEntity<?>deleteUser(@RequestParam String email){
        try{
            String response = userService.deleteUserByEmail(email);
            return new ResponseEntity<>(response, OK);
        }
        catch (BFPCBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @PutMapping("/disable-user")
    public ResponseEntity<?>disableUser(@RequestParam Long id){
        try{
            String response = userService.disableUser(id);
            return new ResponseEntity<>(response, OK);
        }
        catch (BFPCBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    // Profile endpoints
    @PostMapping("/profile")
    public ResponseEntity<BfpcApiResponse<UserProfile>> createOrUpdateProfile(@RequestBody UserProfileRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        Long userId = userService.getUserIdByEmail(email);
        UserProfile profile = userProfileService.createOrUpdateProfile(userId, request);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, profile));
    }

    @GetMapping("/profile")
    public ResponseEntity<BfpcApiResponse<UserProfile>> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        Long userId = userService.getUserIdByEmail(email);
        UserProfile profile = userProfileService.getProfileByUserId(userId);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, profile));
    }

    @GetMapping("/profile/status")
    public ResponseEntity<BfpcApiResponse<Boolean>> getProfileStatus() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        Long userId = userService.getUserIdByEmail(email);
        boolean completed = userProfileService.hasCompletedProfile(userId);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, completed));
    }
}
