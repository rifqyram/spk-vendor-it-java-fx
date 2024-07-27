package ac.unindra.spk_vendor_it.service;

import ac.unindra.spk_vendor_it.entity.EvaluationDetail;

import java.util.List;

public interface EvaluationDetailService {
    void saveOrUpdate(EvaluationDetail evaluationDetail);
    void saveAll(List<EvaluationDetail> evaluationDetails);
}
