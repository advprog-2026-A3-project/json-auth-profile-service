package id.ac.ui.cs.advprog.authnprofile.repository;

import id.ac.ui.cs.advprog.authnprofile.model.KycRequest;
import id.ac.ui.cs.advprog.authnprofile.model.KycStatus;
import id.ac.ui.cs.advprog.authnprofile.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KycRequestRepository extends JpaRepository<KycRequest, Long> {
    Optional<KycRequest> findByUser(User user);
    Optional<KycRequest> findByUserAndStatus(User user, KycStatus status);
    List<KycRequest> findByStatus(KycStatus status);
    List<KycRequest> findAllByOrderBySubmittedAtDesc();
}

