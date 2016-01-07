package de.cme;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pascal
 */
public class Steuerung {

    // Anfang Attribute
    private GUI dieGUI;
    private Anlage dieAnlage;

    private byte[] dieDaten;
    private byte[] empfangeneDaten;

    private int gewWeichenModul;
    private int gewRMKModul;
    private byte lokAdresse;

    //Konstanten
    //Standard-Hash
    private final byte HIGH_BYTE_HASH = (byte) 175;
    private final byte LOW_BYTE_HASH = (byte) 83;
    private final byte STANDARD_LOK_ADRESSE = 24;
    // Ende Attribute

    public Steuerung(GUI eineGUI) {
        dieGUI = eineGUI; // bidirektional
        dieAnlage = new Anlage(this); // bidirektional

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

    //Überladene Methode: mit Adresse oder Adresse von Steuerung benutzen
    public void fahreLok(int geschwindigkeit) {
        fahreLok(lokAdresse, geschwindigkeit);
    }

    public void fahreLok(byte adresse, int geschwindigkeit) {
        // Systemfahrstufe = 1 + (Gleisfahrstufe - 1) * Schrittweite
        System.out.println("Geschw. auf GUI: " + geschwindigkeit);
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
            dieDaten[9] = (byte) geschwHighByte; // High-Byte: 1. Byte der Geschw.: geschwLowByte 8Bits nach rechts verschoben
            dieDaten[10] = (byte) geschwLowByte; // Low-Byte: 2. Byte der Geschw.: 8-Bits des Werts des Schiebereglers * 10
            dieDaten[11] = (byte) 0;             // Rest mit 0 auffüllen
            dieDaten[12] = (byte) 0;             // Rest mit 0 auffüllen
            dieAnlage.schreibeAufCAN(dieDaten);  // Senden
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
        for (int i = 0; i < 4; i++) {
            if (empfangeneDaten[0] == 0 && empfangeneDaten[1] == 23
                    && (empfangeneDaten[7] == 0x30 && empfangeneDaten[8] == (0 + i) /* (hier vom 2. Modul mit Adresse 5 | 1. Adresse: 0x3000/12288d)*/)) {
//                if (empfangeneDaten[9] == 253) { //richtig
                if (empfangeneDaten[9] == 1) { //Test
                    //position = 1; //Grün/rechts/1 /nicht benötigt
                    dieGUI.positionRechts(i + 1);
                }
//                if (empfangeneDaten[9] == 254) { //richtig
                if (empfangeneDaten[9] == 0) { //Test
                    // position = 0; //Rot/links/0 /nicht benötigt
                    dieGUI.positionLinks(i + 1);
                }
                if (empfangeneDaten[9] == 255) {
                    //position = 2; //Gelb Fehler
                    dieGUI.positionFehler(i + 1);
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            if (empfangeneDaten[0] == 0 && empfangeneDaten[1] == 23
                    && (empfangeneDaten[7] == 0x30 && empfangeneDaten[8] == (12 + i) /* (hier vom 2. Modul mit Adresse 5 | 1. Adresse: 0x3000/12288d)*/)) {
//                if (empfangeneDaten[9] == 253) { //richtig
                if (empfangeneDaten[9] == 1) { //Test
                    //position = 1; //Grün/rechts/1 /nicht benötigt
                    dieGUI.positionRechts(i + 1);
                }
//                if (empfangeneDaten[9] == 254) { //richtig
                if (empfangeneDaten[9] == 0) { //Test
                    // position = 0; //Rot/links/0 /nicht benötigt
                    dieGUI.positionLinks(i + 1);
                }
                if (empfangeneDaten[9] == 255) {
                    //position = 2; //Gelb Fehler
                    dieGUI.positionFehler(i + 1);
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            if (empfangeneDaten[0] == 0 && empfangeneDaten[1] == 23
                    && (empfangeneDaten[7] == 0x30 && empfangeneDaten[8] == (21 + i) /* (hier vom 2. Modul mit Adresse 5 | 1. Adresse: 0x3000/12288d)*/)) {
//                if (empfangeneDaten[9] == 253) { //richtig
                if (empfangeneDaten[9] == 1) { //Test
                    //position = 1; //Grün/rechts/1 /nicht benötigt
                    dieGUI.positionRechts(i + 1);
                }
//                if (empfangeneDaten[9] == 254) { //richtig
                if (empfangeneDaten[9] == 0) { //Test
                    // position = 0; //Rot/links/0 /nicht benötigt
                    dieGUI.positionLinks(i + 1);
                }
                if (empfangeneDaten[9] == 255) {
                    //position = 2; //Gelb Fehler
                    dieGUI.positionFehler(i + 1);
                }
            }
        }
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
                Thread.sleep(10); //10ms warten
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

    void clearPortList() {
        dieGUI.clearPortList();
    }
    //Ende Methoden
}
