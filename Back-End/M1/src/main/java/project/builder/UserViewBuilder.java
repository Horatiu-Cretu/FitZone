package project.builder;

import project.dto.UserViewDTO;
import project.entity.User;

public class UserViewBuilder {

    public static UserViewDTO toViewDTO(User user) {
        return UserViewDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }
}