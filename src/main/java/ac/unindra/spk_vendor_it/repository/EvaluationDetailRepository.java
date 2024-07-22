package ac.unindra.spk_vendor_it.repository;

import ac.unindra.spk_vendor_it.entity.EvaluationDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationDetailRepository extends JpaRepository<EvaluationDetail, String>, JpaSpecificationExecutor<EvaluationDetail> {
}
