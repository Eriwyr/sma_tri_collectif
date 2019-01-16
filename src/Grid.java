import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Grid {

    private static Grid instance;

    private Vector<Position> positionsAgents;

    private Lock lock = new ReentrantLock();

    private final Vector<Vector<AtomicInteger>> tab = new Vector<Vector<AtomicInteger>>(50);

    private Grid(List<Agent> agents) {
        // init grid
        for (int i =0; i<50; i++) {
            tab.set(i, new Vector<>(50));
            for (int j = 0; j<50; j++) {
                tab.get(j).set(i, new AtomicInteger(0));
            }
        }

        //init positions
        for(Agent agent :agents) {
            positionsAgents.add(agent.getId(), new Position(agent.getX(), agent.getY()));
        }
    }

    private void randomInit() {
        Random random = new Random();
        List xA = new ArrayList();

        for (int xa = 0; xa<20; xa ++){
            xA.add(ThreadLocalRandom.current().nextInt(0, 50));
        }
    }

    public static synchronized Grid getInstance() {
        if (instance == null) {
            instance = new Grid();
        }
        return instance;
    }

    public boolean moveTo(Agent agent, int x, int y) {

        Position newPosition = new Position(x, y);
        synchronized (tab) {
            for (Position position : positionsAgents) {
                if (newPosition == position) {
                    return false;
                }
            }

            positionsAgents.set(agent.getId(), newPosition);
            return true;
        }

    }

    public ArrayList getNeighbourhood(int x, int y) {
        ArrayList<AtomicInteger> list = new ArrayList<AtomicInteger>();
        synchronized (tab) {
            list.add(tab.get(y+1).get(x));
            list.add(tab.get(y).get(x+1));
            list.add(tab.get(y-1).get(x));
            list.add(tab.get(y).get(x-1));
            return list;
        }
    }

    public void drop(AtomicInteger a, int x, int y) {
        synchronized (tab) {
                tab.get(y).set(x, a);
            }
        }

    public void take(int x, int y) {
        synchronized (tab) {
            tab.get(y).set(x, new AtomicInteger(0));
        }
    }
}
