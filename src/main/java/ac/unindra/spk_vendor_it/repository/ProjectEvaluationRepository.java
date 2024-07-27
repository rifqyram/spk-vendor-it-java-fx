package ac.unindra.spk_vendor_it.repository;

import ac.unindra.spk_vendor_it.entity.ProjectEvaluation;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public interface ProjectEvaluationRepository extends JpaRepository<ProjectEvaluation, String>, JpaSpecificationExecutor<ProjectEvaluation> {
    static Specification<ProjectEvaluation> hasSearchQuery(String q) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(q)) return criteriaBuilder.conjunction();
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("project").get("name")), "%" + q.toLowerCase() + "%")
            );
        };
    }
}
