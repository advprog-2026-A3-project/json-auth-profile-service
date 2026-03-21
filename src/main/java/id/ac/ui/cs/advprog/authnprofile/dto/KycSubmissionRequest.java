package id.ac.ui.cs.advprog.authnprofile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KycSubmissionRequest {
    @NotBlank
    private String displayName;

    private String phoneNumber;
    private String socialMediaLink;
}