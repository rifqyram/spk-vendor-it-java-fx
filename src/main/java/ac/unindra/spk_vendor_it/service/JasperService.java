package ac.unindra.spk_vendor_it.service;

import javafx.scene.layout.Pane;
import net.sf.jasperreports.engine.JasperReport;

import java.util.List;
import java.util.Map;

public interface JasperService {
    void createReport(Pane ownerPane, String jasperFilename, List<Map<String, Object>> dataList, Map<String, Object> params);

    public JasperReport loadReport(String name);
}
