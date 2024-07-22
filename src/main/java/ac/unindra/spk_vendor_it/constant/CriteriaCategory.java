package ac.unindra.spk_vendor_it.constant;

import lombok.Getter;

@Getter
public enum CriteriaCategory {
    BENEFIT("Benefit"),
    COST("Cost");

    private final String description;

    CriteriaCategory(String description) {
        this.description = description;
    }

    public static CriteriaCategory fromString(String text) {
        for (CriteriaCategory category : CriteriaCategory.values()) {
            if (category.description.equalsIgnoreCase(text)) {
                return category;
            }
        }
        return null;
    }
}
