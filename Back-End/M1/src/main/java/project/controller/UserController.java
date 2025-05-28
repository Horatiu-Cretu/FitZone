package project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.dto.LoginRequestDTO;
import project.dto.LoginResponseDTO;
import project.dto.UserDTO;
import project.dto.UserViewDTO;
import project.service.UserService; // Assuming UserService is your interface for UserServiceImpl

import java.util.Map; // Make sure Map is imported

@RestController
@RequestMapping("/api/auth")
public class UserController extends BaseController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody UserDTO userDTO) {
        try {
            UserViewDTO newUser = userService.signUp(userDTO);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            logger.error("Sign up error: ", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            LoginResponseDTO loginResponse = userService.login(loginRequestDTO);
            return ResponseEntity.ok(loginResponse);
        } catch (RuntimeException e) {
            logger.error("Login error: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials or user not found."));
        }
    }
}