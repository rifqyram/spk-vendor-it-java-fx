package ac.unindra.spk_vendor_it.service.impl;

import ac.unindra.spk_vendor_it.constant.ResponseMessage;
import ac.unindra.spk_vendor_it.entity.Evaluation;
import ac.unindra.spk_vendor_it.model.EvaluationResultModel;
import ac.unindra.spk_vendor_it.repository.EvaluationRepository;
import ac.unindra.spk_vendor_it.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationServiceImpl implements EvaluationService {
    private final EvaluationRepository evaluationRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrUpdate(Evaluation evaluation) {
        evaluationRepository.saveAndFlush(evaluation);
    }

}
