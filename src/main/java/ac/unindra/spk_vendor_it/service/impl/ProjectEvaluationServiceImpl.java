package ac.unindra.spk_vendor_it.service.impl;

import ac.unindra.spk_vendor_it.constant.CriteriaCategory;
import ac.unindra.spk_vendor_it.constant.ResponseMessage;
import ac.unindra.spk_vendor_it.entity.*;
import ac.unindra.spk_vendor_it.model.*;
import ac.unindra.spk_vendor_it.repository.ProjectEvaluationRepository;
import ac.unindra.spk_vendor_it.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProjectEvaluationServiceImpl implements ProjectEvaluationService {
    private final ProjectEvaluationRepository projectEvaluationRepository;
    private final EvaluationService evaluationService;
    private final EvaluationDetailService evaluationDetailService;
    private final CriteriaService criteriaService;
    private final ProjectService projectService;
    private final VendorService vendorService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void create(EvaluationModel evaluationModel) {
        Project project = projectService.getOne(evaluationModel.getProject().getId());

        ProjectEvaluation projectEvaluation = ProjectEvaluation.builder()
                .project(project)
                .evaluationDate(LocalDateTime.now())
                .build();

        projectEvaluationRepository.saveAndFlush(projectEvaluation);

        Integer totalWeight = criteriaService.getTotalWeight();

        List<Evaluation> evaluations = new ArrayList<>();

        Map<String, Double> averageWeightPerCriteria = new HashMap<>();

        Map<String, Integer> minMap = new HashMap<>();
        Map<String, Integer> maxMap = new HashMap<>();

        for (EvaluationDetailModel evaluationDetail : evaluationModel.getEvaluationDetails()) {
            evaluationDetail.getCriteria().forEach((criteriaId, score) -> {
                Criteria criteria = criteriaService.getOne(criteriaId);
                if (criteria.getCategory().equals(CriteriaCategory.COST)) {
                    if (minMap.containsKey(criteriaId)) {
                        Integer min = minMap.get(criteriaId);
                        if (score < min) {
                            minMap.put(criteriaId, score);
                        }
                    } else {
                        minMap.put(criteriaId, score);
                    }
                } else if (criteria.getCategory().equals(CriteriaCategory.BENEFIT)) {
                    if (maxMap.containsKey(criteriaId)) {
                        Integer max = maxMap.get(criteriaId);
                        if (score > max) {
                            maxMap.put(criteriaId, score);
                        }
                    } else {
                        maxMap.put(criteriaId, score);
                    }
                }
            });
        }

        for (EvaluationDetailModel evaluationDetailModel : evaluationModel.getEvaluationDetails()) {
            Vendor vendor = vendorService.getOne(evaluationDetailModel.getVendor().getId());
            EvaluationResult evaluationResult = new EvaluationResult();

            List<EvaluationDetail> evaluationDetails = new ArrayList<>();
            for (Map.Entry<String, Integer> criteriaMap : evaluationDetailModel.getCriteria().entrySet()) {
                String criteriaId = criteriaMap.getKey();
                Integer score = criteriaMap.getValue();

                EvaluationDetail evaluationDetail = new EvaluationDetail();
                Criteria criteria = criteriaService.getOne(criteriaId);

                evaluationDetail.setCriteriaId(criteria.getId());
                evaluationDetail.setCriteriaName(criteria.getName());
                evaluationDetail.setCriteriaDescription(criteria.getDescription());
                evaluationDetail.setCriteriaCategory(criteria.getCategory());
                evaluationDetail.setCriteriaWeight(criteria.getWeight());

                if (criteria.getSubCriteria() != null && !criteria.getSubCriteria().isEmpty()) {
                    String subDesc = null;
                    for (SubCriteria sub : criteria.getSubCriteria()) {
                        if (sub.getWeight().equals(score)) {
                            subDesc = sub.getDescription();
                            break;
                        }
                    }
                    evaluationDetail.setSubCriteriaDescription(subDesc);
                    evaluationDetail.setSubCriteriaWeight(score);
                }

                evaluationDetail.setScore(score);
                evaluationDetails.add(evaluationDetail);

                Integer weight = criteria.getWeight();
                double averageWeight = (double) weight / totalWeight;
                averageWeightPerCriteria.put(criteriaId, averageWeight);
            }

            Map<String, Double> normalizedScore = new HashMap<>();
            for (EvaluationDetail detail : evaluationDetails) {
                double normalized = 0;
                String criteriaId = detail.getCriteriaId();
                double score = detail.getScore();
                Integer min = minMap.get(criteriaId);
                Integer max = maxMap.get(criteriaId);

                if (detail.getCriteriaCategory().equals(CriteriaCategory.COST)) {
                    normalized = min / score;
                    normalizedScore.put(criteriaId, normalized);
                    continue;
                } else if (detail.getCriteriaCategory().equals(CriteriaCategory.BENEFIT)) {
                    normalized = score / max;
                    normalizedScore.put(criteriaId, normalized);
                    continue;
                }

                normalizedScore.put(criteriaId, normalized);
            }

            double totalScore = 0;
            for (EvaluationDetail evaluationDetail : evaluationDetails) {
                String criteriaId = evaluationDetail.getCriteriaId();
                double weight = averageWeightPerCriteria.get(criteriaId).doubleValue();
                double normalized = normalizedScore.get(criteriaId);

                double result = weight * normalized;
                totalScore += result;
            }

            evaluationResult.setTotalScore(totalScore);

            Evaluation evaluation = Evaluation.builder()
                    .vendor(vendor)
                    .projectEvaluation(projectEvaluation)
                    .evaluationResult(evaluationResult)
                    .build();

            evaluationService.saveOrUpdate(evaluation);

            for (EvaluationDetail evaluationDetail : evaluationDetails) {
                evaluationDetail.setEvaluation(evaluation);
                evaluationDetailService.saveOrUpdate(evaluationDetail);
            }

            evaluations.add(evaluation);
        }

        projectEvaluation.setEvaluations(evaluations);
    }


    @Transactional(readOnly = true)
    @Override
    public Page<EvaluationModel> getAll(PageModel pageModel) {
        Pageable pageable = PageRequest.of(pageModel.getPage(), pageModel.getSize());
        Specification<ProjectEvaluation> projectEvaluationSpecification = Specification.where(ProjectEvaluationRepository.hasSearchQuery(pageModel.getQuery()));
        Page<ProjectEvaluation> projectEvaluationPage = projectEvaluationRepository.findAll(projectEvaluationSpecification, pageable);
        return projectEvaluationPage.map(projectEvaluation -> {
            Project project = projectEvaluation.getProject();

            ProjectModel projectModel = ProjectModel.fromEntity(project);

            List<EvaluationDetailModel> evaluationDetailModelList = new ArrayList<>();
            for (Evaluation evaluation : projectEvaluation.getEvaluations()) {
                VendorModel vendorModel = VendorModel.fromEntity(evaluation.getVendor());

                Map<String, Integer> criteria = new HashMap<>();

                for (EvaluationDetail evaluationDetail : evaluation.getEvaluationDetails()) {
                    criteria.put(evaluationDetail.getCriteriaId(), evaluationDetail.getScore());
                }

                EvaluationDetailModel evaluationDetailModel = EvaluationDetailModel.builder()
                        .vendor(vendorModel)
                        .criteria(criteria)
                        .build();
                evaluationDetailModelList.add(evaluationDetailModel);
            }

            return EvaluationModel.builder()
                    .id(projectEvaluation.getId())
                    .project(projectModel)
                    .countVendor(projectEvaluation.getEvaluations().size())
                    .startDate(project.getStartDate())
                    .endDate(project.getEndDate())
                    .evaluationDate(projectEvaluation.getEvaluationDate())
                    .evaluationDetails(evaluationDetailModelList)
                    .build();
        });
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProjectEvaluation> getAll() {
        return projectEvaluationRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<EvaluationResultModel> getEvaluationResult(String projectEvaluationId) {
        ProjectEvaluation projectEvaluation = getById(projectEvaluationId);

        projectEvaluation.getEvaluations().sort(Comparator.comparing(evaluation -> evaluation.getEvaluationResult().getTotalScore(), Comparator.reverseOrder()));

        int rank = 1;
        List<EvaluationResultModel> evaluationResultModels = new ArrayList<>();
        for (Evaluation evaluation : projectEvaluation.getEvaluations()) {
            Vendor vendor = evaluation.getVendor();
            EvaluationResult evaluationResult = evaluation.getEvaluationResult();
            evaluationResultModels.add(EvaluationResultModel.builder()
                    .vendor(VendorModel.fromEntity(vendor))
                    .preference(evaluationResult.getTotalScore())
                    .rank(rank++)
                    .build());
        }

        return evaluationResultModels;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String id) {
        projectEvaluationRepository.delete(getById(id));
    }

    @Transactional(readOnly = true)
    public ProjectEvaluation getById(String id) {
        return projectEvaluationRepository.findById(id).orElseThrow(() -> new RuntimeException(ResponseMessage.DATA_NOT_FOUND));
    }
}
