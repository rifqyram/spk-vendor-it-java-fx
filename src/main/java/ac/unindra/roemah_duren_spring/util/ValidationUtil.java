package ac.unindra.roemah_duren_spring.util;

import atlantafx.base.theme.Styles;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.util.Pair;

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

    public static void resetValidation(Map<TextInputControl, Pair<Label, ValidationStrategy>> fields) {
        for (Map.Entry<TextInputControl, Pair<Label, ValidationStrategy>> entry : fields.entrySet()) {
            TextInputControl field = entry.getKey();
            Label errorLabel = entry.getValue().getKey();

            field.pseudoClassStateChanged(Styles.STATE_DANGER, false);
            errorLabel.setText("");
        }
    }

    @FunctionalInterface
    public interface ValidationStrategy {
        String validate(String input);
    }
}
