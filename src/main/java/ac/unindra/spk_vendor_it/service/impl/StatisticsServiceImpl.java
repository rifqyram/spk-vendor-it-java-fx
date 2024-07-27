package ac.unindra.spk_vendor_it.service.impl;

import ac.unindra.spk_vendor_it.repository.CriteriaRepository;
import ac.unindra.spk_vendor_it.repository.ProjectEvaluationRepository;
import ac.unindra.spk_vendor_it.repository.ProjectRepository;
import ac.unindra.spk_vendor_it.repository.VendorRepository;
import ac.unindra.spk_vendor_it.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final VendorRepository vendorRepository;
    private final ProjectRepository projectRepository;
    private final CriteriaRepository criteriaRepository;
    private final ProjectEvaluationRepository projectEvaluationRepository;

    @Override
    public long totalVendor() {
        return vendorRepository.count();
    }

    @Override
    public long totalProject() {
        return projectRepository.count();
    }

    @Override
    public long totalCriteria() {
        return criteriaRepository.count();
    }

    @Override
    public long totalEvaluation() {
        return projectEvaluationRepository.count();
    }
}
