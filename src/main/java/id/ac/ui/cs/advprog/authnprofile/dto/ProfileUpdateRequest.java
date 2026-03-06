package id.ac.ui.cs.advprog.authnprofile.dto;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
    private String username;
    private String fullName;
    private String bio;
}