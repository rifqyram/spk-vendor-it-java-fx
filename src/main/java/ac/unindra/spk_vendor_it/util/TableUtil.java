package ac.unindra.spk_vendor_it.util;

import atlantafx.base.theme.Styles;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

import java.util.function.Function;

public class TableUtil {

    public static <T> TableCell<T, Void> setTableActions(TableAction<TableView<T>, Integer> editCallback, TableAction<TableView<T>, Integer> deleteCallback) {
        return new TableCell<>() {
            private final Button editButton = new Button("", new FontIcon(Material2AL.EDIT));
            private final Button deleteButton = new Button("", new FontIcon(Material2AL.DELETE));
            private final HBox pane = new HBox(editButton, deleteButton);

            {
                editButton.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.ACCENT, Styles.ROUNDED);
                deleteButton.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.DANGER, Styles.ROUNDED);

                editButton.setOnAction(event -> editCallback.accept(getTableView(), getIndex()));
                deleteButton.setOnAction(event -> deleteCallback.accept(getTableView(), getIndex()));

                pane.setSpacing(10);
                HBox.setMargin(editButton, new Insets(12, 0, 12, 0));
                HBox.setMargin(deleteButton, new Insets(12, 0, 12, 0));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                    setTooltip(new Tooltip("Edit / Delete"));
                }
            }
        };
    }

    public static <T> TableCell<T, Void> setTableDeleteAction(TableAction<TableView<T>, Integer> deleteCallback) {
        return new TableCell<>() {
            private final Button deleteButton = new Button("", new FontIcon(Material2AL.DELETE));

            {
                deleteButton.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.DANGER, Styles.ROUNDED);
                deleteButton.setOnAction(event -> deleteCallback.accept(getTableView(), getIndex()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                    setTooltip(new Tooltip("Delete"));
                }
            }
        };
    }

    public static <T> void setTableSequence(TableColumn<T, Integer> column) {
        column.setCellFactory(col -> setSequenceCol());
    }

    private static <T> TableCell<T, Integer> setSequenceCol() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        };
    }

    public static <K, V> TableCell<K, V> setTableObject(Function<V, String> function) {
        return new TableCell<>() {
            @Override
            protected void updateItem(V item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(function.apply(item));
                }
            }
        };
    }

    public static void setColumnResizePolicy(TableView<?> table) {
        table.setSelectionModel(null);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
    }

    public static <T> TableCell<T, Void> createDetailButtonCell(TableAction<TableView<T>, Integer> actionCallback) {
        return new TableCell<>() {
            private final Hyperlink detail = new Hyperlink("Detail");

            {
                detail.getStyleClass().addAll(Styles.ACCENT);
                detail.setOnAction(event -> actionCallback.accept(getTableView(), getIndex()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(detail);
                }
            }
        };

    }


    public interface TableAction<T, S> {
        void accept(T table, S index);
    }
}
