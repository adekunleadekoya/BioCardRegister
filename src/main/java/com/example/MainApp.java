import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

 

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/views/MainView.fxml"));
        primaryStage.setTitle("BestBio - 1.0.0");
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setResizable(false); // Disable resizing to remove maximize button
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}