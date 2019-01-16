import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Agent {

    private static Grid grid;
    private int x;
    private int y;
    private AtomicInteger currentObject;
    private List<AtomicInteger> memory;
    private int sizeOfMemory;

    public Agent(int x, int y, AtomicInteger currentObject, int sizeOfMemory) {
        this.x = x;
        this.y = y;
        this.currentObject = currentObject;
        this.sizeOfMemory = sizeOfMemory;
        this.memory = new ArrayList<>();
    }


    public void takeObject(){

    }

    public void dropObject(){

    }
    
    public void moveToEast(){

    }

    public void moveToWest(){

    }

    public void moveToNorth(){

    }

    public void moveToSouth(){

    }

    public static Grid getGrid() {
        return grid;
    }

    public static void setGrid(Grid grid) {
        Agent.grid = grid;
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

    public List<AtomicInteger> getMemory() {
        return memory;
    }

    public void setMemory(List<AtomicInteger> memory) {
        this.memory = memory;
    }
}
