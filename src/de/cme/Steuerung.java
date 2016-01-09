package de.cme;

import de.cme.dijkstra.Dijkstra;
import de.cme.dijkstra.Knoten;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

/**
 *
 * @author Pascal
 */
public class Steuerung implements Befehle {

    // Anfang Attribute
    private GUI dieGUI;
    private Anlage dieAnlage;
    private Dijkstra dijkstra;
    private Timer sendTimer;

    private byte[] dieDaten;
    private byte[] empfangeneDaten;

    private int gewWeichenModul;
    private int gewRMKModul;
    private byte lokAdresse;

    //Standardmäßig nicht Geschwindigkeit senden
    private boolean sendEnabled = false;

    private List<Knoten> weg;
    private int startPoint;
    private int endPoint;

    //Konstanten
    //Standard-Hash
    private final byte HIGH_BYTE_HASH = (byte) 175;
    private final byte LOW_BYTE_HASH = (byte) 83;
    protected final byte STANDARD_LOK_ADRESSE = 24;
    private static final int TIMER_DELAY = 100;
    // Ende Attribute

    public Steuerung(GUI eineGUI) {
        dieGUI = eineGUI; // bidirektional
        dieAnlage = new Anlage(this); // bidirektional
        dijkstra = new Dijkstra(); //Objekt zur Wegfindung erstellen

        //Timer initialisieren
        initTimer();

        dieDaten = new byte[13]; //Byte Array für Daten mit 13 Bytes
        dieDaten[0] = (byte) 0;
        dieDaten[1] = (byte) 0;
        dieDaten[2] = (byte) 175;
        dieDaten[3] = (byte) 83;
        dieDaten[4] = (byte) 8;
        dieDaten[5] = (byte) 0;
        dieDaten[6] = (byte) 0;
        dieDaten[7] = (byte) 0;
        dieDaten[8] = (byte) 0;
        dieDaten[9] = (byte) 0;
        dieDaten[10] = (byte) 0;
        dieDaten[11] = (byte) 0;
        dieDaten[12] = (byte) 0;

        empfangeneDaten = new byte[13]; //Byte Array für empfangene Daten mit 13 Bytes

        weg = new ArrayList<>(); //Liste des Weges, der die Knoten enthält

        lokAdresse = STANDARD_LOK_ADRESSE;

    }

    // Anfang Methoden
    public void setGewWeichenModul(int gewWeichenModul) {
        this.gewWeichenModul = gewWeichenModul;
    }

    public void setGewRMKModul(int gewRMKModul) {
        this.gewRMKModul = gewRMKModul;
    }

    public byte getLokAdresse() {
        return lokAdresse;
    }

    public void setLokAdresse(int lokAdresse) {
        setLokAdresse((byte) lokAdresse);
    }

    public void setLokAdresse(byte lokAdresse) {
        this.lokAdresse = lokAdresse;
    }

    public void setLokAdresse(String lokAdresse) {
        this.lokAdresse = Byte.parseByte(lokAdresse);
    }

    public void schliessen() {
        dieAnlage.disconnect();
        System.out.println("Beenden");
        System.exit(0);
    }
    
    private void initTimer() {
        sendTimer = new Timer(TIMER_DELAY, (ActionEvent e) -> {
//            System.out.println("Timer abgelaufen nach 100ms");
            sendEnabled = true;
        });
        sendTimer.start();
    }

    //Überladene Methode: mit Adresse oder Adresse von Steuerung benutzen
    public void fahreLok(int geschwindigkeit) {
        fahreLok(lokAdresse, geschwindigkeit);
    }

    public void fahreLok(byte adresse, int geschwindigkeit) {
        // Systemfahrstufe = 1 + (Gleisfahrstufe - 1) * Schrittweite
        System.out.println("Geschw. senden: " + (geschwindigkeit * 10));
        //int systemGeschwindigkeit = 1 + (geschwindigkeit - 1) * 33;
        int geschwLowByte = geschwindigkeit * 10;
        int geschwHighByte = geschwLowByte >> 8;
        System.out.println("Adresse: " + adresse);
//        System.out.println("Systemgeschw.: " + systemGeschwindigkeit);
        System.out.println("Low Byte: " + (byte) geschwLowByte);
        System.out.println("High Byte: " + (byte) geschwHighByte);
        try {
            dieDaten[1] = 8;                     // CAN-ID: 0x08
            setzeHash(HIGH_BYTE_HASH, LOW_BYTE_HASH);  // Standard-Hash setzen
            dieDaten[4] = 6;                     // DLC: 6 Datenbytes senden
            dieDaten[5] = 0;                     // 1. Byte der Loc-ID: 0	Local-ID (4 Bytes): 0 0 0 24
            dieDaten[6] = 0;                     // 2. Byte der Loc-ID: 0
            dieDaten[7] = 0;                     // 3. Byte der Loc-ID: 0 bei MM2
            dieDaten[8] = adresse;       // 4. Byte der Loc-ID: 24 (eingegebene Adresse der Lok)
            dieDaten[9] = (byte) geschwHighByte; // High-Byte: 1. Byte der Geschw.: geschwLowByte 8 Bit nach rechts verschoben
            dieDaten[10] = (byte) geschwLowByte; // Low-Byte: 2. Byte der Geschw.: 8-Bit des Werts des Schiebereglers * 10
            dieDaten[11] = (byte) 0;             // Rest mit 0 auffüllen
            dieDaten[12] = (byte) 0;             // Rest mit 0 auffüllen
            //Nur senden, wenn 100ms gewartet wurde (Timer abgelaufen ist)
            // damit kein Datenstau entsteht
            if (sendEnabled) {
                dieAnlage.schreibeAufCAN(dieDaten);  // Senden
                sendEnabled = false;
            }
        } catch (Exception ex) {
            System.out.println("Fehler beim Lok fahren/Lok anhalten.");
            anhaltenSystem();
        }
    }

    public void connect(String portName) {
        dieAnlage.connect(portName);
    }

    public List<String> aktualisiereSerialPort() {
        return dieAnlage.aktualisiereSerialPort();
    }

    public void disconnect() {
        dieAnlage.disconnect();
    }

    public void einschaltenSystem() {
        System.out.println("System GO");
        setzeHash(HIGH_BYTE_HASH, LOW_BYTE_HASH); //Standard-Hash setzen
        systemBefehl();
        //Sub-ID:1
        dieDaten[9] = 1;
        dieAnlage.schreibeAufCAN(dieDaten);
    }

    public void anhaltenSystem() {
        System.out.println("System HALT");
        setzeHash(HIGH_BYTE_HASH, LOW_BYTE_HASH); //Standard-Hash setzen
        systemBefehl();
        //Sub-ID:2
        dieDaten[9] = 2;
        dieAnlage.schreibeAufCAN(dieDaten);
    }

    public void stoppSystem() {
        System.out.println("System STOPP");
        setzeHash(HIGH_BYTE_HASH, LOW_BYTE_HASH); //Standard-Hash setzen
        systemBefehl();
        //Sub-ID:0
        dieDaten[9] = 0;
        dieAnlage.schreibeAufCAN(dieDaten);
    }

    public void systemBefehl() {
        dieDaten[1] = (byte) 0; //CAN-ID:0
        dieDaten[4] = (byte) 5; //DLC:5 Datenbytes
        dieDaten[5] = (byte) 0;
        dieDaten[6] = (byte) 0;
        dieDaten[7] = (byte) 0;
        dieDaten[8] = (byte) 0;
    }

    private void setzeHash(int nr2, int nr3) {
        /**
         * byte nr2: 3.Hash-Byte in Datenpaket (in Array Index 2) byte nr3:
         * 4.Hash-Byte in Datenpaket (in Array Index 3)
         */
        dieDaten[2] = (byte) nr2;
        dieDaten[3] = (byte) nr3;
    }

    //Wird nicht benötigt
    public void wechsleRichtung(byte adresse) {
        System.out.println("Richtungswechsel");
        dieDaten[1] = (byte) 10; //CAN-ID:10 (0x0A)
        setzeHash(HIGH_BYTE_HASH, LOW_BYTE_HASH); //Standard-Hash setzen
        dieDaten[4] = (byte) 5; //DLC:5
        dieDaten[5] = (byte) 0;
        dieDaten[6] = (byte) 0;
        dieDaten[8] = (byte) adresse;
        dieDaten[9] = (byte) 3; //Richtung umschalten
        dieAnlage.schreibeAufCAN(dieDaten);
    }

    public void lokFunktion(byte adresse, byte funktion, byte wert) {
        System.out.println("Lok Funktion " + funktion);
        dieDaten[1] = 12; //CAN-ID: 12 0x0C
        setzeHash(HIGH_BYTE_HASH, LOW_BYTE_HASH); //Standard-Hash setzen
        dieDaten[4] = 6;    //DLC:6
        dieDaten[5] = 0;
        dieDaten[6] = 0;
        dieDaten[7] = 0;
        dieDaten[8] = adresse;
        dieDaten[9] = funktion;
        dieDaten[10] = wert;
        dieAnlage.schreibeAufCAN(dieDaten);
    }

    @Override
    public void schalteLichtVonLok(int lokName, boolean lichtEin) {
        System.out.println("Licht-Funktion");
        if (lichtEin) {
            lokFunktion((byte) lokName, (byte) 0, (byte) 1); //Licht der Lok einschalten
        } else {
            lokFunktion((byte) lokName, (byte) 0, (byte) 0); //Licht der Lok ausschalten
        }
    }

    public void schalteLichtVonLok(boolean lichtEin) {
        System.out.println("Licht-Funktion");
        if (lichtEin) {
            lokFunktion(lokAdresse, (byte) 0, (byte) 1); //Licht der Lok einschalten
        } else {
            lokFunktion(lokAdresse, (byte) 0, (byte) 0); //Licht der Lok ausschalten
        }
    }

    public void stelleWeiche(int modulNr, byte weichenAdresse, byte stellung) {

        //DEBUG empfangeneDaten Array ausgeben
//        for (int i = 0; i < empfangeneDaten.length; i++) {
//            System.out.println("DEBUG: empfangene Daten Byte Nr." + (i + 1) + ":  " + empfangeneDaten[i]);
//        }
        // ende empfangeneDaten Array ausgeben
        int mAdresse = empfangeneDaten[8] << 8;
        mAdresse += empfangeneDaten[7];

        int mTestAdresse = 0x3000 + weichenAdresse - 1; //Beginn der ersten Adresse des Weichenmoduls
        switch (modulNr) {
            case 4:
                mTestAdresse = 0x3000; //1. Adresse Modul 4: 1 (0)
                break;
            case 5:
                //Nummerierung beginnt bei 0, deshalb '-1'
                mTestAdresse = 0x3000 + 13 - 1; //1. Adresse Modul 5: 13 (12)
                break;
            case 6:
                mTestAdresse = 0x3000 + 22 - 1; //1. Adresse Modul 6: 22 (21)
                break;
        }
        mTestAdresse += weichenAdresse; //WeichenAdresse dazurechnen
        System.out.println("Weiche stellen");
        dieDaten[0] = 0x0;
        dieDaten[1] = 0x16; //CAN-ID: 0x16 = 22d (Zubehör Schalten)
        dieDaten[2] = 0x03;
        dieDaten[3] = 0x00;
        dieDaten[4] = 6;    //DLC:6
        dieDaten[5] = (byte) mTestAdresse; //0x3000; //MM1,2 Zubehörartikeldecoder (40 kHz, 320 & 1024 Adressen)
        dieDaten[6] = 0;
        dieDaten[7] = (byte) (mTestAdresse >> 8);
        dieDaten[8] = (byte) mTestAdresse;
        // 00: Rechts (01: Gerade) 10: Links (11: Weis) (Doku S.37)
        dieDaten[9] = stellung;
        dieDaten[10] = (byte) 1; //Strom ein
        dieAnlage.schreibeAufCAN(dieDaten);
        //Halbe Sekunde warten
        try {
            Thread.sleep(500); //500ms warten
        } catch (InterruptedException ex) {
            Logger.getLogger(Steuerung.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Nocheinmal senden ohne Strom
        dieDaten[9] = stellung;
        dieDaten[10] = (byte) 0; //Strom aus
        dieAnlage.schreibeAufCAN(dieDaten);

        //weiterer Befehl einstellen
//        dieDaten[0] = (byte) 0;
//        dieDaten[1] = (byte) 70;
//        dieDaten[2] = (byte) 42;
//        //TO-DO: Adresse ändern
//        dieDaten[3] = (byte) mTestAdresse; //0x3005; //adresse + 128
//        dieDaten[4] = (byte) 4;
    }

    public void holeDaten(byte[] datenVonCAN) {
        //Daten vom CAN speichern
        empfangeneDaten = datenVonCAN;
//        System.out.println();
//        System.out.println("empfangene Daten: ");
//        for (byte datenByte : empfangeneDaten) //DEBUG
//        {
//            System.out.print(empfangeneDaten + " ");
//        }
//        System.out.println();
    }

    private int leseWeichenAdresse(int nummer) {
        /**
         * int number: Zahl von 1-4 gibt die Moduladresse zurück?
         */
        int modulAdresse = empfangeneDaten[3] - 128;
        int mAdresse = 0;
        if (empfangeneDaten[0] == 0 && empfangeneDaten[1] == 69
                && (empfangeneDaten[2] == 42 && empfangeneDaten[3] == modulAdresse)
                && (empfangeneDaten[6] == 6 && empfangeneDaten[5] == nummer)) {
            mAdresse = empfangeneDaten[8] << 8;
            mAdresse += empfangeneDaten[7];
        }
        return mAdresse;
    }

    public void findeWeichenPosition() {
        //Information zum Auslesen der Stellung der Weichen
        //http://www.can-digital-bahn.com/modul.php?system=sys5&modul=62#Anschluss
        int wAdresse, modulAdresse = empfangeneDaten[3];
        /* siehe Z.895 und Z.1339 in WeichenChef_magnet_002.cs
         ** Modul-Nr.: empfangeneDaten[3] - 128
         ** Weichen-Chef-Adresse: 4, 5, 6
         */

        int position = 2; //Sandardwert 2:Fehler

        //Weichenmodul-Adresse
        wAdresse = empfangeneDaten[7] << 8;
        wAdresse += empfangeneDaten[8];

        //Überprüfe erste angeschlossene Weiche
        //0 23 19 5 6 0 0 48 14 1 1 0 0
        // Durch die vier Ausgänge der WeichenChefs durchiterieren (1-4)
        for (int weichenModul = 0; weichenModul < 25; weichenModul += 12) {
            if (weichenModul == 24) { //die erste Weiche des Weichenmoduls mit Adresse 6 beginnt bei 21
                weichenModul = 21;
            }
            for (int weichenAnschluss = 0; weichenAnschluss < 4; weichenAnschluss++) {
                if (empfangeneDaten[0] == 0 && empfangeneDaten[1] == 23
                        && (empfangeneDaten[7] == 0x30 && empfangeneDaten[8] == (weichenModul + weichenAnschluss) /* (hier vom 1./2./3. Modul mit Adresse 4/5/6 | 1. Adresse: 0x3000/12288d)*/)) {
//                if (empfangeneDaten[9] == 253) { //richtig
                    if (empfangeneDaten[9] == 1) { //Test
                        //position = 1; //Grün/gerade/1 /nicht benötigt
                        dieGUI.positionGerade(weichenAnschluss + 1);
                    }
//                if (empfangeneDaten[9] == 254) { //richtig
                    if (empfangeneDaten[9] == 0) { //Test
                        // position = 0; //Rot/rund/0 /nicht benötigt
                        dieGUI.positionRund(weichenAnschluss + 1);
                    }
                    if (empfangeneDaten[9] == 255) {
                        //position = 2; //Gelb Fehler
                        dieGUI.positionFehler(weichenAnschluss + 1);
                    }
                }
            }
        }
//        for (int i = 0; i < 4; i++) {
//            if (empfangeneDaten[0] == 0 && empfangeneDaten[1] == 23
//                    && (empfangeneDaten[7] == 0x30 && empfangeneDaten[8] == (12 + i) /* (hier vom 2. Modul mit Adresse 5 | 1. Adresse: 0x3000/12288d)*/)) {
////                if (empfangeneDaten[9] == 253) { //richtig
//                if (empfangeneDaten[9] == 1) { //Test
//                    //position = 1; //Grün/rechts/1 /nicht benötigt
//                    dieGUI.positionRund(i + 1);
//                }
////                if (empfangeneDaten[9] == 254) { //richtig
//                if (empfangeneDaten[9] == 0) { //Test
//                    // position = 0; //Rot/links/0 /nicht benötigt
//                    dieGUI.positionGerade(i + 1);
//                }
//                if (empfangeneDaten[9] == 255) {
//                    //position = 2; //Gelb Fehler
//                    dieGUI.positionFehler(i + 1);
//                }
//            }
//        }
//        for (int i = 0; i < 4; i++) {
//            if (empfangeneDaten[0] == 0 && empfangeneDaten[1] == 23
//                    && (empfangeneDaten[7] == 0x30 && empfangeneDaten[8] == (21 + i) /* (hier vom 3. Modul mit Adresse 6 | 1. Adresse: 0x3000/12288d)*/)) {
////                if (empfangeneDaten[9] == 253) { //richtig
//                if (empfangeneDaten[9] == 1) { //Test
//                    //position = 1; //Grün/rechts/1 /nicht benötigt
//                    dieGUI.positionRund(i + 1);
//                }
////                if (empfangeneDaten[9] == 254) { //richtig
//                if (empfangeneDaten[9] == 0) { //Test
//                    // position = 0; //Rot/links/0 /nicht benötigt
//                    dieGUI.positionGerade(i + 1);
//                }
//                if (empfangeneDaten[9] == 255) {
//                    //position = 2; //Gelb Fehler
//                    dieGUI.positionFehler(i + 1);
//                }
//            }
//        }
    }

    public void sendeRMK() {
        System.out.println("RMK-Anfrage:");
//        dieDaten[0] = 2; //Rückmeldung Prio:2
//        dieDaten[1] = 0x20; //CAN-ID: 0x20/32d Polling
//        dieDaten[2] = (byte) 3;
//        dieDaten[3] = (byte) 0; //davor 129
//        dieDaten[4] = 5; //DLC: 5
//        dieDaten[5] = 0; //Gerätekenner
//        dieDaten[6] = 0; //Gerätekenner
//        dieDaten[7] = 0; //Kontaktkennung
//        dieDaten[8] = 1; //Kontaktkennung //Adresse 1
//        dieDaten[9] = 0;
//        dieDaten[10] = 0;
//        dieDaten[11] = 0;
//        dieDaten[12] = 0;
//        dieAnlage.schreibeAufCAN(dieDaten);

        dieDaten[0] = 2; //Rückmeldung Prio:2
        dieDaten[1] = 0x22; //CAN-ID: 0x22/34d Rückmelde Event
        dieDaten[2] = (byte) 11; //Hash
        dieDaten[3] = (byte) gewRMKModul; // RMK-Modul-Nummer
        dieDaten[4] = 4; //DLC: 4
        dieDaten[5] = 0; //Gerätekenner
        dieDaten[6] = 0; //Gerätekenner
        dieDaten[7] = 0; //Kontaktkennung

        dieDaten[9] = 0;
        dieDaten[10] = 0;
        dieDaten[11] = 0;
        dieDaten[12] = 0;
        int anfangsAdresse = (gewRMKModul * 8 - 7); //Anfangsadresse des gewählten RMK-Moduls
        //Alle Ausgänge des Moduls abfragen
        for (int i = anfangsAdresse; i < (anfangsAdresse + 8); i++) {
            try {
                Thread.sleep(50); //50ms warten
                dieDaten[8] = (byte) i; //Kontaktkennung //Adresse 1
                dieAnlage.schreibeAufCAN(dieDaten);
            } catch (InterruptedException ex) {
                Logger.getLogger(Steuerung.class.getName()).log(Level.SEVERE, null, ex);
            }
            /*
             try {
             int adresse = gewRMKModul;
             //Adresse der einzelnen Eingänge des aktuellen GleisReporters
             int rmk = adresse * 8 - 8;
             //Eingangsnummern setzen auf GUI
             for (int i = 1; i < 9; i++) {
             dieGUI.setzeRMKEingangNr(i, rmk + i);
             }
             adresse += 128;
             dieDaten[3] = (byte) adresse;
             dieDaten[1] = (byte) 4;
             dieDaten[4] = (byte) 2;
             dieDaten[5] = (byte) 0;
             dieDaten[6] = (byte) 1;
             dieAnlage.schreibeAufCAN(dieDaten);
             Thread.sleep(2);
             dieDaten[1] = (byte) 4;
             dieDaten[4] = (byte) 2;
             dieDaten[5] = (byte) 0;
             dieDaten[6] = (byte) 16;
             dieAnlage.schreibeAufCAN(dieDaten);
             Thread.sleep(2);
             dieDaten[1] = (byte) 4;
             dieDaten[4] = (byte) 2;
             dieDaten[5] = (byte) 0;
             dieDaten[6] = (byte) 17;
             dieAnlage.schreibeAufCAN(dieDaten);
             Thread.sleep(2);
             dieDaten[1] = (byte) 4;
             dieDaten[4] = (byte) 2;
             dieDaten[5] = (byte) 0;
             dieDaten[6] = (byte) 18;
             dieAnlage.schreibeAufCAN(dieDaten);
             for (byte i = (byte) 1; (int) i < 9; ++i) {
             Thread.sleep(2);
             dieDaten[5] = i;
             dieDaten[6] = (byte) 2;
             dieAnlage.schreibeAufCAN(dieDaten);
             }
             for (byte i = (byte) 1; (int) i < 9; ++i) {
             Thread.sleep(2);
             dieDaten[5] = i;
             dieDaten[6] = (byte) 3;
             dieAnlage.schreibeAufCAN(dieDaten);
             }
             Thread.sleep(2);
             dieDaten[5] = (byte) 0;
             dieDaten[6] = (byte) 238;
             dieAnlage.schreibeAufCAN(dieDaten);
             Thread.sleep(2);
             dieDaten[1] = (byte) 32;
             dieDaten[2] = (byte) 3;
             dieDaten[3] = (byte) 0;
             dieDaten[4] = (byte) 5;
             dieDaten[5] = (byte) 1;
             dieDaten[6] = (byte) 2;
             dieDaten[7] = (byte) 3;
             dieDaten[8] = (byte) 4;
             dieAnlage.schreibeAufCAN(dieDaten);
             dieDaten[0] = (byte) 0;
             dieDaten[1] = (byte) 0;
             dieDaten[2] = (byte) 218;
             dieDaten[3] = (byte) adresse;
             dieDaten[4] = (byte) 0;
             } catch (InterruptedException ex) {
             Logger.getLogger(Steuerung.class.getName()).log(Level.SEVERE, null, ex);
             }
             */
        }

    }

    public void sucheRMK() {
        System.out.println("suche RMK-Methode");
        System.out.println("empfangene Daten in suche RMK-Methode");
        for (byte dataByte : empfangeneDaten) {

            System.out.print(dataByte + " ");
        }
        System.out.println();
        if (empfangeneDaten[0] == 0 && empfangeneDaten[1] == 35 && empfangeneDaten[2] == 11 /* empfangeneDaten[3] == 1 Modul-Nr.=1*/ && empfangeneDaten[4] == 8) {
            int help = empfangeneDaten[7] << 8;
            help += (empfangeneDaten[8] - (gewRMKModul * 8 - 7));
            System.out.println("RMK-Modul: " + gewRMKModul);
            System.out.println("switch-case Zahl: " + help);
            switch (help) {
                case 0:
                    if (empfangeneDaten[9] == 0) {
                        //belegt
                        dieGUI.setzeRMKStatus(0, false);
                        break;
                    }
                    //frei
                    dieGUI.setzeRMKStatus(0, true);
                    break;
                case 1:
                    if (empfangeneDaten[9] == 0) {
                        //belegt
                        dieGUI.setzeRMKStatus(1, false);
                        break;
                    }
                    //frei
                    dieGUI.setzeRMKStatus(1, true);
                    break;
                case 2:
                    if (empfangeneDaten[9] == 0) {
                        //belegt
                        dieGUI.setzeRMKStatus(2, false);
                        break;
                    }
                    //frei
                    dieGUI.setzeRMKStatus(2, true);
                    break;
                case 3:
                    if (empfangeneDaten[9] == 0) {
                        //belegt
                        dieGUI.setzeRMKStatus(3, false);
                        break;
                    }
                    //frei
                    dieGUI.setzeRMKStatus(3, true);
                    break;
                case 4:
                    if (empfangeneDaten[9] == 0) {
                        //belegt
                        dieGUI.setzeRMKStatus(4, false);
                        break;
                    }
                    //frei
                    dieGUI.setzeRMKStatus(4, true);
                    break;
                case 5:
                    if (empfangeneDaten[9] == 0) {
                        //belegt
                        dieGUI.setzeRMKStatus(5, false);
                        break;
                    }
                    //frei
                    dieGUI.setzeRMKStatus(5, true);
                    break;
                case 6:
                    if (empfangeneDaten[9] == 0) {
                        //belegt
                        dieGUI.setzeRMKStatus(6, false);
                        break;
                    }
                    //frei
                    dieGUI.setzeRMKStatus(6, true);
                    break;
                case 7:
                    if (empfangeneDaten[9] == 0) {
                        //belegt
                        dieGUI.setzeRMKStatus(7, false);
                        break;
                    }
                    //frei
                    dieGUI.setzeRMKStatus(7, true);
                    break;
                default:
                    System.out.println("Falscher RMK-Ausgang!");
            }
        }
    }

    public void clearPortList() {
        dieGUI.clearPortList();
    }

    @Override
    public void stelleWeiche(int weichenNummer, char stellung) {
        byte weichenAdresse;
        byte stellungWert;
        //Stellung rund: 0 | gerade: 1
        switch (stellung) {
            case 'r':
                stellungWert = 0;
                break;
            case 'g':
                stellungWert = 1;
                break;
            default:
                stellungWert = 0;
                System.out.println("Falscher Stellungswert: Stellung auf 1 gesetzt (gerade)");
                break;
        }

        switch (weichenNummer) {
            // Knoten 32 und 42 gleiche Weiche, aber Knoten 9 dazwischen
            case 32:
            case 42:
                weichenAdresse = 24;
                break;
            case 33:
                weichenAdresse = 23;
                break;
            case 34:
                weichenAdresse = 15;
                break;
            case 35:
                weichenAdresse = 3;
                break;
            case 36:
                weichenAdresse = 2;
                break;
            case 37:
                weichenAdresse = 1;
                break;
            case 38:
                weichenAdresse = 0;
                break;
            case 39:
                weichenAdresse = 13;
                break;
            case 40:
                weichenAdresse = 12;
                break;
            case 41:
                weichenAdresse = 21;
                break;
            case 43:
                weichenAdresse = 22;
                break;
            case 44:
                weichenAdresse = 14;
                break;
            default:
                weichenAdresse = 0;
                System.out.println("Keine gültige Weichennummer gewählt: Erste Weiche gewählt");
                break;
        }

        System.out.println("Weiche stellen");
        dieDaten[0] = 0x0;  //Priorität 0
        dieDaten[1] = 0x16; //CAN-ID: 0x16 = 22d (Zubehör Schalten)
        dieDaten[2] = 0x03; //Hash
        dieDaten[3] = 0x00; //Hash
        dieDaten[4] = 6;    //DLC:6 Daten Bytes
        dieDaten[5] = 0;
        dieDaten[6] = 0;
        dieDaten[7] = (byte) 0x30; //0x3000; //MM1,2 Zubehörartikeldecoder (40 kHz, 320 & 1024 Adressen)
        dieDaten[8] = (byte) weichenAdresse;
        // 0: Rechts/Rund 1: Gerade (Doku S.37)
        dieDaten[9] = stellungWert;
        dieDaten[10] = (byte) 1; //Strom ein
        dieAnlage.schreibeAufCAN(dieDaten);
        //Halbe Sekunde warten
        try {
            Thread.sleep(500); //500ms warten
        } catch (InterruptedException ex) {
            Logger.getLogger(Steuerung.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Noch einmal senden ohne Strom
        dieDaten[9] = stellungWert;
        dieDaten[10] = (byte) 0; //Strom aus
        dieAnlage.schreibeAufCAN(dieDaten);
    }

    @Override
    public byte gibRMKAdresse(int RMKNummer) {

        switch (RMKNummer) {
            case 1:
                return 19; //Adresse des RMK

            case 2:
                return 20; //Adresse des RMK

            case 3:
                return 22; //Adresse des RMK

            case 4:
                return 31; //Adresse des RMK

            case 5:
                return 25; //Adresse des RMK

            case 6:
                return 27; //Adresse des RMK

            case 7:
                return 18; //Adresse des RMK

            case 8:
                return 2; //Adresse des RMK

            case 9:
                return 6; //Adresse des RMK

            case 10:
                return 7; //Adresse des RMK

            case 11:
                return 16; //Adresse des RMK

            case 12:
                return 21; //Adresse des RMK

            case 13:
                return 23; //Adresse des RMK

            case 14:
                return 32; //Adresse des RMK

            case 15:
                return 26; //Adresse des RMK

            case 16:
                return 29; //Adresse des RMK

            case 17:
                return 11; //Adresse des RMK

            case 18:
                return 14; //Adresse des RMK

            case 19:
                return 5; //Adresse des RMK

            case 20:
                return 8; //Adresse des RMK

            case 21:
                return 3; //Adresse des RMK

            case 22:
                return 9; //Adresse des RMK

            case 23:
                return 17; //Adresse des RMK

            case 24:
                return 24; //Adresse des RMK

            case 25:
                return 28; //Adresse des RMK

            case 26:
                return 30; //Adresse des RMK

            case 27:
                return 10; //Adresse des RMK

            case 28:
                return 15; //Adresse des RMK

            case 29:
                return 12; //Adresse des RMK

            case 30:
                return 1; //Adresse des RMK

            case 31:
                return 4; //Adresse des RMK
            //Abstellgleis zur Erweiterung (wird nicht benutzt)
            case 32:
                return 13; //Adresse des RMK
            default:
                System.out.println("Keine gültige RMK-Nummer gewählt: "
                        + "Erster Abschnitt gewählt mit Adresse 19");
                return 19;
        }
    }

    @Override
    public void sendeRMK(int RMKNummer) {
        //Rückmeldeevent senden
        dieDaten[0] = 2; //Rückmeldung Prio:2
        dieDaten[1] = 0x22; //CAN-ID: 0x22/34d Rückmelde Event
        dieDaten[2] = (byte) 11; //Hash
        dieDaten[3] = (byte) 0; // (RMK-Modul-Nummer) wird nicht unbedingt gebraucht
        dieDaten[4] = 4; //DLC: 4
        dieDaten[5] = 0; //Gerätekenner
        dieDaten[6] = 0; //Gerätekenner
        dieDaten[7] = 0; //Kontaktkennung
        dieDaten[8] = gibRMKAdresse(RMKNummer);
        //Rest mit Nullen auffüllen
        dieDaten[9] = 0;
        dieDaten[10] = 0;
        dieDaten[11] = 0;
        dieDaten[12] = 0;

        dieAnlage.schreibeAufCAN(dieDaten);

    }

    @Override
    public boolean leseRMK(int RMKNummer) {
        System.out.println("Empfangene Daten in leseRMK-Methode: ");

        for (byte dataByte : empfangeneDaten) {
            System.out.print(dataByte + " ");
        }
        //Absatz
        System.out.println("");

        System.out.println("Adresse der RMKNummer: " + gibRMKAdresse(RMKNummer));

        //Rückmeldeabschnitt prüfen
        //Standardmäßig frei = false
        boolean zustand = false;
        if (empfangeneDaten[0] == 0 && empfangeneDaten[1] == 35 && empfangeneDaten[2] == 11
                /* empfangeneDaten[3] == 1 Modul-Nr.=1*/ && empfangeneDaten[4] == 8 && empfangeneDaten[8] == gibRMKAdresse(RMKNummer)) {
            if (empfangeneDaten[9] == 0) {
                //belegt
                System.out.println("RMK '" + RMKNummer + "' mit Adresse '" + gibRMKAdresse(RMKNummer) + "' belegt!");
                zustand = true;
            } else {
                //frei
                System.out.println("RMK nicht belegt");
                zustand = false;
            }
        }
        return zustand;
    }

    @Override
    public ArrayList gibBelegteRMK() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fahreLok(int LokName, int geschwindigkeit) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void sendeWeichenPosition() {
        // 442A80x DLC 2 Data 0x00 0xE0
        System.out.println("sende Weichen Position");
        dieDaten[0] = (byte) 0x44;  //Priorität 0
        dieDaten[1] = (byte) 0x2A; //CAN-ID: 0x16 = 22d (Zubehör Schalten)
        dieDaten[2] = (byte) 0x80; //Hash
        dieDaten[3] = (byte) 0x00; //Hash
        dieDaten[4] = (byte) 2;    //DLC:6 Daten Bytes
        dieDaten[5] = (byte) 0x00;
        dieDaten[6] = (byte) 0xE0;
        dieDaten[7] = (byte) 0;
        dieDaten[8] = (byte) 0;
        dieDaten[9] = (byte) 0;
        dieDaten[10] = (byte) 0;
        dieDaten[11] = (byte) 0;
        dieDaten[12] = (byte) 0;

        dieAnlage.schreibeAufCAN(dieDaten);
    }

    public void setStartPoint(int vonKnoten) {
        startPoint = vonKnoten;
    }

    public int getStartPoint() {
        return startPoint;
    }

    public void setEndPoint(int bisKnoten) {
        endPoint = bisKnoten;
    }

    public int getEndPoint() {
        return endPoint;
    }

    public void findeWeg() {
        dijkstra.init();
        weg = dijkstra.findeWeg(startPoint, endPoint);
//        System.out.println("startPoint: " + startPoint);
//        System.out.println("endPoint: " + endPoint);
        stelleWeichen(weg);

    }

    private void stelleWeichen(List<Knoten> weg) {
        // Weichen entsprechend dem Weg stellen
        for (Knoten punkt : weg) {
            System.out.println("Knoten-Name: " + punkt.getName());
            int nameNachfolger = 0; //Standard-Wert
            //letzes Element hat keinen Nachfolger
            if ((weg.size() - 1) != weg.indexOf(punkt)) {
                nameNachfolger = weg.get(weg.indexOf(punkt) + 1).getName();
            }
            System.out.println("Nachfolger-Name: " + nameNachfolger);
            //Fahrtrichtung mit Gewichtung 1
            switch (punkt.getName()) {
                case 32:
                    if (nameNachfolger == 9) {
                        stelleWeiche(32, 'r');
                    }//ACHTUNG 6/4 Problem
                    if (nameNachfolger == 33) {
                        stelleWeiche(32, 'g');
                    }
                    break;
                case 33:
                    if (nameNachfolger == 31) {
                        stelleWeiche(33, 'g');
                    }//falsche Stellung
                    if (nameNachfolger == 30) {
                        stelleWeiche(33, 'r');
                    }//falsche Stellung
                    break;
                case 34:
                    if (nameNachfolger == 35) {
                        stelleWeiche(34, 'g');
                    }
                    //if(nameNachfolger ==35){stelleWeiche(34,'r');} Abstellgleis wird hier nicht benötigt
                    break;
                case 35:
                    if (nameNachfolger == 36) {
                        stelleWeiche(35, 'g');
                    } //faslsche Stellung
                    if (nameNachfolger == 6) {
                        stelleWeiche(35, 'r');
                    }//falsche Stellung
                    break;
                case 36:
                    if (nameNachfolger == 37) {
                        stelleWeiche(36, 'g');
                    }
                    break;
                case 37:
                    if (nameNachfolger == 15) {
                        stelleWeiche(37, 'g');
                    }
                    break;
                case 38:
                    if (nameNachfolger == 14) {
                        stelleWeiche(38, 'g');
                    }//falsche Stellung
                    if (nameNachfolger == 4) {
                        stelleWeiche(38, 'r');
                    }//falsche Stellung
                    break;
                case 39:
                    //if (nameNachfolger == 24) {stelleWeiche(39, 'r');} INNERKREIS
                    if (nameNachfolger == 13) {
                        stelleWeiche(39, 'g');
                    }
                    break;
                case 12://ACHTUNG BRAINFUCK WEGEN WEGFALLEN EINES KNOTENPUNKTES!!!!
                    if (nameNachfolger == 40) {
                        stelleWeiche(40, 'r');
                    }
                    if (nameNachfolger == 11) {
                        stelleWeiche(40, 'g');
                    }
                    break;
                case 40:
                    if (nameNachfolger == 10) {
                        stelleWeiche(40, 'g');
                    }
                    break;
                case 41:

                    if (nameNachfolger == 20) {
                        stelleWeiche(41, 'g');
                    }
                    break;
                case 19: // BRAINFUCK

                    if (nameNachfolger == 42) {
                        stelleWeiche(42, 'g');
                    }//ACHTUNG 6/4 Problem!!
                    break;
                case 9:
                    if (nameNachfolger == 42) {
                        stelleWeiche(32, 'r');
                    }
                    break;
                case 43:
                    //if (nameNachfolger == 28) {stelleWeiche(43, 'r');} INNERKREIS
                    if (nameNachfolger == 18) {
                        stelleWeiche(43, 'g');
                    }
                    break;
                /*case 44:
                 if (nameNachfolger == 23) {stelleWeiche(44, 'r');}
                 if(nameNachfolger ==26){stelleWeiche(44,'g');}
                 if(nameNachfolger ==22){stelleWeiche(44,'r');}
                 if(nameNachfolger ==27){stelleWeiche(44,'g');}
                 break; */
                default:
                    System.out.println("Falsche Weichenknoten-Nummer");
                    break;
            }

            //werden nicht gebraucht
//            Knoten startKnoten = weg.get(0);
//            Knoten endKnoten = weg.get(weg.size() - 1);
            if (punkt.getName() < 32) {
                //Losfahren, wenn Lok auf Startknoten steht
//                if (punkt.getName() == startPoint) {
                System.out.println("RM-Event, ob Anfang oder Ende belegt sind ");
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Steuerung.class.getName()).log(Level.SEVERE, null, ex);
                }
                sendeRMK(startPoint);
//                }
                //Lok anhalten, wenn sie am Ziel angekommen ist
//                if (punkt.getName() == endPoint) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Steuerung.class.getName()).log(Level.SEVERE, null, ex);
                }
                sendeRMK(endPoint);
//                }
            }

//            //Kanten entfernen, deren Rückmeldeabschnitte belegt sind
//            List<Knoten> entfernteKanten = new ArrayList<>();
//            Knoten vorgaenger = null;
//            //erstes Element hat keinen Vorgänger
//            if (weg.indexOf(punkt) != 0) {
//                vorgaenger = weg.get(weg.indexOf(punkt) - 1);
//            }
//            if (punkt.getName() < 32) {
//                sendeRMK(punkt.getName());
//                if (leseRMK(punkt.getName())) //true liefert belegt!
//                {
//                    System.out.println("RMK " + punkt.getName() + " belegt");
//                    if (weg.indexOf(punkt) != 0) {
//                        dijkstra.kanteEntfernen(vorgaenger, punkt);
//                    }
//                    entfernteKanten.add(punkt);
//                }
//            }
        }
    }

    public void RMKfuerFahren() {
        //Losfahren, wenn Lok auf Startknoten steht
        if (leseRMK(startPoint)) {
            System.out.println("Lok mit v=20 fahren, da Lok auf Startpunkt");
            fahreLok(20);
        }
        //Lok anhalten, wenn sie am Ziel angekommen ist
        if (leseRMK(endPoint)) {
            fahreLok(0);
        }
    }

    public void leseGeschwindigkeit() {
        //Slider auf aktuelle Lok Geschwindigkeit setzen
        if (empfangeneDaten[0] == 0 && empfangeneDaten[1] == 9
                && empfangeneDaten[2] == 7 && empfangeneDaten[3] == 31
                && empfangeneDaten[4] == 6) {
            //Geschwindigkeit nur setzen, wenn größer als 0
            int geschwindigkeitLB = empfangeneDaten[10];
            int geschwindigkeitHB = empfangeneDaten[9];
            int geschwindigkeit = geschwindigkeitLB;
            geschwindigkeit += geschwindigkeitHB << 8;
            System.out.println("Geschwindigkeit " + geschwindigkeit + " wird auf Slider gesetzt");
            dieGUI.setzeGeschwindigkeit(geschwindigkeit);
        }
    }
    //Ende Methoden
}
