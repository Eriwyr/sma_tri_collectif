package model;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Grid extends Observable {

    private static Grid instance;

    private int gridHeight =50;
    private int gridWidth =50;

    private static Vector<Position> positionsAgents;

    private Lock lock = new ReentrantLock();

    private static Vector<Vector<AtomicInteger>> tab = new Vector<>(50);

    private static List<Agent> agents ;


    private Grid() {
    }

    public static List<Agent> getAgents() {
        return agents;
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

        for (int z = 0; z<10; z++) {
            tab.get(0).set(z,new AtomicInteger(1));
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
//                    System.out.println("finally moving to "+newPosition);
                    return true;
                }
            }
        }

        return false;
    }

    HashMap<Position,AtomicInteger> getCompleteNeighbourhood(int x, int y) {
        HashMap<Position, AtomicInteger> map = new HashMap<Position, AtomicInteger>();
        synchronized (tab) {
            if(x<gridWidth-1) {
                // east
                map.put(new Position(x+1, y), tab.get(y).get(x+1));
                // north - east
                if(y>0) map.put(new Position(x+1, y-1), tab.get(y-1).get(x+1));

                // south - east
                if ( y<gridHeight-1) map.put(new Position(x+1, y+1), tab.get(y+1).get(x+1));

            }
            //north
            if(y>0)
                map.put(new Position(x, y-1), tab.get(y-1).get(x));


            // south
            if(y<gridWidth)
                map.put(new Position( x,y+1), tab.get(y+1).get(x));


            // west
            if(x>0) {
                map.put(new Position(x-1, y), tab.get(y).get(x-1));

                // south - west
                if (y<gridHeight-1) map.put(new Position(x-1, y+1), tab.get(y+1).get(x-1));

                //north - west
                if(y>0) map.put(new Position(x-1, y-1), tab.get(y-1).get(x-1));

            }



            return map;
        }
    }


    boolean drop(AtomicInteger a, int x, int y) {
        synchronized (tab) {

            if (tab.get(y).get(x).get() == 0) {
                tab.get(y).set(x, a);
            } else {
                return false;
            }
        }
        getInstance().setChanged();
        getInstance().notifyObservers();
        return true;
    }

    boolean take(AtomicInteger a, int x, int y) {
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

    AtomicInteger get(int x, int y) {
        synchronized (tab) {
            return tab.get(y).get(x);
        }

    }

    public static Vector<Vector<AtomicInteger>> getTab() {
        return tab;
    }
}
