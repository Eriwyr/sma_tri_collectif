
package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Agent implements Runnable{
    private Lock lock = new ReentrantLock();


    private int gridHeight =50;
    private int gridWidth =50;
    private int id;
    private static Grid grid;
    private int x;
    private int y;
    private int kP;
    private int kM;

    private int sierNeighbourhood = 2;

    private AtomicInteger currentObject;
    private ArrayList<AtomicInteger> memory;
    private int sizeOfMemory;
    private boolean stop;

    public Agent(int id, int x, int y, int sizeOfMemory, int kM, int kP) {
        grid = Grid.getInstance();

        this.id = id;
        this.x = x;
        this.y = y;
        this.currentObject = new AtomicInteger(0);
        this.sizeOfMemory = sizeOfMemory;
        this.memory = new ArrayList<>();
        this.stop=false;
        this.kM = kM;
        this.kP = kP;
    }

    private void takeObject(){
        lock.lock();
        try {

            double bestPp = 0;
            double f;
            double pp;

            Map.Entry<Position, AtomicInteger> bestElement = null;
            HashMap<Position, AtomicInteger> neighbourhood = grid.getCompleteNeighbourhood(x, y);

            for (Map.Entry<Position, AtomicInteger> element : neighbourhood.entrySet()) {
                
                if (element.getValue().get() != 0) {

                    f = calcF(element.getValue(), neighbourhood);
                    pp = calcPp(f);

                    if (pp > bestPp) bestPp = pp;

                    if (f > bestPp) {
                        bestPp = f;
                        bestElement = element;
                    }
                }

            }

            double random = Math.random();

            if (bestPp > random) {

                assert bestElement != null;
//                System.out.println("I have " + currentObject.get() + " and I a trying to get " + bestElement);

                if (grid.take(bestElement.getValue(), bestElement.getKey().getX(), bestElement.getKey().getY())) {
//                    System.out.println("score !");
                    currentObject = bestElement.getValue();
                } else {
//                    System.out.println("could not get");
                }
            }
//            System.out.println("===================================");
        } finally {
            lock.unlock();
        }
    }

    private void dropObject(){
        lock.lock();
        try {
//            System.out.println("=============== dropObject() =============== ");

            double f = calcF(currentObject, grid.getCompleteNeighbourhood(x, y));

            double pd = calcPd(f);

            double random = Math.random();
            Position newPos;

            if (random > pd) {
                newPos = getRandomDirection(x, y);

//                System.out.println("I am at " + x + " " + y + " and I am tring to drop " + currentObject + "at " + newPos);
                assert newPos != null;

                if (isOnEdge(newPos.getX(), newPos.getY())
                        && grid.drop(currentObject, newPos.getX(), getY())) {
//                    System.out.println("I did it !");
                    currentObject = new AtomicInteger(0);

                } else {
//                    System.out.println("could not drop");
                }

            }
        } finally {
            lock.unlock();
        }

    }


    private double calcF(AtomicInteger gridElement, HashMap<Position,AtomicInteger> map){
        ArrayList<AtomicInteger> valuesList = new ArrayList<>(map.values());

        return getNumberOf(valuesList,gridElement) / (double)(valuesList.size()-getNumberOf(valuesList, new AtomicInteger(0)));
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

    private boolean isOnEdge(int x, int y) {
        return (y<gridHeight-1 && y>0 && x<gridWidth-1 && x>0);
    }
    private void goToRandomDirection() {
        lock.lock();
        try {


//            System.out.println("================ goToRandomDirection() ================");
            Position newPosition = getRandomDirection(x, y);
            assert newPosition != null;

//            System.out.println("I am at " + x + " " + y + ", trying to get to " + newPosition + ", where ther is " + grid.get(newPosition.getX(), newPosition.getY()));

            if (isOnEdge(x, y)
                    && grid.get(newPosition.getX(), newPosition.getY()).get() == 0
                    && !Grid.getPositionsAgents().contains(new Position(newPosition.getX(), newPosition.getY()))) {

                if (grid.moveTo(this, newPosition.getX(), newPosition.getY())) {
                    this.x = newPosition.getX();
                    this.y = newPosition.getY();
                }

            }
//            System.out.println("================================================");
        } finally {
            lock.unlock();
        }
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
                Thread.sleep(80);
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
