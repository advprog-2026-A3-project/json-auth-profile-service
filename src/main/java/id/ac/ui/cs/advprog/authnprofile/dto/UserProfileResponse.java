package id.ac.ui.cs.advprog.authnprofile.dto;

import id.ac.ui.cs.advprog.authnprofile.model.KycStatus;
import id.ac.ui.cs.advprog.authnprofile.model.Role;
import id.ac.ui.cs.advprog.authnprofile.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResponse {
    private Long id;
    private String email;
    private String username;
    private String displayName;
    private String phoneNumber;
    private String bio;
    private Role role;
    private KycStatus kycStatus;
    private boolean active;

    // Jastiper-only stats
    private Integer successfulTransactions;
    private Integer failedTransactions;
    private Double rating;

    public static UserProfileResponse from(User user) {
        UserProfileResponse dto = new UserProfileResponse();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getDisplayUsername());
        dto.setDisplayName(user.getDisplayName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setBio(user.getBio());
        dto.setRole(user.getRole());
        dto.setKycStatus(user.getKycStatus());
        dto.setActive(user.isActive());

        // Include Jastiper stats only if user is a Jastiper
        if (Role.JASTIPER.equals(user.getRole())) {
            // Note: Stats would come from Profile entity in a real scenario
            // For now, we set default values. In production, this would join with Profile table.
            dto.setSuccessfulTransactions(0);
            dto.setFailedTransactions(0);
            dto.setRating(0.0);
        }

        return dto;
    }
}