package id.ac.ui.cs.advprog.authnprofile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO untuk validasi KYC oleh Admin.
 * Digunakan untuk approve atau reject pengajuan KYC pengguna.
 */
@Data
public class KycValidationRequest {

    @NotBlank(message = "Action cannot be blank")
    private String action; // "APPROVE" atau "REJECT"

    private String reviewNotes; // Catatan review (opsional)

    public boolean isApprove() {
        return "APPROVE".equalsIgnoreCase(action);
    }

    public boolean isReject() {
        return "REJECT".equalsIgnoreCase(action);
    }
}

