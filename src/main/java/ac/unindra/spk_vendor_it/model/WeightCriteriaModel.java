package ac.unindra.spk_vendor_it.model;

import ac.unindra.spk_vendor_it.entity.SubCriteria;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Builder;

public class WeightCriteriaModel {
    private final StringProperty id;
    private final IntegerProperty weight;
    private final StringProperty description;

    public WeightCriteriaModel() {
        this.id = new SimpleStringProperty();
        this.weight = new SimpleIntegerProperty();
        this.description = new SimpleStringProperty();
    }

    @Builder
    public WeightCriteriaModel(String id, Integer weight, String description) {
        this.id = new SimpleStringProperty(id);
        this.weight = new SimpleIntegerProperty(weight);
        this.description = new SimpleStringProperty(description);
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public int getWeight() {
        return weight.get();
    }

    public IntegerProperty weightProperty() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight.set(weight);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public SubCriteria toEntity() {
        return SubCriteria.builder()
                .id(this.getId())
                .weight(this.getWeight())
                .description(this.getDescription())
                .build();
    }

    public static WeightCriteriaModel fromEntity(SubCriteria subCriteria) {
        return WeightCriteriaModel.builder()
                .id(subCriteria.getId())
                .weight(subCriteria.getWeight())
                .description(subCriteria.getDescription())
                .build();
    }
}
