package ac.unindra.roemah_duren_spring.service;

import javafx.scene.layout.Pane;

import java.util.List;
import java.util.Map;

public interface JasperService {
    void createReport(Pane ownerPane, String path, List<Map<String, Object>> dataList, Map<String, Object> params);
}
