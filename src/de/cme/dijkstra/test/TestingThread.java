package de.cme.dijkstra.test;

/**
 *
 * @author Pascal
 */
public class TestingThread extends Thread {

    private DijkstraTest dijkstraTest = new DijkstraTest();
    private int startPoint;
    private int endPoint;
    private int removeNodeFrom;
    private int removeNodeTo;
    private boolean onlyOneTest = false;

    public TestingThread(int von, int bis) {
        onlyOneTest = true;
        startPoint = von;
        endPoint = bis;
    }

    //Konstruktor, um zu entscheiden, ob nur ein Weg oder alle getestet werden sollen
    public TestingThread(boolean onlyOneTest) {
        this.onlyOneTest = onlyOneTest;
    }
    
    public TestingThread() {
        onlyOneTest = false;
    }
    
    public void setStartPoint(int startPoint) {
        this.startPoint = startPoint;
    }

    public void setEndPoint(int endPoint) {
        this.endPoint = endPoint;
    }
    
    public void setRemoveNodeFrom(int removeNodeFrom) {
        this.removeNodeFrom = removeNodeFrom;
    }

    public void setRemoveNodeTo(int removeNodeTo) {
        this.removeNodeTo = removeNodeTo;
    }
    
    @Override
    public void run() {
        if (onlyOneTest) {
            dijkstraTest.entferneKanten(removeNodeFrom, removeNodeTo);
            dijkstraTest.testeGraph(startPoint, endPoint);
        } else {
            dijkstraTest.testeGraph();
        }
    }

}
