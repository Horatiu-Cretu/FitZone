package project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.builder.UserBuilder;
import project.builder.UserViewBuilder;
import project.dto.LoginRequestDTO;
import project.dto.LoginResponseDTO;
import project.dto.UserDTO;
import project.dto.UserViewDTO;
import project.entity.User;
import project.entity.UserRole;
import project.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final IJWTService jwtService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           IJWTService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public UserViewDTO signUp(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        User user = UserBuilder.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        if (userDTO.getRole() == null) {
            user.setRole(UserRole.USER);
        } else {
            user.setRole(userDTO.getRole());
        }

        User savedUser = userRepository.save(user);
        return UserViewBuilder.toViewDTO(savedUser);
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found for email: " + userDetails.getUsername()));

        String jwt = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        return new LoginResponseDTO(jwt);
    }
}