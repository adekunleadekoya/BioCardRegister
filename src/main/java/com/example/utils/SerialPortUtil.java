package com.example.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class SerialPortUtil {

    // Static member to hold the current port
    private static SerialPort port;

    // Callback for received data
    private static Consumer<byte[]> dataReceivedCallback;

    public static List<String> listAvailablePorts() {
        List<String> portNames = new ArrayList<>();
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            portNames.add(port.getSystemPortName());
        }
        return portNames;
    }

    public static SerialPort getPort() {
        return port;
    }

    public static void configurePort(int baudRate, int dataBits, int parity, int stopBits, int readTimeout, int writeTimeout) {
        if (port != null && port.isOpen()) {
            port.setBaudRate(baudRate);
            port.setNumDataBits(dataBits);
            port.setParity(parity);
            port.setNumStopBits(stopBits);
            port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, readTimeout, writeTimeout);
            System.out.println("Serial port configured: BaudRate=" + baudRate + ", DataBits=" + dataBits +
                               ", Parity=" + parity + ", StopBits=" + stopBits +
                               ", ReadTimeout=" + readTimeout + ", WriteTimeout=" + writeTimeout);
        } else {
            System.out.println("Port is not open. Cannot configure.");
        }
    }

    public static void connect(String portName) {
        // Check if a port is already connected
        if (port != null && port.isOpen()) {
            System.out.println("Port " + port.getSystemPortName() + " is already connected.");
            return; // Exit the method to avoid reconnecting
        }
        port = SerialPort.getCommPort(portName);
        if (port.openPort()) {
            System.out.println("Connected to " + portName);

            // Add a data listener to the port
            port.addDataListener(new SerialPortDataListener() {
                @Override
                public int getListeningEvents() {
                    return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
                }

                @Override
                public void serialEvent(SerialPortEvent event) {
                    if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                        byte[] buffer = new byte[port.bytesAvailable()];
                        int bytesRead = port.readBytes(buffer, buffer.length);

                        // Trigger the callback if set
                        if (dataReceivedCallback != null) {
                            dataReceivedCallback.accept(buffer);
                        }

                       // System.out.println("Received " + bytesRead + " bytes: " + new String(buffer));
                    }
                }
            });
        } else {
            System.out.println("Failed to connect to " + portName);
        }
    }

    public static void disconnect() {
        if (port != null && port.isOpen()) {
            port.closePort();
            System.out.println("Disconnected from " + port.getSystemPortName());
            port = null; // Reset the static port variable
        } else {
            System.out.println("No port is currently connected.");
        }
    }

    // Method to set the data received callback
    public static void setDataReceivedCallback(Consumer<byte[]> callback) {
        dataReceivedCallback = callback;
    }
}