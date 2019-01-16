import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Grid {

    private static Grid instance;

    private Lock lock = new ReentrantLock();

    private final Vector<Vector<Object>> tab = new Vector<Vector<Object>>(50);

    private Grid() {
        for (int i =0; i<50; i++) {
            tab.set(i, new Vector<>(50));
            for (int j = 0; j<50; j++) {
                tab.get(j).set(i, 0);
            }
        }
    }

    public static synchronized Grid getInstance() {
        if (instance == null) {
            instance = new Grid();
        }
        return instance;
    }

    public boolean moveTo(Agent agent, int x, int y) {

        synchronized (tab) {
            Object cell = tab.get(y).get(x);
            if (cell.getClass() != AtomicInteger.class || ((AtomicInteger) cell).get() != 0) {
            return false;
            } else {
                tab.get(y).set(x, agent);
                return true;
            }
        }

    }

    public ArrayList getNeighbourhood(int x, int y) {
        ArrayList<Object> list = new ArrayList<Object>();
        synchronized (tab) {
            list.add(tab.get(y+1).get(x));
            list.add(tab.get(y).get(x+1));
            list.add(tab.get(y-1).get(x));
            list.add(tab.get(y).get(x-1));
            return list;
        }
    }
}
