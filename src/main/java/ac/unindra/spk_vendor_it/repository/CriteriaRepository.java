package ac.unindra.spk_vendor_it.repository;

import ac.unindra.spk_vendor_it.entity.Criteria;
import ac.unindra.spk_vendor_it.util.StringUtil;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public interface CriteriaRepository extends JpaRepository<Criteria, String>, JpaSpecificationExecutor<Criteria> {
    static Specification<Criteria> hasSearchQuery(String q) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(q)) return criteriaBuilder.conjunction();
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + q.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("category")), "%" + q.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + q.toLowerCase() + "%")
            );
        };
    }
}
