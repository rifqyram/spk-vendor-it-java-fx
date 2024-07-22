package ac.unindra.spk_vendor_it.model;

import ac.unindra.spk_vendor_it.entity.Project;
import ac.unindra.spk_vendor_it.util.DateUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class ProjectModel {
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty description;

    @Getter
    private final LocalDate startDate;

    @Getter
    private final LocalDate endDate;

    public ProjectModel() {
        this.id = new SimpleStringProperty();
        this.name = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.startDate = LocalDate.now();
        this.endDate = LocalDate.now();
    }

    @Builder
    public ProjectModel(String id, String name, String description, LocalDate startDate, LocalDate endDate) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public Project toEntity() {
        return Project.builder()
                .id(getId())
                .name(getName())
                .description(getDescription())
                .startDate(getStartDate())
                .endDate(getEndDate())
                .build();
    }

    public static ProjectModel fromEntity(Project project) {
        return ProjectModel.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .build();
    }
}
