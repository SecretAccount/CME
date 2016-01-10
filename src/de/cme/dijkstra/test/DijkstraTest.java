package de.cme.dijkstra.test;

import de.cme.dijkstra.Dijkstra;

public class DijkstraTest {

    private Dijkstra dijkstra = new Dijkstra();
    
    public void testeGraph() {
        dijkstra.init();
        for (int i = 1; i < 45; i++) {
            for (int j = 2; j < 45; j++) {
                dijkstra.findeWeg(i, j);
                dijkstra.showList(); //Zeigt Fehler, wenn noch nicht alle Knoten und Kanten eingefügt sind
            }
        }
    }
}
