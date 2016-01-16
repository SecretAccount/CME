package de.cme.dijkstra;

public class Graph {
    private Knoten[] knotenliste;
    private int[][] adjazenzmatrix; //erste Index: Spalte, 2. Index: Zeile adjazenzmatrix[2]<--2. Spalte[1]<-- 1. Zeile
    private int maxAnzahl;
    private int anzahl;

    public Graph(int groesse) {
        maxAnzahl = groesse; // 56 Knoten
        anzahl = 0;
        knotenliste = new Knoten[groesse];
        adjazenzmatrix = new int [groesse][groesse]; // 56 x 56 Array
        for (int[] adjazenzmatrix1 : adjazenzmatrix) {
            for (int j = 0; j < adjazenzmatrix.length; j++) {
                adjazenzmatrix1[j] = 0;
            }
        }
    }
    
    public boolean knotenEinfuegen(Knoten k) {
        
        if(anzahl < maxAnzahl) {
            knotenliste[anzahl] = k;
            anzahl++;
            return true;
        }
        return false;
    }
    
    public boolean kanteEinfuegen(Knoten von, Knoten bis, int gewicht) {
        int i = knotenIndexSuchen(von);
        int j = knotenIndexSuchen(bis);
        if(i != -1 && j != -1) { //gerichtete Kante
            adjazenzmatrix[i][j] = gewicht;
            return true;
        }
        return false;
    }
    
    public boolean kanteEntfernen(Knoten von, Knoten bis) {
        int i = knotenIndexSuchen(von);
        int j = knotenIndexSuchen(bis);
        if(i != -1 && j != -1) { //gerichtete Kante
            adjazenzmatrix[i][j] = 1000; //hohe Gewichtung, damit Kante nicht mehr genutzt wird (entfernt)
            return true;
        }
        return false;
    }
    
    private int knotenIndexSuchen(Knoten k) {
        int index = -1;
        int i = 0;
        while(index < 0 && i < knotenliste.length) {
            if(knotenliste[i].equals(k)) {
                index = i;
            }
            i++;
        }
        return index;
    }
    
    public boolean kanteEntfernen(int vonKnotenNummer, int bisKnotenNummer) {
        int i = knotenIndexSuchen(vonKnotenNummer);
        int j = knotenIndexSuchen(bisKnotenNummer);
        if(i != -1 && j != -1) { //gerichtete Kante
            adjazenzmatrix[i][j] = 1000; //hohe Gewichtung, damit Kante nicht mehr genutzt wird (entfernt)
            return true;
        }
        return false;
    }
    
    //Methode, um Knotenname zu übergeben und Index zu suchen
    private int knotenIndexSuchen(int kNr) {
        int index = -1;
        int i = 0;
        while(index < 0 && i < knotenliste.length) {
            if(knotenliste[i].getName() == kNr) {
                index = i;
            }
            i++;
        }
        return index;
    }
    
    public Knoten[] getNachfolger(Knoten k) {
        Knoten[] hilf = new Knoten[0];
        int i = knotenIndexSuchen(k);
        for(int j = 0; j < maxAnzahl; j++) {
            if(adjazenzmatrix[i][j] != 0) {
                Knoten[] tmp = new Knoten[hilf.length + 1];
                for(int v = 0; v < hilf.length; v++) {
                    tmp[v] = hilf[v];
                }
                tmp[hilf.length] = knotenliste[j];
                hilf = tmp;
            }
        }
        return hilf;
    }
    
    public int getKantenGewicht(Knoten von, Knoten bis) {
        int i = knotenIndexSuchen(von);
        int j = knotenIndexSuchen(bis);
        return adjazenzmatrix[i][j];
    }

}
