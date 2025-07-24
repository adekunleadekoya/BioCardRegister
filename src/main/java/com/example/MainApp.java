import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.example.utils.UserCredentials;

public class MainApp extends Application {
@Override
public void start(Stage primaryStage) throws Exception {
    // Load the main view
    Parent root = FXMLLoader.load(getClass().getResource("/com/example/views/MainView.fxml"));

    // Create a menu bar
    MenuBar menuBar = new MenuBar();

    // Create the "App" menu
    Menu appMenu = new Menu("App");

    // Create the "Settings" menu item
    MenuItem settingsMenuItem = new MenuItem("Settings");
    settingsMenuItem.setOnAction(event -> showSettingsDialog());

    // Add the "Settings" menu item to the "App" menu
    appMenu.getItems().add(settingsMenuItem);

    // Add the "App" menu to the menu bar
    menuBar.getMenus().add(appMenu);

    // Create a layout and add the menu bar and main view
    BorderPane layout = new BorderPane();
    layout.setTop(menuBar); // Place the menu bar at the top
    layout.setCenter(root); // Place the main view directly below the menu bar

    // Set the size of the window
    Scene scene = new Scene(layout, 800, 400); // Width: 800, Height: 600
    primaryStage.setScene(scene);

    primaryStage.setTitle("BestBio - 1.0.0");
    primaryStage.initStyle(StageStyle.DECORATED);
    primaryStage.setResizable(false); // Disable resizing to remove maximize button
    primaryStage.show();
}


    private void showSettingsDialog() {
        // Create a new Stage for the dialog
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL); // Block interaction with other windows
        dialog.setTitle("Settings");

        // Create a GridPane for the dialog layout
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Add username label and text field
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setMaxWidth(Double.MAX_VALUE);

        // Add password label and text field
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setMaxWidth(Double.MAX_VALUE);

        // Add submit button
        Button submitButton = new Button("Submit");
        submitButton.setMaxWidth(Double.MAX_VALUE);
        submitButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            // Store the username and password in the UserCredentials class
            UserCredentials.setUsername(usernameField.getText());
            UserCredentials.setPassword(passwordField.getText());
            dialog.close(); // Close the dialog after submission
        });

        // Add elements to the GridPane
        gridPane.add(usernameLabel, 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(passwordLabel, 0, 1);
        gridPane.add(passwordField, 1, 1);
        gridPane.add(submitButton, 0, 2, 2, 1); // Span the button across two columns

        // Create a scene for the dialog
        Scene dialogScene = new Scene(gridPane, 300, 200);
        dialog.setScene(dialogScene);

        // Show the dialog
        dialog.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}