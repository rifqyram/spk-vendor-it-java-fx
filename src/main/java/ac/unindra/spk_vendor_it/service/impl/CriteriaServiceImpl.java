package ac.unindra.spk_vendor_it.service.impl;

import ac.unindra.spk_vendor_it.entity.Criteria;
import ac.unindra.spk_vendor_it.model.CriteriaModel;
import ac.unindra.spk_vendor_it.model.PageModel;
import ac.unindra.spk_vendor_it.repository.CriteriaRepository;
import ac.unindra.spk_vendor_it.service.CriteriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CriteriaServiceImpl implements CriteriaService {
    private final CriteriaRepository criteriaRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void create(CriteriaModel criteriaModel) {
        Criteria criteria = criteriaModel.toEntity();
        if (criteria.getSubCriteria() != null) {
            criteria.getSubCriteria().forEach(subCriteria -> subCriteria.setCriteria(criteria));
        }
        criteriaRepository.saveAndFlush(criteria);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CriteriaModel> getAll(PageModel pageModel) {
        Pageable pageable = PageRequest.of(pageModel.getPage(), pageModel.getSize(), Sort.Direction.ASC, "name");
        Specification<Criteria> specification = Specification.where(CriteriaRepository.hasSearchQuery(pageModel.getQuery()));
        Page<Criteria> criteriaPage = criteriaRepository.findAll(specification, pageable);
        return criteriaPage.map(CriteriaModel::fromEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(CriteriaModel criteriaModel) {
        getCriteria(criteriaModel.getId());
        Criteria criteria = criteriaModel.toEntity();
        if (criteria.getSubCriteria() != null) {
            criteria.getSubCriteria().forEach(subCriteria -> subCriteria.setCriteria(criteria));
        }
        criteriaRepository.saveAndFlush(criteria);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String id) {
        Criteria criteria = getCriteria(id);
        criteriaRepository.delete(criteria);
    }

    @Transactional(readOnly = true)
    protected Criteria getCriteria(String id) {
        return criteriaRepository.findById(id).orElseThrow(() -> new RuntimeException("Criteria tidak ditemukan"));
    }
}
