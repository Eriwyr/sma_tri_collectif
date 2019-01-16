
package model;

import org.w3c.dom.ls.LSInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
        double fp = calcFp(memory.get(0));
        double random = Math.random();
        if(random>fp){
            grid.take(x,y);
            currentObject = memory.get(0);
            return true;
        }
        return false;
    }

    public boolean dropObject(){
        double fd = calcFd();
        double random = Math.random();

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

        double fp = getNumberOf(this.memory, gridElement) / (double)this.memory.size();

        return fp;

    }

    private double calcFd(){
        double fd =0;

        fd= getNumberOf(grid.getNeighbourhood(x,y),this.currentObject);
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
        if(memory.size()==10){
            memory.remove(memory.size()-1);
        }
        memory.add(0,element);

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
            addMemoryElement(grid.get(x,y));

            if(!grid.get(x,y).equals(0)){
                takeObject();
            }
            else if(currentObject!=null){
                dropObject();
            }
        }

    }
}
