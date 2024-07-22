package ac.unindra.spk_vendor_it.repository;

import ac.unindra.spk_vendor_it.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, String>, JpaSpecificationExecutor<UserCredential> {
    Optional<UserCredential> findByUsername(String username);
    boolean existsByUsername(String username);
}
