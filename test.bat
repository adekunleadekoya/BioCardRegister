@echo off
REM Set the path to the JavaFX SDK
set JAVAFX_PATH=C:\Program Files\Java\javafx-sdk-24.0.1\lib
set JAVAFX_PATH=C:\Dekun - files\RUN - 2024\Biometric Project\BioApp\PreRegisterCards\my-javafx-app\ship\lib


REM Run the JAR file
java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -jar build\libs\my-javafx-app.jar

pause