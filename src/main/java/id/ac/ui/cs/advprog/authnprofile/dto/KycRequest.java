package id.ac.ui.cs.advprog.authnprofile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KycRequest {
    @NotBlank
    private String fullName;
    private String socialMediaLink;
}