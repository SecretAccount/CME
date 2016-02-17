package de.cme.dijkstra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Dijkstra {

    private final static int KNOTEN_ANZAHL = 44;
    private Knoten[] knoten = new Knoten[KNOTEN_ANZAHL]; //44 Knoten/Waypoints
    //Graphen setzen
    private Graph graph = new Graph(KNOTEN_ANZAHL); //44 Knoten/Waypoints
    //Hilfsliste zur Wegfindung erstellen
    private Hilfsliste hilfsliste = new Hilfsliste(KNOTEN_ANZAHL);
    //Gewichtung des Pfades
    private int pfadgewichtEndPfad;
    

    public int getPfadgewichtEndPfad() {
        return pfadgewichtEndPfad;
    }
    
    public void init() {

        // Knoten werden erstellt
        //Knoten 1-44
        for (int i = 0; i < KNOTEN_ANZAHL; i++) {
            knoten[i] = new Knoten(i + 1);
            graph.knotenEinfuegen(knoten[i]);
        }

        //Kanten werden gemäß Vorlage verknüpft  Gewichtung 1 Fahrtrichtung
        
        //TEST
        graph.kanteEinfuegen(knoten[34], knoten[4], 1);  // Knoten 35-5
        
        //TEST
        
        graph.kanteEinfuegen(knoten[0], knoten[39], 1);  // Knoten 1-40
        graph.kanteEinfuegen(knoten[1], knoten[0], 1);   // Knoten 2-1
        graph.kanteEinfuegen(knoten[2], knoten[1], 1);   // Knoten 3-2
        graph.kanteEinfuegen(knoten[3], knoten[2], 1);   // Knoten 4-3
        graph.kanteEinfuegen(knoten[4], knoten[3], 1);   // Knoten 5-4
//        graph.kanteEinfuegen(knoten[5], knoten[4], 1);   // Knoten 6-5    wieder einkommentieren
        graph.kanteEinfuegen(knoten[6], knoten[34], 1);  // Knoten 7-35
        graph.kanteEinfuegen(knoten[7], knoten[6], 1);   // Knoten 8-7
        graph.kanteEinfuegen(knoten[8], knoten[41], 1);  // Knoten 9-42
        graph.kanteEinfuegen(knoten[9], knoten[31], 1);  // Knoten 10-32
        graph.kanteEinfuegen(knoten[10], knoten[40], 1); // Knoten 11-41
        graph.kanteEinfuegen(knoten[11], knoten[10], 1); // Knoten 12-11
        graph.kanteEinfuegen(knoten[12], knoten[11], 1); // Knoten 13-12
        graph.kanteEinfuegen(knoten[13], knoten[38], 1); // Knoten 14-39
        graph.kanteEinfuegen(knoten[14], knoten[37], 1); // Knoten 15-38
        graph.kanteEinfuegen(knoten[15], knoten[35], 1); // Knoten 16-36
        graph.kanteEinfuegen(knoten[16], knoten[15], 1); // Knoten 17-16
        graph.kanteEinfuegen(knoten[17], knoten[16], 1); // Knoten 18-17
        graph.kanteEinfuegen(knoten[18], knoten[41], 1); // Knoten 19-42
        graph.kanteEinfuegen(knoten[19], knoten[18], 1); // Knoten 20-19

        graph.kanteEinfuegen(knoten[28], knoten[33], 1); // Knoten 29-34
        graph.kanteEinfuegen(knoten[29], knoten[28], 1); // Knoten 30-29
        graph.kanteEinfuegen(knoten[30], knoten[7], 1);  // Knoten 31-8

        graph.kanteEinfuegen(knoten[31], knoten[8], 1);  // Knoten 32-9
        graph.kanteEinfuegen(knoten[31], knoten[32], 1); // Knoten 32-33
        graph.kanteEinfuegen(knoten[32], knoten[29], 1); // Knoten 33-30
        graph.kanteEinfuegen(knoten[32], knoten[30], 1); // Knoten 33-31

        graph.kanteEinfuegen(knoten[11], knoten[39], 1); // Knoten 12-40
        graph.kanteEinfuegen(knoten[37], knoten[3], 1); // Knoten 38-4
        graph.kanteEinfuegen(knoten[34], knoten[35], 1); // Knoten 35-36
//        graph.kanteEinfuegen(knoten[34], knoten[5], 1); // Knoten 35-6    wieder einkommentieren
        graph.kanteEinfuegen(knoten[33], knoten[34], 1); // Knoten 34-35

        graph.kanteEinfuegen(knoten[35], knoten[36], 1); // Knoten 36-37
        graph.kanteEinfuegen(knoten[36], knoten[14], 1); // Knoten 37-15 
        graph.kanteEinfuegen(knoten[37], knoten[13], 1); // Knoten 38-14
        graph.kanteEinfuegen(knoten[38], knoten[12], 1); // Knoten 39-13
        graph.kanteEinfuegen(knoten[39], knoten[9], 1);  // Knoten 40-10
        graph.kanteEinfuegen(knoten[40], knoten[19], 1); // Knoten 41-20
        graph.kanteEinfuegen(knoten[41], knoten[42], 1); // Knoten 42-43
        graph.kanteEinfuegen(knoten[42], knoten[17], 1); // Knoten 43-18
        
        //Als Test, um zu testende Kanten (8-7 und 18-17) von Start an zu entfernen
        /*
        if(graph.kanteEntfernen(knoten[7], knoten[6])) {
            System.out.println("Kanten 8-7 erfolgreich entfernt");
        } else {
            System.out.println("Kanten 8-7 entfernen fehlgeschlagen!");
        }
        
        if(graph.kanteEntfernen(knoten[17], knoten[16])) {
            System.out.println("Kanten 18-17 erfolgreich entfernt");
        } else {
            System.out.println("Kanten 18-17 entfernen fehlgeschlagen!");
        }
        */

        // Kanten werden gemäß Vorlage verknüpft  Gewichtung 2  entgegen der Fahrtrichtung
        /*
         graph.kanteEinfuegen(knoten[0], knoten[1], 2);   // Knoten 1-2
         graph.kanteEinfuegen(knoten[1], knoten[2], 2);   // Knoten 2-3
         graph.kanteEinfuegen(knoten[2], knoten[3], 2);   // Knoten 3-4
         graph.kanteEinfuegen(knoten[4], knoten[5], 2);   // Knoten 5-6
         graph.kanteEinfuegen(knoten[6], knoten[7], 2);   // Knoten 7-8
         graph.kanteEinfuegen(knoten[7], knoten[30], 2);  // Knoten 8-31
         graph.kanteEinfuegen(knoten[8], knoten[31], 2);  // Knoten 9-32
         graph.kanteEinfuegen(knoten[9], knoten[39], 2);  // Knoten 10-40
         graph.kanteEinfuegen(knoten[11], knoten[12], 2); // Knoten 12-13
         graph.kanteEinfuegen(knoten[12], knoten[38], 2); // Knoten 13-39
         graph.kanteEinfuegen(knoten[13], knoten[37], 2); // Knoten 14-38
         graph.kanteEinfuegen(knoten[14], knoten[36], 2); // Knoten 15-37
         graph.kanteEinfuegen(knoten[15], knoten[16], 2); // Knoten 16-17
         graph.kanteEinfuegen(knoten[16], knoten[17], 2); // Knoten 17-18
         graph.kanteEinfuegen(knoten[17], knoten[42], 2); // Knoten 18-43
         graph.kanteEinfuegen(knoten[18], knoten[19], 2); // Knoten 19-20
         graph.kanteEinfuegen(knoten[19], knoten[40], 2); // Knoten 20-41

         graph.kanteEinfuegen(knoten[28], knoten[29], 2); // Knoten 29-30
         graph.kanteEinfuegen(knoten[29], knoten[32], 2); // Knoten 30-33
         graph.kanteEinfuegen(knoten[30], knoten[32], 2); // Knoten 31-33

         graph.kanteEinfuegen(knoten[35], knoten[34], 2); // Knoten 36-35
         graph.kanteEinfuegen(knoten[34], knoten[33], 2); // Knoten 35-34
         graph.kanteEinfuegen(knoten[10], knoten[11], 2); // Knoten 11-12
         graph.kanteEinfuegen(knoten[39], knoten[11], 2); // Knoten 40-12
         graph.kanteEinfuegen(knoten[3],  knoten[37], 2); // Knoten 4-38

         graph.kanteEinfuegen(knoten[31], knoten[9], 2);  // Knoten 32-10
         graph.kanteEinfuegen(knoten[32], knoten[31], 2); // Knoten 33-32
         graph.kanteEinfuegen(knoten[33], knoten[28], 2); // Knoten 34-29
         graph.kanteEinfuegen(knoten[34], knoten[6], 2);  // Knoten 35-7
         graph.kanteEinfuegen(knoten[35], knoten[15], 2); // Knoten 36-16
         graph.kanteEinfuegen(knoten[36], knoten[35], 2); // Knoten 37-36
         graph.kanteEinfuegen(knoten[37], knoten[14], 2); // Knoten 38-15
         graph.kanteEinfuegen(knoten[38], knoten[13], 2); // Knoten 39-14
         graph.kanteEinfuegen(knoten[39], knoten[0], 2);  // Knoten 40-1
         graph.kanteEinfuegen(knoten[40], knoten[10], 2); // Knoten 41-11
         graph.kanteEinfuegen(knoten[41], knoten[8], 2);  // Knoten 42-9
         graph.kanteEinfuegen(knoten[41], knoten[18], 2); // Knoten 42-19
         graph.kanteEinfuegen(knoten[42], knoten[41], 2); // Knoten 43-42
         */
         //Kanten werden gemäß Vorlage verknüpft Mittlekreis mit jeweils Gewichtung
         //von 50 (beide Fahrtrichtungen)
         graph.kanteEinfuegen(knoten[20], knoten[40], 50); // Knoten 21-41
         graph.kanteEinfuegen(knoten[20], knoten[21], 50); // Knoten 21-22
         graph.kanteEinfuegen(knoten[21], knoten[20], 50); // Knoten 22-21
         graph.kanteEinfuegen(knoten[21], knoten[43], 50); // Knoten 22-44
         graph.kanteEinfuegen(knoten[43], knoten[21], 50); // Knoten 44-22
         graph.kanteEinfuegen(knoten[43], knoten[22], 50); // Knoten 44-23
         graph.kanteEinfuegen(knoten[43], knoten[25], 50); // Knoten 44-26
         graph.kanteEinfuegen(knoten[43], knoten[26], 50); // Knoten 44-27
         graph.kanteEinfuegen(knoten[22], knoten[43], 50); // Knoten 23-44
         graph.kanteEinfuegen(knoten[22], knoten[23], 50); // Knoten 23-24
         graph.kanteEinfuegen(knoten[23], knoten[38], 50); // Knoten 24-39
         graph.kanteEinfuegen(knoten[23], knoten[22], 50); // Knoten 24-23
         graph.kanteEinfuegen(knoten[38], knoten[23], 50); // Knoten 39-24
         graph.kanteEinfuegen(knoten[25], knoten[43], 50); // Knoten 26-44
         graph.kanteEinfuegen(knoten[25], knoten[24], 50); // Knoten 26-25
         graph.kanteEinfuegen(knoten[24], knoten[36], 50); // Knoten 25-37
         graph.kanteEinfuegen(knoten[24], knoten[25], 50); // Knoten 25-26
         graph.kanteEinfuegen(knoten[26], knoten[43], 50); // Knoten 27-44
         graph.kanteEinfuegen(knoten[26], knoten[27], 50); // Knoten 27-28
         graph.kanteEinfuegen(knoten[27], knoten[26], 50); // Knoten 28-27
         graph.kanteEinfuegen(knoten[27], knoten[42], 50); // Knoten 28-43
         graph.kanteEinfuegen(knoten[42], knoten[27], 50); // Knoten 43-28
        
         
    }

    private void findeStrecke(Knoten startknoten) {
        //Dijkstra
        startknoten.setStart();
        
        //Knoten in die Hilfsliste einfügen
        for (int i = 0; i < KNOTEN_ANZAHL; i++) {
            hilfsliste.add(knoten[i]);
        }
        hilfsliste.sortieren();
        while (hilfsliste.allesBearbeitet() == false) {
            Knoten erster = hilfsliste.ersterUnbearbeiteter();
            //Nachfolger bestimmen und eventuell Pfadgewicht und Vorgaenger aktualisieren
            Knoten[] nachfolger = graph.getNachfolger(erster);
            for (int i = 0; i < nachfolger.length; i++) {
                if (graph.getKantenGewicht(erster, nachfolger[i]) + erster.getPfadgewicht() < nachfolger[i].getPfadgewicht()) {
                    //Relaxation
                    nachfolger[i].setPfadgewicht(graph.getKantenGewicht(erster, nachfolger[i]) + erster.getPfadgewicht());
                    nachfolger[i].setVorgaenger(erster);
                }
            }
            erster.setBearbeitet(true);
            hilfsliste.sortieren();
        }
    }

    public void showList() {
        //DEBUGGING
        Knoten[] knotenliste = hilfsliste.getListe();
        System.out.println("Knotenliste ausgeben: ");
        System.out.println("Nr." + 1 + " Name: " + knotenliste[0].getName() + " Gew.: " + knotenliste[0].getPfadgewicht() + " Kein Vorgänger");
        for (int i = 1; i < knotenliste.length; i++) {
            System.out.println("Nr." + (i + 1) + " Name: " + knotenliste[i].getName() + " Gew.: " + knotenliste[i].getPfadgewicht() + " Vorg.: " + knotenliste[i].getVorgaenger().getName());
        }
    }

    public List findeWeg(int startpunkt, int endpunkt) {
        //mit Dijkstra-Algorithmus kürzeste Strecke berechnen vom Startpunkt aus
        findeStrecke(knoten[startpunkt - 1]);
        //Liste der Knoten mit dem kürzesten Weg zum Startknoten
        Knoten[] knotenliste = hilfsliste.getListe();
        //DEBUG-Informationen vor Wegfindung ausgeben
//        showList();
        //Liste der Wegpunkte
        List<Knoten> way = new ArrayList<>();
        //Endpunkt hinzufügen
        for (Knoten lKnoten : knotenliste) {
            if (lKnoten.getName() == endpunkt) {
                //Manchmal Bug, wenn Endknoten in Hilfsliste am Anfang steht, 
                //-> NullPointerException in Z.242, da der Anfangsknoten 
                //der Hilfsliste keinen Vorgänger hat
//                DEBUGGING: System.out.println("Name des ersten Knotens: " + lKnoten.getName());
                //Gewichtung des Pfades speichern
                pfadgewichtEndPfad = lKnoten.getPfadgewicht();
                way.add(lKnoten);
            }
        }

        //immer Startknoten
        //System.out.println("Startknoten: " + knotenliste[0].getName());
        System.out.println("Startknoten: \t" + knoten[startpunkt - 1].getName());
        System.out.println("Endknoten: \t" + knoten[endpunkt - 1].getName());
        int vorgaengerNr = 0;
        //do-while Schleife
        do {
            //füge Vorgänger der Liste hinzu
            way.add(way.get(vorgaengerNr).getVorgaenger());
            
            vorgaengerNr++;
        //breche ab, wenn der Startknoten in die Liste eingetragen wird
        //bzw. breche ab, sobald der Name des Vorgängers nicht mehr ungleich (=gleich) wie der Name des Endknotens ist
        } while(way.get(vorgaengerNr).getName() != knotenliste[0].getName());

        /*
        //oder mit unendlicher Schleife
        while(true) {
            //füge Vorgänger der Liste hinzu
            way.add(way.get(vorgaengerNr).getVorgaenger());
            
                //breche ab, wenn der Startknoten in die Liste eingetragen wird
                  if (way.get(vorgaengerNr + 1).getName() == knotenliste[0].getName()) {
                      break;
                  }
            vorgaengerNr++;
        }
        */
        
        
        //Liste umdrehen
        Collections.reverse(way);

        System.out.println("Umgedrehte Liste");
        way.stream().forEach((lKnoten) -> {
            System.out.println("Name: " + lKnoten.getName());
        });

        /*
         * oder man schreibt:
         for(Knoten knoten : way) {
         System.out.println("Name: " + knoten.getName());
         }
         */
        return way;
        
        
    }

    //Knoten übergeben
    public boolean entferneKante(Knoten von, Knoten bis) {
        return graph.kanteEntfernen(von, bis);
    }
    
    //Knotennummer übergeben
    public boolean entferneKante(int vonKnotenNummer, int bisKnotenNummer) {
        //aus Knoten-Array die Kanten entfernen
        return graph.kanteEntfernen(knoten[vonKnotenNummer - 1], knoten[bisKnotenNummer - 1]);
    }
    
    //Knotennummer übergeben (boolean Parameter wird nicht gebraucht, nur dazu da, 
    //dass nicht zwei Methoden mit gleicher Signatur existieren)
    public boolean entferneKante(int vonKnotenNummer, int bisKnotenNummer, boolean test) {
        //aus Knoten-Array die Kanten entfernen
        return graph.kanteEntfernen(vonKnotenNummer, bisKnotenNummer);
    }
}
