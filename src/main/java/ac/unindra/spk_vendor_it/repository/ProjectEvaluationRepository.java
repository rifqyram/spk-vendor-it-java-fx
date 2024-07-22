package ac.unindra.spk_vendor_it.repository;

import ac.unindra.spk_vendor_it.entity.ProjectEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectEvaluationRepository extends JpaRepository<ProjectEvaluation, String>, JpaSpecificationExecutor<ProjectEvaluation> {
}
