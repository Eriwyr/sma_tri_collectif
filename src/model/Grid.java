package model;

import javafx.geometry.Pos;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Grid extends Observable {

    private static Grid instance;

    private static Vector<Position> positionsAgents;

    private Lock lock = new ReentrantLock();

    private static Vector<Vector<AtomicInteger>> tab = new Vector<>(50);

    private List<Agent> agents ;


    private Grid() {
    }

    public static Vector<Position> getPositionsAgents() {
        return positionsAgents;
    }

    public static void init() {
        // init grid
        tab = new Vector<>(50);


        for (int i =0; i<50; i++) {
            tab.add(i, new Vector<>(50));
            for (int j = 0; j<50; j++) {
                tab.get(i).add(j, new AtomicInteger(0));
            }
        }


        getInstance().setChanged();
        getInstance().notifyObservers();

    }

    public void setAgents(List<Agent> agents) {
        this.agents = agents;
        positionsAgents = new Vector<Position>();
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

        if (x<50 && y<50) {


            Position newPosition = new Position(x, y);

            synchronized (tab) {
                for (Position position : positionsAgents) {
                    if (newPosition == position) {
                        return false;
                    }
                }
                if (tab.get(y).get(x) == new AtomicInteger(0)) {
                    positionsAgents.set(agent.getId(), newPosition);
                    return true;
                }
            }
        }
        return false;
    }

    public HashMap getNeighbourhood(int x, int y) {

        HashMap<Position, AtomicInteger> map = new HashMap<Position, AtomicInteger>();
        synchronized (tab) {
            map.put(new Position(y+1, x), tab.get(y+1).get(x));
            map.put(new Position(y, x+1), tab.get(y).get(x+1));
            map.put(new Position(y-1, x), tab.get(y-1).get(x));
            map.put(new Position(y, x-1), tab.get(y).get(x-1));

            return map;
        }
    }

    public boolean drop(AtomicInteger a, int x, int y) {
        synchronized (tab) {
            if (tab.get(y).get(y) == new AtomicInteger(0)) {
                tab.get(y).set(x, a);
            } else {
                return false;
            }
            getInstance().setChanged();
            getInstance().notifyObservers();
            return true;
        }
    }

    public boolean take(AtomicInteger a, int x, int y) {
        synchronized (tab) {
            if(a ==tab.get(y).get(y)) {
                tab.get(y).set(x, new AtomicInteger(0));
            } else {
                return false;
            }
        }
        getInstance().setChanged();
        getInstance().notifyObservers();
        return true;

    }


    public static void addNewObserver(Observer observer) {
        getInstance().addObserver(observer);
    }

    public AtomicInteger get(int x, int y) {
        System.out.println("x : "+x+" y : "+y);
        synchronized (tab) {
            return tab.get(y).get(x);
        }

    }

    public static Vector<Vector<AtomicInteger>> getTab() {
        return tab;
    }
}
