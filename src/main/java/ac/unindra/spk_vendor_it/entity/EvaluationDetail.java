package ac.unindra.spk_vendor_it.entity;

import ac.unindra.spk_vendor_it.constant.CriteriaCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "t_evaluation_detail")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class EvaluationDetail extends BaseEntity {
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "evaluation_id", nullable = false)
    private Evaluation evaluation;

    @Column(name = "criteria_id", nullable = false)
    private String criteriaId;

    @Column(name = "criteria_name", nullable = false)
    private String criteriaName;

    @Column(name = "criteria_description", nullable = false)
    private String criteriaDescription;

    @Column(name = "criteria_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private CriteriaCategory criteriaCategory;

    @Column(name = "criteria_weight", nullable = false)
    private Integer criteriaWeight;

    @Column(name = "sub_criteria_id")
    private String subCriteriaDescription;

    @Column(name = "sub_criteria_weight")
    private Integer subCriteriaWeight;

    @Column(name = "score", nullable = false)
    private Integer score;
}
