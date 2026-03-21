package id.ac.ui.cs.advprog.authnprofile.dto;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
    private String username;
    private String displayName;
    private String phoneNumber;
    private String bio;
}