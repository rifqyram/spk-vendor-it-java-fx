package ac.unindra.spk_vendor_it;

import ac.unindra.spk_vendor_it.util.FXMLUtil;
import atlantafx.base.theme.NordLight;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFxApplication extends Application {
    private static ConfigurableApplicationContext applicationContext;

    @Override
    public void start(Stage primaryStage) {
        applicationContext.publishEvent(new StageReadyEvent(primaryStage));
        Application.setUserAgentStylesheet(new NordLight().getUserAgentStylesheet());

        Parent root = FXMLUtil.loadFXML("login");
        String title = "Login | SPK Vendor IT";
        boolean resizable = false;

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
        primaryStage.setResizable(resizable);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(SPKVendorITSpringApplication.class)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    public static <T> T getBean(Class<T> type) {
        return applicationContext.getBean(type);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        applicationContext.close();
        Platform.exit();
    }
}
