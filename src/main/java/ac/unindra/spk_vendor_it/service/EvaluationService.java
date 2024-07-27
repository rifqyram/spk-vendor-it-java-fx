package ac.unindra.spk_vendor_it.service;

import ac.unindra.spk_vendor_it.entity.Evaluation;
import ac.unindra.spk_vendor_it.model.EvaluationResultModel;

import java.util.List;

public interface EvaluationService {
    void saveOrUpdate(Evaluation evaluation);
}
