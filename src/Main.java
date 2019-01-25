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
        agents.add(new Agent(0, 30, 30));
        agents.add(new Agent(1, 10, 40));
        agents.add(new Agent(2, 13, 34));
        agents.add(new Agent(3, 17, 20));
        agents.add(new Agent(4, 22, 6));
        agents.add(new Agent(5, 25, 34));
        agents.add(new Agent(6, 28, 20));
        agents.add(new Agent(7, 32, 40));
        agents.add(new Agent(8, 36, 24));
        agents.add(new Agent(9, 11, 40));
        agents.add(new Agent(10, 23, 40));
        agents.add(new Agent(11, 20, 34));
        agents.add(new Agent(12, 40, 20));
        agents.add(new Agent(13, 44, 6));
        agents.add(new Agent(14, 49, 34));
        agents.add(new Agent(15, 37, 20));
        agents.add(new Agent(16, 45, 40));
        agents.add(new Agent(17, 1, 24));
        agents.add(new Agent(18, 8, 40));
        agents.add(new Agent(19, 47, 24));
        agents.add(new Agent(20, 25, 40));


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
