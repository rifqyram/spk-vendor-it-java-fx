package ac.unindra.spk_vendor_it.util;

import javafx.scene.control.ListCell;

import java.util.function.Function;

public class ComboBoxUtil {
    public static <T> ListCell<T> getComboBoxListCell(Function<T, String> nameExtractor) {
        return new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(nameExtractor.apply(item));
                }
            }
        };
    }
}
