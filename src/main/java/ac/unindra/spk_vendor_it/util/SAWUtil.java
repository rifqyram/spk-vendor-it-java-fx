package ac.unindra.spk_vendor_it.util;

import ac.unindra.spk_vendor_it.constant.CriteriaCategory;
import ac.unindra.spk_vendor_it.entity.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SAWUtil {
    public static Integer getTotalWeight(List<Criteria> criteriaList) {
        return criteriaList.stream().mapToInt(Criteria::getWeight).sum();
    }

    public static Double getProcessWeight(Integer totalWeight, Integer weight) {
        return (double) weight / totalWeight;
    }

    public static Double getValueByCategory(ProjectEvaluation projectEvaluation, String criteriaId) {
        // check project evaluation -> List Evaluation -> List EvaluationDetail If Cost get min value else get max value
        List<Evaluation> evaluations = projectEvaluation.getEvaluations();
        for (Evaluation evaluation : evaluations) {
            for (EvaluationDetail evaluationDetail : evaluation.getEvaluationDetails()) {
                if (evaluationDetail.getCriteriaId().equals(criteriaId)) {
                    if (evaluationDetail.getCriteriaCategory().equals(CriteriaCategory.COST)) {
                        return evaluations.stream().mapToDouble(e -> e.getEvaluationDetails().stream()
                                .filter(ed -> ed.getCriteriaId().equals(criteriaId))
                                .mapToDouble(EvaluationDetail::getScore)
                                .min().orElse(0)).min().orElse(0);
                    } else {
                        return evaluations.stream().mapToDouble(e -> e.getEvaluationDetails().stream()
                                .filter(ed -> ed.getCriteriaId().equals(criteriaId))
                                .mapToDouble(EvaluationDetail::getScore)
                                .max().orElse(0)).max().orElse(0);
                    }
                }
                return 0.0;
            }
        }
        return 0.0;
    }
}
