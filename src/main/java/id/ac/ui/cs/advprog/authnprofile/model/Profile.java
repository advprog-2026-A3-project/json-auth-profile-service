package id.ac.ui.cs.advprog.authnprofile.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "successful_transactions")
    @Builder.Default
    private Integer successfulTransactions = 0;

    @Column(name = "failed_transactions")
    @Builder.Default
    private Integer failedTransactions = 0;

    @Column(name = "rating")
    @Builder.Default
    private Double rating = 0.0;
}

