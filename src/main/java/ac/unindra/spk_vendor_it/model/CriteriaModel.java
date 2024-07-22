package ac.unindra.spk_vendor_it.model;

import ac.unindra.spk_vendor_it.constant.CriteriaCategory;
import ac.unindra.spk_vendor_it.entity.Criteria;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class CriteriaModel {
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty description;
    private final StringProperty category;
    private final IntegerProperty weight;

    @Setter
    @Getter
    private List<WeightCriteriaModel> weightCriteria;

    public CriteriaModel() {
        this.id = new SimpleStringProperty();
        this.name = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.category = new SimpleStringProperty();
        this.weight = new SimpleIntegerProperty();
        this.weightCriteria = new ArrayList<>();
    }

    @Builder
    public CriteriaModel(String id, String name, String description, String category, Integer weight, List<WeightCriteriaModel> weightCriteria) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.category = new SimpleStringProperty(category);
        this.weight = new SimpleIntegerProperty(weight);
        this.weightCriteria = weightCriteria;
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

    public String getCategory() {
        return category.get();
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public int getWeight() {
        return weight.get();
    }

    public void setWeight(int weight) {
        this.weight.set(weight);
    }

    public Criteria toEntity() {
        return Criteria.builder()
                .id(this.getId())
                .name(this.getName())
                .description(this.getDescription())
                .category(CriteriaCategory.fromString(this.getCategory()))
                .weight(this.getWeight())
                .subCriteria(this.getWeightCriteria().stream().map(WeightCriteriaModel::toEntity).toList())
                .build();
    }

    public static CriteriaModel fromEntity(Criteria criteria) {
        return CriteriaModel.builder()
                .id(criteria.getId())
                .name(criteria.getName())
                .description(criteria.getDescription())
                .category(criteria.getCategory().getDescription())
                .weight(criteria.getWeight())
                .weightCriteria(criteria.getSubCriteria().stream().map(WeightCriteriaModel::fromEntity).toList())
                .build();
    }
}
