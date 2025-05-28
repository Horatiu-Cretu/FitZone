package project.builder;

import project.dto.UserDTO;
import project.entity.User;
import project.entity.UserRole;

public class UserBuilder {

    public static User toEntity(UserDTO userDTO) {
        return User.builder()
                .email(userDTO.getEmail())
                .fullName(userDTO.getFullName())
                .password(userDTO.getPassword())
                .role(userDTO.getRole() != null ? userDTO.getRole() : UserRole.USER)
                .build();
    }
}