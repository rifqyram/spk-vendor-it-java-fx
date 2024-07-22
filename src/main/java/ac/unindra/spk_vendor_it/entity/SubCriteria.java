package ac.unindra.spk_vendor_it.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "m_sub_criteria")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SubCriteria extends BaseEntity {
    @Column(name = "weight", nullable = false)
    private Integer weight;

    @ManyToOne
    @JoinColumn(name = "criteria_id")
    @JsonBackReference
    private Criteria criteria;

    @Column(name = "description", nullable = false)
    private String description;
}
