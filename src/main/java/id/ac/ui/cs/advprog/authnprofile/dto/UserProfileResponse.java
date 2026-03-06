package id.ac.ui.cs.advprog.authnprofile.dto;

import id.ac.ui.cs.advprog.authnprofile.model.KycStatus;
import id.ac.ui.cs.advprog.authnprofile.model.Role;
import id.ac.ui.cs.advprog.authnprofile.model.User;
import lombok.Data;

@Data
public class UserProfileResponse {
    private Long id;
    private String email;
    private String username;
    private String fullName;
    private String bio;
    private Role role;
    private KycStatus kycStatus;
    private boolean active;

    public static UserProfileResponse from(User user) {
        UserProfileResponse dto = new UserProfileResponse();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getDisplayUsername());
        dto.setFullName(user.getFullName());
        dto.setBio(user.getBio());
        dto.setRole(user.getRole());
        dto.setKycStatus(user.getKycStatus());
        dto.setActive(user.isActive());
        return dto;
    }
}