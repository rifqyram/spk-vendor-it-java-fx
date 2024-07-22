package ac.unindra.spk_vendor_it.repository;

import ac.unindra.spk_vendor_it.entity.UserInfo;
import ac.unindra.spk_vendor_it.util.StringUtil;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, String>, JpaSpecificationExecutor<UserInfo> {
    static Specification<UserInfo> hasSearchQuery(String q) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(q)) return criteriaBuilder.conjunction();
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("nip")), StringUtil.wrapLikeQuery(q)),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), StringUtil.wrapLikeQuery(q)),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("position")), StringUtil.wrapLikeQuery(q)),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), StringUtil.wrapLikeQuery(q)),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("mobilePhoneNo")), StringUtil.wrapLikeQuery(q))
            );
        };
    }
}
