package id.ac.ui.cs.advprog.authnprofile.repository;

import id.ac.ui.cs.advprog.authnprofile.model.KycStatus;
import id.ac.ui.cs.advprog.authnprofile.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    List<User> findByKycStatus(KycStatus kycStatus);
}