
package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Agent implements Runnable{

    private int id;
    private static Grid grid;
    private int x;
    private int y;
    private int kP;
    private int kM;
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

        double bestPp = 0;
        double tmpPp = 0;
        double tmpFp =0;

        Map.Entry<Position,AtomicInteger> tmpElement = null;
        HashMap<Position,AtomicInteger> neighbourhood = grid.getNeighbourhoodTake(x,y);

        int count = 0;
        for(Map.Entry<Position,AtomicInteger> element : neighbourhood.entrySet()) {

            if(element.getValue().get() != 0){

                tmpFp = calcFp(element.getValue(),neighbourhood );
                double pp = calcPp(tmpFp);


                if(tmpPp > bestPp){
                    bestPp  = tmpPp;
                    tmpElement  = element;
                }
            }

            double random = Math.random();

            if( this.memory.size() == 0 ){
                Position position = getRandomDirection(x, y);
                AtomicInteger chosen = grid.get(position.getX(), position.getY());

                if (chosen.get()!= 0 && grid.take(chosen, position.getX(), position.getY())) {
                    currentObject = chosen;
                    addMemoryElement(currentObject);
                }
            }
            else if(bestPp > random){

                if(grid.take(tmpElement.getValue(), tmpElement.getKey().getX(), tmpElement.getKey().getY())){
                    currentObject = tmpElement.getValue();
                    addMemoryElement(currentObject);
                }
            }
            count++;
        }
    }

    public boolean dropObject(){
        double fd = calcFd();
        double pd = calcPd(fd);
        System.out.println("fd "+fd);
        System.out.println("pd "+pd);

        double random = Math.random();
        Position newPos = null;

        if(random>pd){
            System.out.println("in fi ");
            newPos = getRandomDirection(x,y);
            grid.drop(currentObject , newPos.getX(),getY());
            currentObject = new AtomicInteger(0);
            return true;
        }

        return false;
    }



    public static Grid getGrid() {
        return grid;
    }

    public static void setGrid(Grid grid) {
        Agent.grid = grid;
    }

    private double calcFp(AtomicInteger gridElement, HashMap<Position,AtomicInteger> map){
        ArrayList<AtomicInteger> valuesList = new ArrayList<AtomicInteger>(map.values());

        double fp = getNumberOf(valuesList,gridElement) / (double)this.memory.size();

        return fp;

    }

    private double calcFd(){

        ArrayList<AtomicInteger> valuesList = new ArrayList<AtomicInteger>(grid.getNeighbourhood(x,y).values());
        System.out.println("valuesList "+valuesList.toString());
        System.out.println("getNumberOf(valuesList,this.currentObject) "+getNumberOf(valuesList,this.currentObject));
        System.out.println("(double)this.memory.size() "+(double)this.memory.size());

        double fd = getNumberOf(valuesList,this.currentObject)/(double)this.memory.size();
        
        return  fd;
    }

    private double calcPp(double fp){
        if(this.memory.size() == 0) {
            return Math.random();
        }
        return Math.pow(fp/(kP+fp),2);
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

    private void addMemoryElement(AtomicInteger element){
        if(memory.size()==sizeOfMemory){
            memory.remove(memory.size()-1);
        }
        memory.add(0,element);

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
        }
        return null;
    }

    private void goToRandomDirection(){

            int random = ThreadLocalRandom.current().nextInt(0, 4);

            switch (random){

                case 0:
                    // east
                    if( x<49
                        && grid.get(x+1, y).get() == 0
                        && !Grid.getPositionsAgents().contains(new Position(x+1, y))) {

                        if(grid.moveTo(this,x+1,y)){
                            this.x = this.x+1;
                        }

                    }
                    break;
                case 1:

                    // south
                    if( y < 49
                        && (grid.get(x, y+1)).get()==0
                        && !Grid.getPositionsAgents().contains(new Position(x, y+1)) ) {

                        //moveToSouth();
                        if(grid.moveTo(this,x,y+1)){
                            this.y = this.y+1;
                        }
                    }

                    break;
                case 2:
                    //west
                    if(x>0
                        && ((AtomicInteger)grid.get(x-1, y)).get()==0
                        && !Grid.getPositionsAgents().contains(new Position(x-1, y)) ) {

                        // moveToWest();
                        if(grid.moveTo(this,x-1,y)){
                            this.x = this.x-1;
                        }

                    }

                    break;
                case 3:
                    //north
                    if(y>0
                        && ((AtomicInteger)grid.get(x, y-1)).get()==0
                        && !Grid.getPositionsAgents().contains(new Position(x, y-1)) ) {

                        //moveToNorth();
                        if(grid.moveTo(this,x,y-1)){
                            this.y = this.y-1;
                        }
                    }
                    break;

        }

    }

    int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    int getY() {
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


        while(!stop){
            try {
                Thread.sleep(100);
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
