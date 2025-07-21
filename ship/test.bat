@echo off
REM Set the path to the JavaFX SDK relative to the batch file
set JAVAFX_PATH=%~dp0lib
set JAVAFX_PATH=C:\Dekun - files\RUN - 2024\Biometric Project\BioApp\PreRegisterCards\my-javafx-app\ship\lib

REM Echo the JavaFX path to verify it
echo JAVAFX_PATH is set to: "%JAVAFX_PATH%"


REM Run the JAR file 

java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -jar "%~dp0my-javafx-app.jar"

pause