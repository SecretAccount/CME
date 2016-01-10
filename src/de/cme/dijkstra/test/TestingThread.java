package de.cme.dijkstra.test;

/**
 *
 * @author Pascal
 */
public class TestingThread extends Thread {

    private DijkstraTest dijkstraTest = new DijkstraTest();
    private int startPoint;
    private int endPoint;
    boolean onlyOneTest = false;

    public TestingThread(int von, int bis) {
        onlyOneTest = true;
        startPoint = von;
        endPoint = bis;
    }

    public TestingThread() {
        onlyOneTest = false;
    }

    @Override
    public void run() {
        if (onlyOneTest) {
            dijkstraTest.testeGraph(startPoint, endPoint);
        } else {
            dijkstraTest.testeGraph();
        }
    }

}
