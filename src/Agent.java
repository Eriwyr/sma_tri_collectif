import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Agent implements Runnable{

    private static Grid grid;
    private int x;
    private int y;
    private AtomicInteger currentObject;
    private ArrayList<AtomicInteger> memory;
    private int sizeOfMemory;
    private boolean stop;

    public Agent(int x, int y, AtomicInteger currentObject, int sizeOfMemory) {
        this.x = x;
        this.y = y;
        this.currentObject = currentObject;
        this.sizeOfMemory = sizeOfMemory;
        this.memory = new ArrayList<>();
        this.stop=false;
    }


    public boolean takeObject(){
        double fp = calcFp(memory.get(0));
        double random = Math.random();
        if(random>fp){
            grid.take(x,y);
            return true;
        }
        return false;
    }

    public boolean dropObject(){
        double fd = calcFd();
        double random = Math.random();

        if(random>fd){
            grid.drop(x,y,this.currentObject);
        }
        return false;
    }

    public void moveToEast(){

        if(grid.moveTo(this,x+1,y)){
            this.x = this.x+1;
        };
    }

    public void moveToWest(){

        if(grid.moveTo(this,x-1,y)){
            this.x = this.x-1;
        };
    }

    public void moveToNorth(){

        if(grid.moveTo(this,x,y-1)){
            this.y = this.y-1;
        };
    }

    public void moveToSouth(){

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

        fd= getNumberOf(grid.getNeighbourhood,this.currentObject);
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

    @Override
    public void run() {

        while(!stop){
            
        }

    }
}
