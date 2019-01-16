package model;

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


        Random random = new Random();
        boolean isPut = false;
        AtomicInteger element = new AtomicInteger(1);

        for (int c = 0; c <400; c++) {
            //int randomNum = rand.nextInt((max - min) + 1) + min;

            int x = random.nextInt((49)+1);
            int y =random.nextInt((49)+1);

            while(getInstance().get(x,y).get()!=0){
                x = random.nextInt((49)+1);
                y =random.nextInt((49)+1);
            }
            
            tab.get(y).set(x,element);

            if(c==200){
                element = new AtomicInteger(2);
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

    public static synchronized Grid getInstance() {
        if (instance == null) {
            instance = new Grid();
        }
        return instance;
    }

    public boolean moveTo(Agent agent, int x, int y) {
        if (x<49 && y<49 && x>0 && y>0) {
            
            Position newPosition = new Position(x, y);

            synchronized (tab) {
                for (Position position : positionsAgents) {
                    if (newPosition == position) {
                        return false;
                    }
                }

                if (tab.get(y).get(x).get() == 0) {
                    positionsAgents.set(agent.getId(), newPosition);

                    getInstance().setChanged();
                    getInstance().notifyObservers();
                    return true;
                }
            }
        }

        return false;
    }

    public HashMap getNeighbourhoodTake(int x, int y ) {
        HashMap<Position, AtomicInteger> map = new HashMap<Position, AtomicInteger>();
        synchronized (tab) {
            //north
            map.put(new Position(y-1, x), tab.get(y-1).get(x));
            // north - east
            map.put(new Position(y-1, x+1), tab.get(y-1).get(x+1));
            // east
            map.put(new Position(y, x+1), tab.get(y).get(x+1));
            // south - east
            map.put(new Position(y+1, x+1), tab.get(y+1).get(x+1));
            // south
            map.put(new Position(y+1, x), tab.get(y+1).get(x));
            // south - west
            map.put(new Position(y+1, x-1), tab.get(y+1).get(x-1));
            // west
            map.put(new Position(y, x-1), tab.get(y).get(x-1));
            //north - west
            map.put(new Position(y-1, x-1), tab.get(y-1).get(x-1));

            return map;
        }
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
        System.out.println("dropped");
        synchronized (tab) {
            if (tab.get(y).get(x).get() == 0) {
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
            if (tab.get(y).get(x).get() == a.get()) {

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
        synchronized (tab) {
            return tab.get(y).get(x);
        }

    }

    public static Vector<Vector<AtomicInteger>> getTab() {
        return tab;
    }
}
