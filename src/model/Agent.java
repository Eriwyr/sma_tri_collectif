package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Agent implements Runnable{
    private Lock lock = new ReentrantLock();

    private int scope =2;
    private int gridHeight =50;
    private int gridWidth =50;
    private int id;
    private static Grid grid;
    private int x;
    private int y;
    private int kP =  2;
    private float kM = (float) 0.5;


    private AtomicInteger currentObject;
    private boolean stop;

    public Agent(int id, int x, int y) {
        grid = Grid.getInstance();

        this.id = id;
        this.x = x;
        this.y = y;
        this.currentObject = new AtomicInteger(0);
        this.stop=false;
    }

    private void takeObject(){

        Position positionToObserve;
        lock.lock();
        try {

            double bestPp = 0;
            double f;
            double pp;

            Map.Entry<Position, AtomicInteger> bestElement = null;
            HashMap<Position, AtomicInteger> neighbourhood = observeNeighbourhood(null);

            for (Map.Entry<Position, AtomicInteger> element : neighbourhood.entrySet()) {

                if (bestElement == null) bestElement = element;

                if (element.getValue().get() != 0) {

                    positionToObserve = new Position(element.getKey().getX(), element.getKey().getY());
                    f = calcF(element.getValue(), observeNeighbourhood(positionToObserve));
                    pp = calcPp(f);
                    if (pp > bestPp) {
                        bestPp = pp;
                        bestElement = element;
                    }
                }
            }

            HashMap<Position, AtomicInteger> possibilities = new HashMap<>();

            if(bestElement.getValue().get() != 0) {

                for (Map.Entry<Position, AtomicInteger> element : neighbourhood.entrySet()) {
                    if(element.getValue().get() == bestElement.getValue().get())

                        possibilities.put(element.getKey(), element.getValue());

                }

                Random rand = new Random();

                int r = rand.nextInt(possibilities.size());
                int count =0;

                for (Map.Entry<Position, AtomicInteger> element : possibilities.entrySet()) {
                    if (count == r) {
                        bestElement = element;

                        break;
                    } else count ++;
                }



                double random = Math.random();

                if (bestPp > random) {
                    if (grid.take(bestElement.getValue(), bestElement.getKey().getX(), bestElement.getKey().getY())) {
                        currentObject = bestElement.getValue();
                    }
                }
            }

        } finally {
            lock.unlock();
        }
    }

    private void dropObject(){
        lock.lock();
        try {
            Position newPos;
            newPos = getRandomDirection(this.x, this.y);

            HashMap map = observeNeighbourhood(newPos);

            double f = calcF(currentObject, map);
            double pd = calcPd(f);

            double random = Math.random();


            if (random < pd) {



                assert newPos != null;

                if (isInGrid(newPos)
                        && grid.drop(currentObject, newPos.getX(), getY())) {
                    currentObject = new AtomicInteger(0);

                }
            }
        } finally {
            lock.unlock();
        }

    }


    private double calcF(AtomicInteger gridElement, HashMap<Position,AtomicInteger> map){
        ArrayList<AtomicInteger> valuesList = new ArrayList<>(map.values());

        double f= getNumberOf(valuesList,gridElement) / (double)(valuesList.size());
        return f;
    }


    private double calcPp(double fp){
        return Math.pow(kP/(kP+fp),2);
    }

    private double calcPd(double fd){

        return Math.pow(fd/(kM+fd),2);
    }

    private int getNumberOf(ArrayList<AtomicInteger> listElement, AtomicInteger element){
        int number = 0;
        for (AtomicInteger currentElement:listElement) {
            if (currentElement.get() == element.get()){
                number++;
            }

        }
        return number;
    }

    private Position getRandomDirection(int x, int y) {
        int random = ThreadLocalRandom.current().nextInt(0, 4);

        switch (random) {
            case 0:

                return new Position(x + 1, y);
            case 1:
                return new Position(x , y+1);
            case 2:
                return new Position(x - 1, y);
            case 3:
                return new Position(x , y-1);
            default:
                return null;
        }
    }

    private boolean isInGrid(int x, int y) {
        return (y<gridHeight && y>=0 && x<gridWidth && x>=0);
    }

    private boolean isInGrid(Position position) {
        return (position.getY()<gridHeight && position.getY()>=0 && position.getX()<gridWidth && position.getX()>=0);
    }

    private void goToRandomDirection() {
        lock.lock();
        try {


            Position newPosition = getRandomDirection(x, y);
            assert newPosition != null;

            if (isInGrid(newPosition)
                    && grid.get(newPosition.getX(), newPosition.getY()).get() == 0
                    && !Grid.getPositionsAgents().contains(new Position(newPosition.getX(), newPosition.getY()))) {

                if (grid.moveTo(this, newPosition.getX(), newPosition.getY())) {
                    this.x = newPosition.getX();
                    this.y = newPosition.getY();
                }

            }
        } finally {
            lock.unlock();
        }
    }

    public HashMap<Position, AtomicInteger>  observeNeighbourhood(Position p) {
        if (p == null) p = new Position(this.x, this.y);


        Position position;
        HashMap<Position, AtomicInteger>  neighbourhood = new HashMap<>();

        for (int x=scope*-1; x<scope+1; x++) {
            for (int y=scope*-1; y<scope+1; y++) {

                position = new Position(p.getX()+x, p.getY()+y);

                if(isInGrid(position.getX(), position.getY()) && (x!=0 || y!=0)) {
                    neighbourhood.put(position, grid.get(position));
                }
            }
        }

        return neighbourhood;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getId() {
        return id;
    }

    @Override
    public void run() {
        while(!stop){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            goToRandomDirection();

            if(currentObject.get() == 0 ){
                takeObject();

            }
            else {

                dropObject();

            }
        }

    }
}
