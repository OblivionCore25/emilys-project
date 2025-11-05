package com.example.drainadoption.dto;

import com.example.drainadoption.model.User;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String role;
    private Long adoptedDrainId;

    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        if (user.getAdoptedDrain() != null) {
            dto.setAdoptedDrainId(user.getAdoptedDrain().getId());
        }
        return dto;
    }
}