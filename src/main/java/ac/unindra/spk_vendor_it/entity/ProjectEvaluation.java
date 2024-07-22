package ac.unindra.spk_vendor_it.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "t_project_evaluation")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ProjectEvaluation extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    private LocalDateTime evaluationDate;

    @OneToMany(mappedBy = "projectEvaluation", cascade = CascadeType.ALL)
    private List<Evaluation> evaluations;
}
