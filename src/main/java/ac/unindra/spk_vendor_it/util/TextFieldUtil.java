package ac.unindra.spk_vendor_it.util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.text.NumberFormat;
import java.util.Locale;

public class TextFieldUtil {
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    public static void changeValueToUppercase(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                textField.setText(newValue.toUpperCase());
            }
        });
    }

    public static void changeValueToCurrency(TextField currencyField) {
        currencyFormat.setMaximumFractionDigits(0);

        currencyField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE) {
                if (currencyField.getText().length() <= 3) {
                    currencyField.setText("Rp0");
                    currencyField.positionCaret(currencyField.getText().length());
                    event.consume();
                }
            }
        });

        currencyField.textProperty().addListener(new ChangeListener<>() {
            private boolean ignore = false;

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (ignore) {
                    return;
                }

                ignore = true;

                if (newValue.isEmpty() || newValue.equals("Rp0")) {
                    currencyField.setText("Rp0");
                    currencyField.positionCaret(currencyField.getText().length());
                } else {
                    String numericValue = newValue.replaceAll("\\D", "");
                    if (!numericValue.isEmpty()) {
                        try {
                            long parsedValue = Long.parseLong(numericValue);
                            String formatted = currencyFormat.format(parsedValue);
                            currencyField.setText(formatted);
                            currencyField.positionCaret(formatted.length());
                        } catch (NumberFormatException ignored) {
                        }
                    } else {
                        currencyField.setText("Rp0");
                    }
                }

                ignore = false;
            }
        });

        currencyField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            if (!keyEvent.getCharacter().matches("[0-9]")) {
                keyEvent.consume();
                java.awt.Toolkit.getDefaultToolkit().beep();
            }
        });
    }

    public static void changeValueToNumber(TextField qtyField) {
        qtyField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            if (!keyEvent.getCharacter().matches("[0-9]+")) {
                keyEvent.consume();
                java.awt.Toolkit.getDefaultToolkit().beep();
            }
        });
    }
}
