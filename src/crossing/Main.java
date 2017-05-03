package crossing;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/layout.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setResizable(false);
        Scene scene = new Scene(root, 1000, 1000);
        primaryStage.setTitle("Crossing_Han Li v1.0");
        primaryStage.setScene(scene);
        primaryStage.show();
        Controller controller = fxmlLoader.getController();
        controller.init();


        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });

    }


    public static void main(String[] args) {
        launch(args);
    }
}
