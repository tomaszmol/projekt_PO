package files.simulation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ParameterApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Załadowanie widoku FXML
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("parameterInput.fxml"));
        BorderPane viewRoot = loader.load();

        // Pobranie kontrolera z FXML
        //SimulationPresenter presenter = loader.getController();

        // Konfiguracja okna aplikacji
        configureStage(primaryStage, viewRoot);
        primaryStage.show();

    }

    private void configureStage(Stage primaryStage, BorderPane viewRoot) {
        var scene = new Scene(viewRoot);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Parameter Input");
        primaryStage.minWidthProperty().bind(viewRoot.minWidthProperty());
        primaryStage.minHeightProperty().bind(viewRoot.minHeightProperty());
    }

}
