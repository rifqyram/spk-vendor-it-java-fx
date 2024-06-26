package ac.unindra.roemah_duren_spring.util;

import atlantafx.base.theme.Styles;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.util.Pair;
import org.springframework.util.StringUtils;

import java.util.Map;

public class ValidationUtil {
    public static boolean isFormValid(Map<TextInputControl, Pair<Label, ValidationStrategy>> fields) {
        boolean formIsValid = true;
        for (Map.Entry<TextInputControl, Pair<Label, ValidationStrategy>> entry : fields.entrySet()) {
            TextInputControl field = entry.getKey();
            Label errorLabel = entry.getValue().getKey();
            ValidationStrategy strategy = entry.getValue().getValue();

            String validationResult = strategy.validate(field.getText());
            boolean fieldIsValid = validationResult == null || validationResult.isEmpty();
            field.pseudoClassStateChanged(Styles.STATE_DANGER, !fieldIsValid);
            errorLabel.setText(fieldIsValid ? "" : validationResult);

            if (!fieldIsValid) {
                formIsValid = false;
            }
        }
        return formIsValid;
    }

    public static boolean isFormValidComboBox(Map<ComboBoxBase<?>, Pair<Label, ValidationStrategyComboBox>> fields) {
        boolean formIsValid = true;
        for (Map.Entry<ComboBoxBase<?>, Pair<Label, ValidationStrategyComboBox>> entry : fields.entrySet()) {
            ComboBoxBase<?> field = entry.getKey();
            Label errorLabel = entry.getValue().getKey();
            ValidationStrategyComboBox strategy = entry.getValue().getValue();

            String validationResult = strategy.validate(field);
            boolean fieldIsValid = validationResult == null || validationResult.isEmpty();
            field.pseudoClassStateChanged(Styles.STATE_DANGER, !fieldIsValid);
            errorLabel.setText(fieldIsValid ? "" : validationResult);

            if (!fieldIsValid) {
                formIsValid = false;
            }
        }
        return formIsValid;
    }


    public static void resetValidation(Map<TextInputControl, Pair<Label, ValidationStrategy>> fields) {
        for (Map.Entry<TextInputControl, Pair<Label, ValidationStrategy>> entry : fields.entrySet()) {
            TextInputControl field = entry.getKey();
            Label errorLabel = entry.getValue().getKey();

            field.pseudoClassStateChanged(Styles.STATE_DANGER, false);
            errorLabel.setText("");
        }
    }

    public static void resetValidationComboBox(Map<ComboBoxBase<?>, Pair<Label, ValidationStrategyComboBox>> fields) {
        for (Map.Entry<ComboBoxBase<?>, Pair<Label, ValidationStrategyComboBox>> entry : fields.entrySet()) {
            ComboBoxBase<?> field = entry.getKey();
            Pair<Label, ValidationStrategyComboBox> strategy = entry.getValue();
            Label errorLabel = strategy.getKey();

            errorLabel.setText("");
            field.pseudoClassStateChanged(Styles.STATE_DANGER, false);
        }
    }


    public static boolean isValidEmail(String input) {
        return input.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
    }

    public static boolean isValidPhoneNumber(String input) {
        return input.matches("[0-9]{7,13}");
    }

    @FunctionalInterface
    public interface ValidationStrategy {
        ValidationStrategy REQUIRED = input -> input.isEmpty() ? "Field ini wajib diisi" : null;
        ValidationStrategy EMAIL = input -> {
            if (!StringUtils.hasText(input)) {
                return "Field ini wajib diisi";
            } else if (!ValidationUtil.isValidEmail(input)) {
                return "Format email tidak valid";
            }
            return "";
        };
        ValidationStrategy PHONE_NUMBER = input -> {
            if (!StringUtils.hasText(input)) {
                return "Field ini wajib diisi";
            } else if (input.startsWith("0") || input.startsWith("+62")) {
                return "Nomor telepon tidak boleh diawali dengan 0 atau +62";
            } else if (!ValidationUtil.isValidPhoneNumber(input)) {
                return "Nomor telepon minimal 10 digit dan maksimal 15 digit";
            }
            return "";
        };
        ValidationStrategy NUMBER = input -> {
            if (!StringUtils.hasText(input)) {
                return "Field ini wajib diisi";
            } else if (!input.matches("[0-9]+")) {
                return "Field ini hanya boleh diisi dengan angka";
            }
            return "";
        };
        ValidationStrategy PASSWORD = input -> {
            if (!StringUtils.hasText(input)) {
                return "Field ini wajib diisi";
            } else if (input.length() < 8) {
                return "Password minimal 8 karakter";
            }
            return "";
        };

        String validate(String input);
    }

    @FunctionalInterface
    public interface ValidationStrategyComboBox {

        String validate(ComboBoxBase<?> input);
    }
}
