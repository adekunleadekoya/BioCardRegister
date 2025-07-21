# BestBio

This mini app provides a user-friendly graphical interface. It enumerates the COM ports on a Windows machine. If a scanner is attached to any, connection can be esatablished. A disconnect buttons breaks any existing connection. Register card sends a card's serial number to 3way's authentication platform.

## Project Structure

```
my-javafx-app
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── com
│   │   │   │   └── example
│   │   │   │       ├── MainApp.java
│   │   │   │       ├── controllers
│   │   │   │       │   └── MainController.java
│   │   │   │       └── utils
│   │   │   │           └── SerialPortUtil.java
│   │   └── resources
│   │       └── com
│   │           └── example
│   │               └── views
│   │                   └── MainView.fxml
├── build.gradle
└── README.md
```

## Features

- Dropdown list to select available COM ports.
- Connect and disconnect buttons to manage serial port connections.
- Register button to send card's serial number to 3way's authentication platform
- Utilizes JavaFX for a modern user interface.

## Setup Instructions

1. Clone the repository:
   ```
   git clone <repository-url>
   ```

2. Navigate to the project directory:
   ```
   cd my-javafx-app
   ```

3. Build the project using Gradle:
   ```
   gradle build
   ```

4. Run the application:
   ```
   gradle  run

5. execute jar file
   ```  
   java --module-path modules/lib  --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar build/libs/my-javafx-app.jar

## Usage

- Upon startup, the application will display a form with a dropdown list of available COM ports.
- Select a COM port from the dropdown and click the "Connect" button to establish a connection.
- Click the "Disconnect" button to terminate the connection.

## Dependencies

This project requires JavaFX libraries. Ensure that the necessary dependencies are included in the `build.gradle` file.

## License

This project is licensed under the MIT License. See the LICENSE file for more details.