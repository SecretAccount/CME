package de.cme.dijkstra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Dijkstra {

    private final static int KNOTEN_ANZAHL = 56;
    private Knoten[] knoten = new Knoten[KNOTEN_ANZAHL]; //56 Knoten/Waypoints
    //Graphen setzen
    private Graph graph = new Graph(KNOTEN_ANZAHL); //56 Knoten/Waypoints
    //Hilfsliste zur Wegfindung erstellen
    private Hilfsliste hilfsliste = new Hilfsliste(KNOTEN_ANZAHL);
    // Knoten werden erstellt

    public void init() {

        //Knoten 1-56
        for (int i = 0; i < KNOTEN_ANZAHL; i++) {
            knoten[i] = new Knoten(i + 1);
            graph.knotenEinfuegen(knoten[i]);
        }

        //Kanten werden gemäß Vorlage verknüpft  Gewichtung 1 Fahrtrichtung
        graph.kanteEinfuegen(knoten[0], knoten[51], 1);  // Knoten 1-52
        graph.kanteEinfuegen(knoten[1], knoten[0], 1);   // Knoten 2-1
        graph.kanteEinfuegen(knoten[2], knoten[1], 1);   // Knoten 3-2
        graph.kanteEinfuegen(knoten[3], knoten[2], 1);   // Knoten 4-3
        graph.kanteEinfuegen(knoten[4], knoten[48], 1);  // Knoten 5-49
        graph.kanteEinfuegen(knoten[5], knoten[4], 1);   // Knoten 6-5
        graph.kanteEinfuegen(knoten[6], knoten[43], 1);  // Knoten 7-44
        graph.kanteEinfuegen(knoten[7], knoten[6], 1);   // Knoten 8-7
        graph.kanteEinfuegen(knoten[8], knoten[53], 1);  // Knoten 9-54
        graph.kanteEinfuegen(knoten[9], knoten[39], 1);  // Knoten 10-40
        graph.kanteEinfuegen(knoten[10], knoten[52], 1); // Knoten 11-53
        graph.kanteEinfuegen(knoten[11], knoten[50], 1); // Knoten 12-51
        graph.kanteEinfuegen(knoten[12], knoten[11], 1); // Knoten 13-12
        graph.kanteEinfuegen(knoten[13], knoten[49], 1); // Knoten 14-50
        graph.kanteEinfuegen(knoten[14], knoten[47], 1); // Knoten 15-48
        graph.kanteEinfuegen(knoten[15], knoten[45], 1); // Knoten 16-46
        graph.kanteEinfuegen(knoten[16], knoten[15], 1); // Knoten 17-16
        graph.kanteEinfuegen(knoten[17], knoten[16], 1); // Knoten 18-17
        graph.kanteEinfuegen(knoten[18], knoten[53], 1); // Knoten 19-54
        graph.kanteEinfuegen(knoten[19], knoten[18], 1); // Knoten 20-19
           
        graph.kanteEinfuegen(knoten[28], knoten[41], 1); // Knoten 29-42
        graph.kanteEinfuegen(knoten[29], knoten[28], 1); // Knoten 30-29
        graph.kanteEinfuegen(knoten[30], knoten[7], 1);  // Knoten 31-8

        graph.kanteEinfuegen(knoten[39], knoten[8], 1);  // Knoten 40-9
        graph.kanteEinfuegen(knoten[39], knoten[40], 1); // Knoten 40-41
        graph.kanteEinfuegen(knoten[40], knoten[29], 1); // Knoten 41-30
        graph.kanteEinfuegen(knoten[40], knoten[30], 1); // Knoten 41-31
        graph.kanteEinfuegen(knoten[41], knoten[42], 1); // Knoten 42-43
        graph.kanteEinfuegen(knoten[42], knoten[43], 1); // Knoten 43-44
        graph.kanteEinfuegen(knoten[43], knoten[44], 1); // Knoten 44-45
        graph.kanteEinfuegen(knoten[44], knoten[5], 1);  // Knoten 45-6
        graph.kanteEinfuegen(knoten[44], knoten[45], 1); // Knoten 45-46
        graph.kanteEinfuegen(knoten[45], knoten[46], 1); // Knoten 46-47
        graph.kanteEinfuegen(knoten[46], knoten[14], 1); // Knoten 47-15 
        graph.kanteEinfuegen(knoten[47], knoten[13], 1); // Knoten 48-14
        graph.kanteEinfuegen(knoten[47], knoten[48], 1); // Knoten 48-49
        graph.kanteEinfuegen(knoten[48], knoten[3], 1);  // Knoten 49-4
        graph.kanteEinfuegen(knoten[49], knoten[12], 1); // Knoten 50-13
        graph.kanteEinfuegen(knoten[50], knoten[10], 1); // Knoten 51-11
        graph.kanteEinfuegen(knoten[50], knoten[51], 1); // Knoten 51-52
        graph.kanteEinfuegen(knoten[51], knoten[9], 1);  // Knoten 52-10
        graph.kanteEinfuegen(knoten[52], knoten[19], 1); // Knoten 53-20
        graph.kanteEinfuegen(knoten[53], knoten[54], 1); // Knoten 54-55
        graph.kanteEinfuegen(knoten[54], knoten[17], 1); // Knoten 55-18

        // Kanten werden gemäß Vorlage verknüpft  Gewichtung 2  entgegen der Fahrtrichtung
        graph.kanteEinfuegen(knoten[0], knoten[1], 2);   // Knoten 1-2
        graph.kanteEinfuegen(knoten[1], knoten[2], 2);   // Knoten 2-3
        graph.kanteEinfuegen(knoten[2], knoten[3], 2);   // Knoten 3-4
        graph.kanteEinfuegen(knoten[3], knoten[48], 2);  // Knoten 4-49
        graph.kanteEinfuegen(knoten[4], knoten[5], 2);   // Knoten 5-6
        graph.kanteEinfuegen(knoten[5], knoten[44], 2);  // Knoten 6-45
        graph.kanteEinfuegen(knoten[6], knoten[7], 2);   // Knoten 7-8
        graph.kanteEinfuegen(knoten[7], knoten[30], 2);  // Knoten 8-31
        graph.kanteEinfuegen(knoten[8], knoten[39], 2);  // Knoten 9-40
        graph.kanteEinfuegen(knoten[9], knoten[51], 2);  // Knoten 10-52
        graph.kanteEinfuegen(knoten[10], knoten[50], 2); // Knoten 11-51
        graph.kanteEinfuegen(knoten[11], knoten[12], 2); // Knoten 12-13
        graph.kanteEinfuegen(knoten[12], knoten[49], 2); // Knoten 13-50
        graph.kanteEinfuegen(knoten[13], knoten[47], 2); // Knoten 14-48
        graph.kanteEinfuegen(knoten[14], knoten[46], 2); // Knoten 15-47
        graph.kanteEinfuegen(knoten[15], knoten[16], 2); // Knoten 16-17
        graph.kanteEinfuegen(knoten[16], knoten[17], 2); // Knoten 17-18
        graph.kanteEinfuegen(knoten[17], knoten[54], 2); // Knoten 18-55
        graph.kanteEinfuegen(knoten[18], knoten[19], 2); // Knoten 19-20
        graph.kanteEinfuegen(knoten[19], knoten[52], 2); // Knoten 20-53

        graph.kanteEinfuegen(knoten[28], knoten[29], 2); // Knoten 29-30
        graph.kanteEinfuegen(knoten[29], knoten[40], 2); // Knoten 30-41
        graph.kanteEinfuegen(knoten[30], knoten[40], 2); // Knoten 31-41

        graph.kanteEinfuegen(knoten[39], knoten[9], 2);  // Knoten 40-10
        graph.kanteEinfuegen(knoten[40], knoten[39], 2); // Knoten 41-40
        graph.kanteEinfuegen(knoten[41], knoten[28], 2); // Knoten 42-29
        graph.kanteEinfuegen(knoten[42], knoten[41], 2); // Knoten 43-42
        graph.kanteEinfuegen(knoten[43], knoten[6], 2);  // Knoten 44-7
        graph.kanteEinfuegen(knoten[43], knoten[42], 2); // Knoten 44-43
        graph.kanteEinfuegen(knoten[44], knoten[43], 2); // Knoten 45-44
        graph.kanteEinfuegen(knoten[45], knoten[44], 2); // Knoten 46-45
        graph.kanteEinfuegen(knoten[45], knoten[15], 2); // Knoten 46-16
        graph.kanteEinfuegen(knoten[46], knoten[45], 2); // Knoten 47-46
        graph.kanteEinfuegen(knoten[47], knoten[14], 2); // Knoten 48-15
        graph.kanteEinfuegen(knoten[48], knoten[4], 2);  // Knoten 49-5
        graph.kanteEinfuegen(knoten[48], knoten[47], 2); // Knoten 49-48
        graph.kanteEinfuegen(knoten[49], knoten[13], 2); // Knoten 50-14
        graph.kanteEinfuegen(knoten[50], knoten[11], 2); // Knoten 51-12
        graph.kanteEinfuegen(knoten[51], knoten[50], 2); // Knoten 52-51
        graph.kanteEinfuegen(knoten[51], knoten[0], 2);  // Knoten 52-1
        graph.kanteEinfuegen(knoten[52], knoten[10], 2); // Knoten 53-11
        graph.kanteEinfuegen(knoten[53], knoten[8], 2);  // Knoten 54-9
        graph.kanteEinfuegen(knoten[53], knoten[18], 2); // Knoten 54-19
        graph.kanteEinfuegen(knoten[54], knoten[53], 2); // Knoten 55-54
        
        //Kanten werden gemäß Vorlage verknüpft Mittlekreis mit jeweils Gewichtung
        //von 3 (beide Fahrtrichtungen)
        graph.kanteEinfuegen(knoten[52], knoten[20], 3); // Knoten 53-21
        graph.kanteEinfuegen(knoten[20], knoten[52], 3); // Knoten 21-53
        graph.kanteEinfuegen(knoten[20], knoten[21], 3); // Knoten 21-22
        graph.kanteEinfuegen(knoten[21], knoten[20], 3); // Knoten 22-21
        graph.kanteEinfuegen(knoten[21], knoten[55], 3); // Knoten 22-56
        graph.kanteEinfuegen(knoten[55], knoten[21], 3); // Knoten 56-22
        graph.kanteEinfuegen(knoten[55], knoten[22], 3); // Knoten 56-23
        graph.kanteEinfuegen(knoten[55], knoten[25], 3); // Knoten 56-26
        graph.kanteEinfuegen(knoten[55], knoten[26], 3); // Knoten 56-27
        graph.kanteEinfuegen(knoten[22], knoten[55], 3); // Knoten 23-56
        graph.kanteEinfuegen(knoten[22], knoten[23], 3); // Knoten 23-24
        graph.kanteEinfuegen(knoten[23], knoten[49], 3); // Knoten 24-50
        graph.kanteEinfuegen(knoten[23], knoten[22], 3); // Knoten 24-23
        graph.kanteEinfuegen(knoten[49], knoten[23], 3); // Knoten 50-24
        graph.kanteEinfuegen(knoten[25], knoten[55], 3); // Knoten 26-56
        graph.kanteEinfuegen(knoten[25], knoten[24], 3); // Knoten 26-25
        graph.kanteEinfuegen(knoten[24], knoten[46], 3); // Knoten 25-47
        graph.kanteEinfuegen(knoten[24], knoten[25], 3); // Knoten 25-26
        graph.kanteEinfuegen(knoten[46], knoten[24], 3); // Knoten 47-25
        graph.kanteEinfuegen(knoten[26], knoten[55], 3); // Knoten 27-56
        graph.kanteEinfuegen(knoten[26], knoten[27], 3); // Knoten 27-28
        graph.kanteEinfuegen(knoten[27], knoten[26], 3); // Knoten 28-27
        graph.kanteEinfuegen(knoten[27], knoten[54], 3); // Knoten 28-55
        graph.kanteEinfuegen(knoten[54], knoten[27], 3); // Knoten 55-28
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
        //DEBGGING
        Knoten[] knotenliste = hilfsliste.getListe();
        System.out.println("Knotenliste ausgeben: ");
        System.out.println("Nr." + 1 + " Name: " + knotenliste[0].getName() + " Gew.: " + knotenliste[0].getPfadgewicht() + " Kein Vorgänger");
        for (int i = 1; i < knotenliste.length; i++) {
            System.out.println("Nr." + (i + 1) + " Name: " + knotenliste[i].getName() + " Gew.: " + knotenliste[i].getPfadgewicht() + " Vorg.: " + knotenliste[i].getVorgaenger().getName());
        }
    }

    public void findeWeg(int startpunkt, int endpunkt) {
        //mit Dijkstra-Algorithmus kürzeste Strecke berechnen vom Startpunkt aus
        findeStrecke(knoten[startpunkt - 1]);
        //Liste der Knoten mit dem kürzesten Weg zum Startknoten
        Knoten[] knotenliste = hilfsliste.getListe();
        //Liste der Wegpunkte
        List<Knoten> way = new ArrayList<>();
        //Endpunkt hinzufügen
        for (Knoten lKnoten : knotenliste) {
            if (lKnoten.getName() == endpunkt) {
                //DEBUGGING: System.out.println("erster Knoten: " + lKnoten.getName());
                way.add(lKnoten);
            }
        }
        //immer Startknoten
        //System.out.println("Startknoten: " + knotenliste[0].getName());
        System.out.println("Startknoten: " + knoten[startpunkt - 1].getName());
        System.out.println("Endknoten: " + knoten[endpunkt - 1].getName());
        int vorgaengerNr = 0;
        //unendliche Schleife
        while (true) {
            way.add(way.get(vorgaengerNr).getVorgaenger()); //10
            //breche ab, wenn der Startknoten in die Liste eingetragen wird
            if (way.get(vorgaengerNr + 1).getName() == knotenliste[0].getName()) {
                break;
            }
            vorgaengerNr++;
        }
        
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
    }
}
