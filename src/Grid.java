public class Grid {

    private static Grid instance;

    private Grid(){}

    public static synchronized Grid getInstance(){
        if(instance == null){
            instance = new Grid();
        }
        return instance;
    }
}
