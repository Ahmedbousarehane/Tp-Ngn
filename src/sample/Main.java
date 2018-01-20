package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.log4j.BasicConfigurator;

public class Main extends Application {

    static Stage stage;
    @Override
    public void start(Stage primaryStage) throws Exception{
        Platform.setImplicitExit(false);
        Pane root = FXMLLoader.load(getClass().getResource("home.fxml"));
        primaryStage.setTitle("Telephone");
        primaryStage.setScene(new Scene(root, 546, 234));
        primaryStage.show();
        stage = primaryStage;
    }


    public static void main(String[] args) {
        BasicConfigurator.configure();
        launch(args);

    }
}
