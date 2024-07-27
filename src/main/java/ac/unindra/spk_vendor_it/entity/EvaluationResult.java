package ac.unindra.spk_vendor_it.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "t_evaluation_result")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class EvaluationResult extends BaseEntity {
    @Column(name = "total_score", nullable = false)
    private Double totalScore;

    @OneToMany(mappedBy = "evaluationResult")
    private List<Evaluation> evaluations;
}
