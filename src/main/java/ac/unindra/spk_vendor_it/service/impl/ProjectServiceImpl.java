package ac.unindra.spk_vendor_it.service.impl;

import ac.unindra.spk_vendor_it.constant.ResponseMessage;
import ac.unindra.spk_vendor_it.entity.Project;
import ac.unindra.spk_vendor_it.model.PageModel;
import ac.unindra.spk_vendor_it.model.ProjectModel;
import ac.unindra.spk_vendor_it.repository.ProjectRepository;
import ac.unindra.spk_vendor_it.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void create(ProjectModel projectModel) {
        Project project = projectModel.toEntity();
        projectRepository.saveAndFlush(project);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(ProjectModel projectModel) {
        findProjectById(projectModel.getId());
        Project project = projectModel.toEntity();
        projectRepository.saveAndFlush(project);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ProjectModel> getAll(PageModel pageModel) {
        Pageable pageable = PageRequest.of(pageModel.getPage(), pageModel.getSize());
        Specification<Project> projectSpecification = Specification.where(ProjectRepository.hasSearchQuery(pageModel.getQuery()));
        Page<Project> projects = projectRepository.findAll(projectSpecification, pageable);
        return projects.map(ProjectModel::fromEntity);
    }

    @Override
    public List<ProjectModel> getAll() {
        return projectRepository.findAll().stream().map(ProjectModel::fromEntity).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String id) {
        try {
            Project project = findProjectById(id);
            projectRepository.delete(project);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Data tidak dapat dihapus karena terdapat data yang terkait");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Project getOne(String id) {
        return findProjectById(id);
    }

    @Transactional(readOnly = true)
    protected Project findProjectById(String id) {
        return projectRepository.findById(id).orElseThrow(() -> new RuntimeException(ResponseMessage.DATA_NOT_FOUND));
    }
}
