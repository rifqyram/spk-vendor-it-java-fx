package ac.unindra.roemah_duren_spring;

import ac.unindra.roemah_duren_spring.constant.ConstantPage;
import ac.unindra.roemah_duren_spring.repository.TokenManager;
import ac.unindra.roemah_duren_spring.util.FXMLUtil;
import atlantafx.base.theme.NordLight;
import atlantafx.base.theme.PrimerLight;
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
        TokenManager tokenManager = applicationContext.getBean(TokenManager.class);

        Parent root;
        String title = "Roemah Duren";
        boolean resizable = true;
        if (tokenManager.getToken() != null) {
            root = FXMLUtil.loadFXML(ConstantPage.MAIN_APP);
        } else {
            root = FXMLUtil.loadFXML(ConstantPage.LOGIN);
            title = "Login | Roemah Duren";
            resizable = false;
        }

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
        primaryStage.setResizable(resizable);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(RoemahDurenSpringApplication.class)
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
