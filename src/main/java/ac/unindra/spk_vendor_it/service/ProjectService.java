package ac.unindra.spk_vendor_it.service;

import ac.unindra.spk_vendor_it.entity.Project;
import ac.unindra.spk_vendor_it.model.PageModel;
import ac.unindra.spk_vendor_it.model.ProjectModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProjectService {
    void create(ProjectModel projectModel);
    void update(ProjectModel projectModel);
    Page<ProjectModel> getAll(PageModel pageModel);
    List<ProjectModel> getAll();
    void delete(String id);

    Project getOne(String id);
}
