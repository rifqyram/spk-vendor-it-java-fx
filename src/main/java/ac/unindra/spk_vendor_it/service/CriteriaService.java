package ac.unindra.spk_vendor_it.service;

import ac.unindra.spk_vendor_it.entity.Criteria;
import ac.unindra.spk_vendor_it.model.CriteriaModel;
import ac.unindra.spk_vendor_it.model.PageModel;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface CriteriaService {
    void create(CriteriaModel criteriaModel);
    Page<CriteriaModel> getAll(PageModel pageModel);
    List<CriteriaModel> getAll();
    void update(CriteriaModel criteriaModel);
    void delete(String id);

    Criteria getOne(String id);

    Integer getTotalWeight();

}
