
package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Agent implements Runnable{

    private int id;
    private static Grid grid;
    private int x;
    private int y;
    private AtomicInteger currentObject;
    private ArrayList<AtomicInteger> memory;
    private int sizeOfMemory;
    private boolean stop;

    public Agent(int id, int x, int y, int sizeOfMemory) {
        grid = Grid.getInstance();

        this.id = id;
        this.x = x;
        this.y = y;
        this.currentObject = new AtomicInteger(0);
        this.sizeOfMemory = sizeOfMemory;
        this.memory = new ArrayList<>();
        this.stop=false;
    }


    public boolean takeObject(){
        double random = Math.random();
        double bestFp = 0;
        double tmpFp = 0;
        Map.Entry<Position,AtomicInteger> tmpElement = null;
        HashMap<Position,AtomicInteger> neighbourhood = grid.getNeighbourhoodTake(x,y);

        for(Map.Entry<Position,AtomicInteger> element : neighbourhood.entrySet()) {
            if(!element.getValue().equals(new AtomicInteger(0))){
                tmpFp = calcFp(element.getValue());
                if(tmpFp > bestFp){
                    bestFp  = tmpFp;
                    tmpElement  = element;
                }
            }
            if(bestFp > random){
                if(grid.take(tmpElement.getValue(), tmpElement.getKey().getX(), tmpElement.getKey().getY())){
                    currentObject = tmpElement.getValue();
                }
            }
        }
    }

    public boolean dropObject(){

        double random = Math.random();
        double fd = 0;

        fd = calcFd();

        if(fd>random){

        }



        if(random>fd){
            grid.drop(this.currentObject, x, y);
            memory.remove(0);
            return true;
        }
        return false;
    }

    private void moveToEast(){

        if(grid.moveTo(this,x+1,y)){
            this.x = this.x+1;
        }
    }

    private void moveToWest(){

        if(grid.moveTo(this,x-1,y)){
            this.x = this.x-1;
        }
    }

    private void moveToNorth(){

        if(grid.moveTo(this,x,y-1)){
            this.y = this.y-1;
        }
    }

    private void moveToSouth(){

        if(grid.moveTo(this,x,y+1)){
            this.y = this.y+1;
        };
    }

    public static Grid getGrid() {
        return grid;
    }

    public static void setGrid(Grid grid) {
        Agent.grid = grid;
    }

    private double calcFp(AtomicInteger gridElement){
        ArrayList<AtomicInteger> valuesList = new ArrayList<AtomicInteger>(grid.getNeighbourhood(x,y).values());

        double fp = getNumberOf(valuesList,gridElement) / (double)this.memory.size();
        return fp;

    }

    private double calcFd(){
        ArrayList<AtomicInteger> valuesList = new ArrayList<AtomicInteger>(grid.getNeighbourhood(x,y).values());

        double fd= getNumberOf(valuesList,this.currentObject)/(double)this.memory.size();
        return  fd;
    }

    private int getNumberOf(ArrayList<AtomicInteger> listElement, AtomicInteger element){
        int number = 0;
        for (AtomicInteger currentElement:listElement) {
            if (currentElement.equals(element)){
                number++;
            }

        }
        return number;
    }

    private void addMemoryElement(AtomicInteger element){
        if(memory.size()==sizeOfMemory){
            memory.remove(memory.size()-1);
        }
        memory.add(0,element);

    }

    private Position getRandomDirection(int x, int y) {
        int random = ThreadLocalRandom.current().nextInt(0, 3);

        switch (random) {
            case 0:
                return new Position(x + 1, y);
            case 1:
                return new Position(x + 1, y);
            case 2:
                return new Position(x + 1, y);
            case 3:
                return new Position(x + 1, y);
        }
        return null;
    }

    private void goToRandomDirection(){
        boolean hasChosenDirection = false;

        while(!hasChosenDirection) {
            int random = ThreadLocalRandom.current().nextInt(0, 3);

            switch (random){
                case 0:
                    if(x<50
                        && ((AtomicInteger)grid.get(x+1, y)).get()==0
                        && !Grid.getPositionsAgents().contains(new Position(x+1, y)) ) {
                        moveToEast();
                        hasChosenDirection = true;
                    }
                    break;
                case 1:
                    if( y< 50
                        && ((AtomicInteger)grid.get(x, y+1)).get()==0
                        && !Grid.getPositionsAgents().contains(new Position(x, y+1)) ) {
                        moveToSouth();
                        hasChosenDirection = true;
                    }

                    break;
                case 2:
                    if(x>0
                        && ((AtomicInteger)grid.get(x-1, y)).get()==0
                        && !Grid.getPositionsAgents().contains(new Position(x-1, y)) ) {
                        moveToWest();
                        hasChosenDirection = true;
                    }

                    break;
                case 3:
                    if(y<0
                        && ((AtomicInteger)grid.get(x, y-1)).get()==0
                        && !Grid.getPositionsAgents().contains(new Position(x, y-1)) ) {
                        moveToNorth();
                        hasChosenDirection = true;
                    }
                    break;
            }
        }

    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public AtomicInteger getCurrentObject() {
        return currentObject;
    }

    public void setCurrentObject(AtomicInteger currentObject) {
        this.currentObject = currentObject;
    }

    public ArrayList<AtomicInteger> getMemory() {
        return memory;
    }

    public void setMemory(ArrayList<AtomicInteger> memory) {
        this.memory = memory;
    }

    public int getId() {
        return id;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        while(!stop){
            goToRandomDirection();
            if(!grid.get(x,y).equals(0)){
                takeObject();
            }
            else if(currentObject!=null){
                dropObject();
            }
        }

    }
}
