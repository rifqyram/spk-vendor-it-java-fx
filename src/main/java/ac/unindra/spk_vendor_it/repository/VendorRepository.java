package ac.unindra.spk_vendor_it.repository;

import ac.unindra.spk_vendor_it.entity.Vendor;
import ac.unindra.spk_vendor_it.util.StringUtil;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, String>, JpaSpecificationExecutor<Vendor> {
    static Specification<Vendor> hasSearchQuery(String q) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(q)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("id")), StringUtil.wrapLikeQuery(q)),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), StringUtil.wrapLikeQuery(q)),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), StringUtil.wrapLikeQuery(q)),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), StringUtil.wrapLikeQuery(q)),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("mobilePhoneNo")), StringUtil.wrapLikeQuery(q))
            );
        };
    }
}
