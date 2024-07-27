package ac.unindra.spk_vendor_it.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
public class EvaluationDetailModel {
    private VendorModel vendor;
    private Map<String, Integer> criteria;

    @Builder
    public EvaluationDetailModel(VendorModel vendor, Map<String, Integer> criteria) {
        this.vendor = vendor;
        this.criteria = criteria;
    }

    public EvaluationDetailModel() {
        this.criteria = new HashMap<>();
    }
}
