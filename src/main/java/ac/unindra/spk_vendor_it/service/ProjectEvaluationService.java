package ac.unindra.spk_vendor_it.service;

import ac.unindra.spk_vendor_it.entity.Evaluation;
import ac.unindra.spk_vendor_it.entity.ProjectEvaluation;
import ac.unindra.spk_vendor_it.model.EvaluationModel;
import ac.unindra.spk_vendor_it.model.EvaluationResultModel;
import ac.unindra.spk_vendor_it.model.PageModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProjectEvaluationService {
    void create(EvaluationModel evaluationModel);
    Page<EvaluationModel> getAll(PageModel pageModel);
    List<ProjectEvaluation> getAll();
    ProjectEvaluation getById(String id);
    List<EvaluationResultModel> getEvaluationResult(String projectEvaluationId);
    void delete(String id);
}
