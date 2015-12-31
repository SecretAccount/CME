package de.cme.dijkstra.test;

import de.cme.dijkstra.Knoten;
import de.cme.dijkstra.Graph;

public class GraphenTest {

    Knoten[] knoten = new Knoten[56];

    // Knoten werden erstellt
    public void testeGraphen() {
        //Graphen setzen
        Graph graph = new Graph(56); //56 Knoten/Waypoints

        //Knoten 1-56
        for (int i = 0; i < 56; i++) {
            knoten[i] = new Knoten(i + 1);
            graph.knotenEinfuegen(knoten[i]);
        }

        
    }
}
