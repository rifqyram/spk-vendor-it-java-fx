package ac.unindra.spk_vendor_it.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class ComboBoxValueModel<T> {
    private StringProperty label;

    @Setter
    @Getter
    private T value;

    public ComboBoxValueModel(String label, T value) {
        this.label = new SimpleStringProperty(label);
        this.value = value;
    }

    public String getLabel() {
        return label.get();
    }

    public StringProperty labelProperty() {
        return label;
    }

    public void setLabel(String label) {
        this.label.set(label);
    }

}
