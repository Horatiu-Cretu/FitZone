package project.service;

import project.dto.LoginRequestDTO;
import project.dto.LoginResponseDTO;
import project.dto.UserDTO;
import project.dto.UserViewDTO;

public interface UserService {
    UserViewDTO signUp(UserDTO userDTO);
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
}