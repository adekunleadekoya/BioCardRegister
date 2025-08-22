package com.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import com.example.utils.SerialPortUtil;
import com.fazecast.jSerialComm.SerialPort;
import java.util.function.Consumer;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
 
import org.json.JSONObject;
import com.example.utils.UserCredentials;

public class MainController {
    // Path to the token file, accessible from any function
    private static final String TOKEN_FILE_PATH = "token.txt";
    // Cached base URL
    private static String cachedBaseUrl = null;

    // Reads the base URL from config.txt once and caches it
    private static String getBaseUrl() {
        if (cachedBaseUrl != null) {
            return cachedBaseUrl;
        }
        try {
            java.nio.file.Path path = java.nio.file.Paths.get("config.txt");
            cachedBaseUrl = java.nio.file.Files.readAllLines(path).get(0).trim();
        } catch (Exception e) {
            System.err.println("Error reading base URL from config.txt: " + e.getMessage());
            cachedBaseUrl = "https://run.fintecgrate.com/"; // fallback
        }
        return cachedBaseUrl;
    }

    // Callback for the device serial number
    private Consumer<String> deviceSerialNumberCallback;
    // Method to set the callback

    public void setDeviceSerialNumberCallback(Consumer<String> callback) {
        this.deviceSerialNumberCallback = callback;
    }

    @FXML
    private ComboBox<String> comPortDropdown;

    @FXML
    private Button connectButton;

    @FXML
    private Button disconnectButton;

    @FXML
    private Button serialNumberBtn;
    @FXML
    private Button registerCardBtn;
    @FXML
    private Button readCardSNBtn;
    @FXML
    private Button validateCardBtn;

    private String selectedPort = null;

    public void initialize() {
        populateComPortDropdown();
    }

    private void populateComPortDropdown() {
        // Ensure comPortDropdown is not null before accessing it
        if (comPortDropdown != null) {
            comPortDropdown.getItems().addAll(SerialPortUtil.listAvailablePorts());
        } else {
            System.err.println("comPortDropdown is null. Check your FXML file.");
        }
    }

    @FXML
    private void handleConnect() {
        selectedPort = comPortDropdown.getValue();
        if (selectedPort != null) {
            SerialPortUtil.connect(selectedPort);
            // Configure the serial port parameters
            SerialPortUtil.configurePort(115200, 8, SerialPort.NO_PARITY, SerialPort.ONE_STOP_BIT, 3000, 3000);

        } else {
            System.out.println("No port selected.");
        }
    }

    @FXML
    private void handleDisconnect() {
        SerialPortUtil.disconnect();
        System.out.println("Disconnected.");
    }
 

 @FXML
    private void handleReadCardSN() {

        // Pass the callback to handle the card serial number
        getCardSerialNumber(serialNumber -> {
            System.out.println("Reading Card Serial Number..."  +  serialNumber);     
        });    
       
    }

@FXML
    private void handleValidateCard()
    {
        try {
            // Read token from file
            String Authtoken = null;
            java.nio.file.Path tokenPath = java.nio.file.Paths.get(TOKEN_FILE_PATH);
            if (java.nio.file.Files.exists(tokenPath)) {
                try {
                    Authtoken = java.nio.file.Files.readAllLines(tokenPath).get(0).trim();
                } catch (Exception e) {
                    System.err.println("Error reading token from token.txt: " + e.getMessage());
                }
            }

            String username = UserCredentials.getUsername();
            String password = UserCredentials.getPassword();

            final String[] tokenToUse = new String[]{Authtoken};
            getCardSerialNumber(serialNumber -> {
                if (tokenToUse[0] == null || tokenToUse[0].isEmpty()) {
                    tokenToUse[0] = getAuthToken(username, password);
                    if (tokenToUse[0] != null) {
                        try {
                            java.nio.file.Files.write(tokenPath, java.util.Collections.singleton(tokenToUse[0]));
                        } catch (Exception e) {
                            System.err.println("Error writing token to token.txt: " + e.getMessage());
                        }
                    }
                }

                String response = validateCardVia3wc(tokenToUse[0], serialNumber);
                System.out.println("ValidateCardVia3wc: " + response);

                // If response contains 'Unauthorized', get new token and retry
                if (response != null && response.toLowerCase().contains("unauthorized")) {
                    tokenToUse[0] = getAuthToken(username, password);
                    if (tokenToUse[0] != null) {
                        try {
                            java.nio.file.Files.write(tokenPath, java.util.Collections.singleton(tokenToUse[0]));
                        } catch (Exception e) {
                            System.err.println("Error writing token to token.txt: " + e.getMessage());
                        }
                        response = validateCardVia3wc(tokenToUse[0], serialNumber);
                        System.out.println("ValidateCardVia3wc (retry): " + response);
                    } else {
                        System.out.println("Failed to get new authentication token after Unauthorized.");
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("Error in handleValidateCard: " + e.getMessage());
        }
    }

    @FXML

    private void handleRegisterCard() {
        try {
            // Read token from file
            String Authtoken = null;
            java.nio.file.Path tokenPath = java.nio.file.Paths.get(TOKEN_FILE_PATH);
            if (java.nio.file.Files.exists(tokenPath)) {
                try {
                    Authtoken = java.nio.file.Files.readAllLines(tokenPath).get(0).trim();
                } catch (Exception e) {
                    System.err.println("Error reading token from token.txt: " + e.getMessage());
                }
            }

            String username = UserCredentials.getUsername();
            String password = UserCredentials.getPassword();
            String apiKey = UserCredentials.getApiKey();

            final String[] tokenToUse = new String[]{Authtoken};
            getCardSerialNumber(serialNumber -> {
                if (tokenToUse[0] == null || tokenToUse[0].isEmpty()) {
                    tokenToUse[0] = getAuthToken(username, password);
                    if (tokenToUse[0] != null) {
                        try {
                            java.nio.file.Files.write(tokenPath, java.util.Collections.singleton(tokenToUse[0]));
                        } catch (Exception e) {
                            System.err.println("Error writing token to token.txt: " + e.getMessage());
                        }
                    }
                }

                String resp = registerCardVia3wc(tokenToUse[0], apiKey, serialNumber);
                System.out.println("RegisterCardVia3wc: " + resp);

                // If response contains 'Unauthorized', get new token and retry
                if (resp != null && resp.toLowerCase().contains("unauthorized")) {
                    tokenToUse[0] = getAuthToken(username, password);
                    if (tokenToUse[0] != null) {
                        try {
                            java.nio.file.Files.write(tokenPath, java.util.Collections.singleton(tokenToUse[0]));
                        } catch (Exception e) {
                            System.err.println("Error writing token to token.txt: " + e.getMessage());
                        }
                        resp = registerCardVia3wc(tokenToUse[0], apiKey, serialNumber);
                        System.out.println("RegisterCardVia3wc (retry): " + resp);
                    } else {
                        System.out.println("Failed to get new authentication token after Unauthorized.");
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("Error in handleRegisterCard: " + e.getMessage());
        }
    }

  @FXML
private void getCardSerialNumber(Consumer<String> callback) {
    try {        
        byte cmdid = CMD_CARDSN; // Get serial number of card
        mCmdSize = 0;
        SerialPortUtil.setDataReceivedCallback(data -> {
            try {              
                System.arraycopy(data, 0, mCmdData, mCmdSize, data.length);
                int datasize = (byte) (mCmdData[5]) + (mCmdData[6] << 8 & 0xFF00) - 1;
                if (datasize > 0) {
                    byte[] cardsn = new byte[64];
                    System.arraycopy(mCmdData, 8, cardsn, 0, datasize);
                    // Convert the card serial number to a hexadecimal string
                    String sn = Integer.toHexString(cardsn[0] & 0xFF)
                            + Integer.toHexString(cardsn[1] & 0xFF)
                            + Integer.toHexString(cardsn[2] & 0xFF)
                            + Integer.toHexString(cardsn[3] & 0xFF);                   

                    // Invoke the callback with the serial number
                    if (callback != null) {
                        //System.out.println("Card Serial Number: " + sn);
                        callback.accept(sn);
                    }
                } else {
                    System.out.println("Invalid data size received.");
                }

                // Clear the callback after use
                SerialPortUtil.setDataReceivedCallback(null);
            } catch (Exception e) {
                System.err.println("Error processing card serial number: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // Send the command to get the card serial number
        sendCommand(cmdid, null, 0);
    } catch (Exception e) {
        System.err.println("Error in getCardSerialNumber: " + e.getMessage());
        e.printStackTrace();
    }
}

    @FXML
    private void handleGetScannerSN() {

        try {

            byte cmdid = CMD_GETSN; // get serial number of scanner
            mCmdSize = 0;
            SerialPortUtil.setDataReceivedCallback(data -> {
                System.arraycopy(data, 0, mCmdData, mCmdSize, data.length);
                int datasize = (byte) (mCmdData[5]) + (mCmdData[6] << 8 & 0xFF00) - 1;
                if (mCmdData[7] == 1) {
                    byte[] snb = new byte[32];
                    System.arraycopy(mCmdData, 8, snb, 0, datasize);
                    System.out.println("sn: " + " " + new String(snb));

                }

                SerialPortUtil.setDataReceivedCallback(null); // Clear the callback after use

            });
            sendCommand(cmdid, null, 0);
        } catch (Exception e) {
            System.out.println("Error getting scanner SN: " + e.getMessage());
        }

    }

    public void sendCommand(byte cmdid, byte[] data, int size) {
        if (SerialPortUtil.getPort() == null || !SerialPortUtil.getPort().isOpen()) {
            return; // Do nothing if the serial port is null or not open
        }

        int sendsize = 9 + size;
        byte[] sendbuf = new byte[sendsize];
        sendbuf[0] = 0x46; // 'F'
        sendbuf[1] = 0x54; // 'T'
        sendbuf[2] = 0x00;
        sendbuf[3] = 0x00;
        sendbuf[4] = cmdid;
        sendbuf[5] = (byte) size;
        sendbuf[6] = (byte) (size >> 8);

        if (size > 0 && data != null) {
            for (int i = 0; i < size; i++) {
                sendbuf[7 + i] = data[i];
            }
        }

        int sum = calcCheckSum(sendbuf, 7 + size);
        sendbuf[7 + size] = (byte) sum;
        sendbuf[8 + size] = (byte) (sum >> 8);
        // Set the device command
        mDeviceCmd = cmdid;

        // Write the data to the serial port
        SerialPortUtil.getPort().writeBytes(sendbuf, sendbuf.length);
    }

    private int calcCheckSum(byte[] buffer, int length) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += (buffer[i] & 0xFF); // Ensure unsigned addition
        }
        return sum;
    }


     public String getAuthToken(String emailAddy, String password) {
        System.out.println("Getting Auth Token...");
        String apiUrl = getBaseUrl() + "api/v1/authenticate";
        String response = "";
        try {
            // Create the URL object
            URL url = new URL(apiUrl);
            // Open the connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Set the request method to POST
            connection.setRequestMethod("POST");
            // Set the request headers
            connection.setRequestProperty("accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("X-CSRF-TOKEN", "");
            // Enable output for the connection
            connection.setDoOutput(true);
            // Create the JSON payload
            String jsonInputString = String.format(
                "{\"email\": \"%s\", \"password\": \"%s\"}",
                emailAddy, password
            );

            // Write the JSON payload to the output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read the response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                response = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                response = extractToken(response);
                 
            } else {
                response = "Error: " + responseCode + " - " + connection.getResponseMessage();
                System.out.println("getAuthToken(failed): " + response);
                response = null; // Set to null if the request failed
                
            }

        } catch (Exception e) {
            //e.printStackTrace();
            response = "Exception: " + e.getMessage();
            System.out.println("getAuthToken(failed): " + response);
            response = null; // Set to null if an exception occurred
        }       
        return response;
    } 

 public String extractToken(String jsonResponse) {
        try {
            // Parse the JSON response
            JSONObject responseObject = new JSONObject(jsonResponse);
            // Check if the "status" is true
            if (responseObject.getBoolean("status")) {
                // Navigate to the "data" object and extract the "token"
                JSONObject dataObject = responseObject.getJSONObject("data");
                String token = dataObject.getString("token");
                return token;
            } else {
                System.out.println("Status is false. Message: " + responseObject.getString("message"));
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error extracting token: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String validateCardVia3wc(String token,  String sn) {
        String apiUrl = getBaseUrl() + "api/v1/validate-card?serial_number=" + sn;  
        String responseMessage = ""; 
        try {
            // Create the URL object
            URL url = new URL(apiUrl);
            // Open the connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");
            // Set the request headers
            connection.setRequestProperty("accept", "*/*");            
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("X-CSRF-TOKEN", "");

            // Read the response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED ) {
                responseMessage = "Unauthorized";
                System.out.println("validateCardVia3wc(failed): " + responseMessage);
            }
            else if (responseCode == HttpURLConnection.HTTP_OK) {
                String jsonResponse = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                // Parse the JSON response
                JSONObject responseObject = new JSONObject(jsonResponse);
                // Check if the "status" is true
                if (responseObject.getBoolean("status")) {
                    // Extract the "message" and "is_valid" fields
                    String message = responseObject.getString("message");
                    boolean isActive = responseObject.getJSONObject("data")
                                                   .getJSONObject("card")
                                                   .getBoolean("is_active");
                    boolean isUsed = responseObject.getJSONObject("data")
                                                   .getJSONObject("card")
                                                   .getBoolean("is_used");

                    responseMessage = String.format("Card (%s, %s, IsActive(%b), IsUsed(%b))", sn, message, isActive, isUsed);
                } else {
                    responseMessage = "Error: " + responseObject.getString("message");
                }
            } else {
                responseMessage = "HTTP Error: " + responseCode + " - " + connection.getResponseMessage();
            }

        } catch (Exception e) {
            responseMessage = "Exception: " + e.getMessage();
            e.printStackTrace();
        }

        return responseMessage;
    }
    public String registerCardVia3wc(String token, String apikey, String sn) {
        String apiUrl = getBaseUrl() + "api/v1/register-card?serial_number=" + sn;  

        String responseMessage = "";
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("X-API-KEY", apikey);
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("X-CSRF-TOKEN", "");
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = "".getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read the response
            int responseCode = connection.getResponseCode();
            if (responseCode  == HttpURLConnection.HTTP_UNAUTHORIZED ) {
                responseMessage = "Unauthorized";
            }
            else if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                String jsonResponse = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                // Parse the JSON response
                JSONObject responseObject = new JSONObject(jsonResponse);
                // Check if the "status" is true
                if (responseObject.getBoolean("status")) {
                    // Extract the "message" and "is_used" fields
                    String message = responseObject.getString("message");
                    boolean isUsed = responseObject.getJSONObject("data")
                                                   .getJSONObject("card")
                                                   .getBoolean("is_used");
                    responseMessage = String.format("Card(%s, %s, IsUsed(%b))", sn, message, isUsed);
                } else {
                    responseMessage = "Error: " + responseObject.getString("message");
                }
            } else {
                responseMessage = "HTTP Error: " + responseCode + " - " + connection.getResponseMessage();
            }

        } catch (Exception e) {
            responseMessage = "Exception: " + e.getMessage();
            e.printStackTrace();
        }

        return responseMessage;
    }


    // Command Constants
    private static final byte CMD_PASSWORD = 0x01;     // Password
    private static final byte CMD_ENROLID = 0x02;      // Enroll in Device
    private static final byte CMD_VERIFY = 0x03;       // Verify in Device
    private static final byte CMD_IDENTIFY = 0x04;     // Identify in Device (Search)
    private static final byte CMD_DELETEID = 0x05;     // Delete in Device
    private static final byte CMD_CLEARID = 0x06;      // Clear in Device

    private static final byte CMD_ENROLHOST = 0x07;    // Enroll to Host

    private static final byte CMD_CAPTUREHOST = 0x08;  // Capture to Host
    private static final byte CMD_MATCH = 0x09;        // Match

    private static final byte CMD_WRITEFPCARD = 0x0A;  // Write Fingerprint Card
    private static final byte CMD_READFPCARD = 0x0B;   // Read Fingerprint Card
    private static final byte CMD_FPCARDMATCH = 0x13;  // Fingerprint Card Match

    private static final byte CMD_UPCARDSN = 0x43;
    private static final byte CMD_CARDSN = 0x0E;       // Read Card Sn

    private static final byte CMD_WRITEDATACARD = 0x14; // Write Card Data
    private static final byte CMD_READDATACARD = 0x15;  // Read Card Data
    private static final byte CMD_PRINTCMD = 0x20;
    private static final byte CMD_GETSN = 0x10;
    private static final byte CMD_GETBAT = 0x21;
    private static final byte CMD_GETIMAGE = 0x30;
    private static final byte CMD_GETCHAR = 0x31;

    // Variables
    private boolean mIsWork = false;
    private byte mDeviceCmd = 0x00;
    private byte[] mCmdData = new byte[10240];
    private int mCmdSize = 0;
    private byte[] mUpImage = new byte[73728];
    private int mUpImageSize = 0;
    private byte[] mRefData = new byte[512];
    private byte[] mMatData = new byte[512];

}
