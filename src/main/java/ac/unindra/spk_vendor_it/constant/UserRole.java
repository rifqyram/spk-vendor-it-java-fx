package ac.unindra.spk_vendor_it.constant;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("Admin"),
    EMPLOYEE("Employee");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }
}
