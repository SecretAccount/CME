package de.cme.dijkstra.test;

import de.cme.dijkstra.Dijkstra;

public class DijkstraTest {

    private Dijkstra dijkstra;

    public DijkstraTest() {
        dijkstra = new Dijkstra();
        dijkstra.init();
    }

    public void testeGraph() {
        
        for (int i = 1; i < 48; i++) {
            for (int j = 1; j < 48; j++) {
                //Keine gleichen Knoten vergleichen
                if(i != j && i != 6 && j != 6) {
                dijkstra = new Dijkstra();
                dijkstra.init();
                dijkstra.findeWeg(i, j);
//                dijkstra.showList(); //Zeigt Fehler, wenn noch nicht alle Knoten und Kanten im Quellcode eingefügt sind
                }
            }
        }
        
        /*
        //Teste Knoten 6
        int j = 6;
        for (int i = 1; i < 45; i++) {
                //Keine gleichen Knoten vergleichen
                if(i != j) {
                dijkstra = new Dijkstra();
                dijkstra.init();
                dijkstra.findeWeg(j, i);
//                dijkstra.showList(); //Zeigt Fehler, wenn noch nicht alle Knoten und Kanten im Quellcode eingefügt sind
                }
        }
        */
    }

    public void testeGraph(int von, int bis) {
        dijkstra.findeWeg(von, bis);
//        dijkstra.showList(); //Zeigt Fehler, wenn noch nicht alle Knoten und Kanten im Quellcode eingefügt sind
    }
}
