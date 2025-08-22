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
import javafx.scene.image.Image;

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

    // Create the "About" menu item
    MenuItem aboutMenuItem = new MenuItem("About");
    aboutMenuItem.setOnAction(event -> showAboutDialog()); // Call the About dialog method

    // Add the "Settings" and "About" menu items to the "App" menu
    appMenu.getItems().addAll(settingsMenuItem, aboutMenuItem);

    // Add the "App" menu to the menu bar
    menuBar.getMenus().add(appMenu);

    // Create a layout and add the menu bar and main view
    BorderPane layout = new BorderPane();
    layout.setTop(menuBar); // Place the menu bar at the top
    layout.setCenter(root); // Place the main view directly below the menu bar


    // Set the size of the window
    Scene scene = new Scene(layout, 800, 400); // Width: 800, Height: 600
    scene.getStylesheets().add(getClass().getResource("/com/example/views/style.css").toExternalForm());

    primaryStage.setScene(scene);
    primaryStage.getIcons().add(new Image(getClass().getResource("/com/example/assets/logo.png").toExternalForm()));
    primaryStage.setTitle("BestBio - 1.0.0");
    primaryStage.initStyle(StageStyle.DECORATED);
    primaryStage.setResizable(false); // Disable resizing to remove maximize button
    primaryStage.show();
}

 private void showAboutDialog() {
    // Create a new Stage for the dialog
    Stage aboutDialog = new Stage();
    aboutDialog.initModality(Modality.APPLICATION_MODAL); // Block interaction with other windows
    aboutDialog.setTitle("About");
    aboutDialog.setResizable(false);
    aboutDialog.getIcons().add(new Image(getClass().getResource("/com/example/assets/logo.png").toExternalForm()));

    // Create a GridPane for the dialog layout
    GridPane gridPane = new GridPane();
    gridPane.setPadding(new Insets(20));
    gridPane.setHgap(10);
    gridPane.setVgap(10);

    // Add the app name and build date
    Label appNameLabel = new Label("App Name: BestBio");
    Label buildDateLabel = new Label("Build Date: Aug 22, 2025");

    // Add elements to the GridPane
    gridPane.add(appNameLabel, 0, 0);
    gridPane.add(buildDateLabel, 0, 1);

    // Create a scene for the dialog
    Scene aboutScene = new Scene(gridPane, 300, 150);
    aboutScene.getStylesheets().add(getClass().getResource("/com/example/views/style.css").toExternalForm());

    aboutDialog.setScene(aboutScene);

    // Show the dialog
    aboutDialog.showAndWait();
}
private void showSettingsDialog() {
    // Create a new Stage for the dialog
    Stage dialog = new Stage();    
    dialog.setTitle("Settings");
    // Make the dialog non-resizable
    dialog.setResizable(false);
     // Set the icon for the dialog
    dialog.getIcons().add(new Image(getClass().getResource("/com/example/assets/logo.png").toExternalForm()));
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

    // Add API label and text field
    Label apiKeyLabel = new Label("API Key:");
    TextField apiKeyField = new TextField();
    apiKeyField.setMaxWidth(Double.MAX_VALUE);


    // Add submit button
    Button submitButton = new Button("Submit");
    submitButton.setMaxWidth(Double.MAX_VALUE);
    submitButton.setOnAction(event -> {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String apiKey =   apiKeyField.getText();
        // Store the username and password in the UserCredentials class
        UserCredentials.setUsername(usernameField.getText());
        UserCredentials.setPassword(passwordField.getText());
        UserCredentials.setApiKey(apiKeyField.getText());   
        dialog.close(); // Close the dialog after submission
    });

    // Add elements to the GridPane
    gridPane.add(usernameLabel, 0, 0);
    gridPane.add(usernameField, 1, 0);
    gridPane.add(passwordLabel, 0, 1);
    gridPane.add(passwordField, 1, 1);
    gridPane.add(apiKeyLabel, 0, 2);
    gridPane.add(apiKeyField, 1, 2);
    gridPane.add(submitButton, 0, 3, 2, 1); // Span the button across two columns

    // Create a scene for the dialog
    Scene dialogScene = new Scene(gridPane, 300, 240);

    // Apply the same stylesheet to the dialog
    dialogScene.getStylesheets().add(getClass().getResource("/com/example/views/style.css").toExternalForm());
    dialog.setScene(dialogScene); 
    // Show the dialog
    dialog.showAndWait();
}
    public static void main(String[] args) {
        launch(args);
    }
}