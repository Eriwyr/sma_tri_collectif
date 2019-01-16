package controller;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import model.Grid;
import view.GridView;

public class GridController {

    @FXML
    private Canvas gridCanvas;

    private GridView gridView;

    @FXML
    void initialize() {
        this.gridView = new GridView(gridCanvas);
        Grid.addNewObserver(gridView);
        Grid.init();
    }
}
