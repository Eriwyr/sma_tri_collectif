import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Agent;
import model.Grid;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Grid grid = Grid.getInstance();

        List<Agent> agents = new ArrayList<Agent>();
        agents.add(new Agent(0, 1, 1, 10, 1, 1));
        /*agents.add(new Agent(1, 35, 40, 10, 1, 1));
        agents.add(new Agent(2, 20, 34, 10, 1, 1));
        agents.add(new Agent(3, 48, 20, 10, 1, 1));*/


        grid.setAgents(agents);


        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();




        for (Agent agent : agents) {

            new Thread(agent).start();
        }
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();


    }


    public static void main(String[] args) {
        launch(args);
    }
}
