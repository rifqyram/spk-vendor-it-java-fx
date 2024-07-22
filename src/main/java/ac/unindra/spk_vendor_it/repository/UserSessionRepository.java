package ac.unindra.spk_vendor_it.repository;

import ac.unindra.spk_vendor_it.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, String>, JpaSpecificationExecutor<UserSession> {
    Optional<UserSession> findByToken(String token);
    Optional<UserSession> findByUser_Username(String username);
    boolean existsByUser_Username(String username);
}
