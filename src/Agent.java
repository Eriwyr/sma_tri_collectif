import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Agent {

    private static Grid grid;
    private int x;
    private int y;
    private AtomicInteger currentObject;
    private ArrayList<AtomicInteger> memory;
    private int sizeOfMemory;

    public Agent(int x, int y, AtomicInteger currentObject, int sizeOfMemory) {
        this.x = x;
        this.y = y;
        this.currentObject = currentObject;
        this.sizeOfMemory = sizeOfMemory;
        this.memory = new ArrayList<>();
    }


    public boolean takeObject(){
        double fp = calcFp(memory.get(0));
        double random = Math.random();


        return random > fp;
    }

    public boolean dropObject(){
        double fd = calcFd();
        double random = Math.random();

        return random > fd;
    }

    public void moveToEast(){
        grid.moveToEast();

    }

    public void moveToWest(){
        grid.moveToWest();
    }

    public void moveToNorth(){
        grid.moveToNorth();
    }

    public void moveToSouth(){
        grid.moveToSouth();
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
}
