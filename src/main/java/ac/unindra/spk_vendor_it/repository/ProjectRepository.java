package ac.unindra.spk_vendor_it.repository;

import ac.unindra.spk_vendor_it.entity.Project;
import ac.unindra.spk_vendor_it.util.StringUtil;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String>, JpaSpecificationExecutor<Project> {
    static Specification<Project> hasSearchQuery(String query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (!StringUtils.hasText(query)) return criteriaBuilder.conjunction();
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("name"), "%" + query + "%"),
                    criteriaBuilder.like(root.get("description"), "%" + query + "%")
            );
        };
    }
}
