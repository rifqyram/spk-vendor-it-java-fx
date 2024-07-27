package ac.unindra.spk_vendor_it.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class EvaluationResultModel {
    private DoubleProperty preference;

    @Getter
    @Setter
    private VendorModel vendor;
    private IntegerProperty rank;

    public EvaluationResultModel() {
        this.preference = new SimpleDoubleProperty();
        this.rank = new SimpleIntegerProperty();
    }

    @Builder
    public EvaluationResultModel(Double preference, VendorModel vendor, Integer rank) {
        this.preference = new SimpleDoubleProperty(preference);
        this.vendor = vendor;
        this.rank = new SimpleIntegerProperty(rank);
    }

    public double getPreference() {
        return preference.get();
    }

    public void setPreference(double preference) {
        this.preference.set(preference);
    }

    public int getRank() {
        return rank.get();
    }

    public void setRank(int rank) {
        this.rank.set(rank);
    }
}
