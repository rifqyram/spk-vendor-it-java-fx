package ac.unindra.spk_vendor_it.service.impl;

import ac.unindra.spk_vendor_it.service.JasperService;
import ac.unindra.spk_vendor_it.util.NotificationUtil;
import javafx.scene.layout.Pane;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.fill.ReportFiller;
import net.sf.jasperreports.view.JasperViewer;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
public class JasperServiceImpl implements JasperService {
    @Override
    public void createReport(Pane ownerPane, String jasperFilename, List<Map<String, Object>> dataList, Map<String, Object> params) {
        try (InputStream inputStream = ReportFiller.class.getResourceAsStream("/jasper/" + jasperFilename + ".jrxml")) {
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException | IOException e) {
            NotificationUtil.showNotificationError(ownerPane, "Gagal Mencetak Report");
            throw new RuntimeException(e);
        }

    }

    @Override
    public JasperReport loadReport(String name) {
        try (InputStream inputStream = ReportFiller.class.getResourceAsStream("/jasper/" + name + ".jrxml")) {
            return JasperCompileManager.compileReport(inputStream);
        } catch (JRException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
