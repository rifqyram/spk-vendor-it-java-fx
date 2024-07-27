package ac.unindra.spk_vendor_it.service.impl;

import ac.unindra.spk_vendor_it.entity.EvaluationDetail;
import ac.unindra.spk_vendor_it.repository.EvaluationDetailRepository;
import ac.unindra.spk_vendor_it.service.EvaluationDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationDetailServiceImpl implements EvaluationDetailService {
    private final EvaluationDetailRepository evaluationDetailRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrUpdate(EvaluationDetail evaluationDetail) {
        evaluationDetailRepository.saveAndFlush(evaluationDetail);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveAll(List<EvaluationDetail> evaluationDetails) {
        evaluationDetailRepository.saveAllAndFlush(evaluationDetails);
    }
}
