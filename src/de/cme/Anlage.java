package de.cme;

import gnu.io.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;

public class Anlage {

    // Anfang Attribute
    private Steuerung dieSteuerung;

    private SerialPort serialPort;
    private CommPortIdentifier serialPortId;
    private Enumeration enumComm;

    private OutputStream outputStream;
    private InputStream inputStream;
    
    private List<String> comPorts;
    

    private final int BAUDRATE = 500_000;
    private final int DATA_BITS = SerialPort.DATABITS_8;
    private final int STOP_BITS = SerialPort.STOPBITS_1;
    private final int PARITY = SerialPort.PARITY_NONE;
    private boolean serialPortGeoeffnet = false;
    private byte adresse;
    private int geschwindigkeit;
    // Ende Attribute

    // Anfang Methoden
    public Anlage(Steuerung eineSteuerung) {
        dieSteuerung = eineSteuerung;
        comPorts = new ArrayList<>();
    }
    
    public void schreibeAufCAN(byte[] dieDaten) {
        if (serialPortGeoeffnet != true) {
            System.out.println("Port nicht geöffnet!");
            return;
        }
        try {
            outputStream.write(dieDaten);
        } catch (IOException ex) {
            System.out.println("Fehler beim Senden");
        }
    }

    public byte[] holeVonCAN() {
        byte[] daten = new byte[13]; //immer Datenpaket von 13 Bytes
        try {
            int num;
            while (inputStream.available() > 0) {
                num = inputStream.read(daten, 0, daten.length);
                System.out.println("Empfange: " + new String(daten, 0, num));

                //Empfangene Daten in Konsole ausgeben
                System.out.println("Daten in einzelnen Bytes: ");
                for (byte datenByte : daten) {
                    System.out.print(datenByte);
                    System.out.print(" ");
                }
                System.out.println();

                //DEBUG: Daten in Datei schreiben
                schreibenDatenInDatei(daten);
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen empfangener Daten");
        }
        return daten;
    }

    private void schreibenDatenInDatei(byte[] daten) {
        PrintWriter pWriter = null;
        try {
            pWriter = new PrintWriter(new BufferedWriter(new FileWriter("Daten.txt", true)), true);
            //Alle Bytes einzeln mit Leerzeichen getrennt in Datei schreiben
            for (byte datenByte : daten) {
                pWriter.print(datenByte);
                pWriter.print(" ");
            }
            pWriter.println();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (pWriter != null) {
                pWriter.flush();
                pWriter.close();
            }
        }
    }

    public List<String> aktualisiereSerialPort() {
        System.out.println("Aktualisiere Serialport-Liste");
        if (serialPortGeoeffnet != false) {
            System.out.println("Serialport ist geöffnet");
            return comPorts;
        }
        dieSteuerung.clearPortList();
        enumComm = CommPortIdentifier.getPortIdentifiers();
        while (enumComm.hasMoreElements()) {
            serialPortId = (CommPortIdentifier) enumComm.nextElement();
            if (serialPortId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                comPorts.add(serialPortId.getName());
            }
        }
        Collections.reverse(comPorts);
        return comPorts;
    }

    public boolean isConntected() {
        return serialPortGeoeffnet;
    }

    public boolean connect(String portName) {
        Boolean foundPort = false;

        if (serialPortGeoeffnet != false) {
            System.out.println("Serialport bereits geöffnet");
            return false;
        }
        System.out.println("Öffne Serialport");
        enumComm = CommPortIdentifier.getPortIdentifiers();
        while (enumComm.hasMoreElements()) {
            serialPortId = (CommPortIdentifier) enumComm.nextElement();
            if (portName.contentEquals(serialPortId.getName())) {
                foundPort = true;
                break;
            }
        }
        if (foundPort != true) {
            System.out.println("Serialport nicht gefunden: " + portName);
            return false;
        }
        try {
            serialPort = (SerialPort) serialPortId.open("Öffnen und Senden", BAUDRATE);
        } catch (PortInUseException e) {
            System.out.println("Port belegt");
        }
        try {
            outputStream = serialPort.getOutputStream();
        } catch (IOException e) {
            System.out.println("Keinen Zugriff auf OutputStream");
        }
        try {
            inputStream = serialPort.getInputStream();
        } catch (IOException e) {
            System.out.println("Keinen Zugriff auf InputStream");
        }
        try {
            serialPort.addEventListener(new serialPortEventListener());
        } catch (TooManyListenersException e) {
            System.out.println("TooManyListenersException für Serialport");
        }
        serialPort.notifyOnDataAvailable(true);
        try {
            serialPort.setSerialPortParams(BAUDRATE, DATA_BITS, STOP_BITS, PARITY);
        } catch (UnsupportedCommOperationException e) {
            System.out.println("Konnte Schnittstellen-Paramter nicht setzen");
        }

        serialPortGeoeffnet = true;
        return true;
    }

    public void disconnect() {
        if (serialPortGeoeffnet == true) {
            System.out.println("Schließe Serialport");
            serialPort.close();
            serialPortGeoeffnet = false;
        } else {
            System.out.println("Serialport bereits geschlossen");
        }
    }

    // Ende Methoden
    class serialPortEventListener implements SerialPortEventListener {

        @Override
        public void serialEvent(SerialPortEvent event) {
            System.out.println("serialPortEventlistener");
            switch (event.getEventType()) {
                case SerialPortEvent.DATA_AVAILABLE:
                    System.out.println("Daten auf CAN verfügbar!");
                    //Lese die Daten vom CAN Bus ein und übergib sie der Steuerung
                    dieSteuerung.holeDaten(holeVonCAN());
                    dieSteuerung.findeWeichenPosition(); //bald deprecaded, weil nur noch über Automatik-Wegfindung Weichen gesucht werden
                    dieSteuerung.sucheRMK(); //bald deprecaded, weil noch nur über Automatik-Wegfindung gesucht wird
                    dieSteuerung.RMKfuerFahren();
                    break;
                case SerialPortEvent.BI:
                case SerialPortEvent.CD:
                case SerialPortEvent.CTS:
                case SerialPortEvent.DSR:
                case SerialPortEvent.FE:
                case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                case SerialPortEvent.PE:
                case SerialPortEvent.RI:
                default:

            }
        }
    }
}
