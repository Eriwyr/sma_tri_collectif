package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Cell;
import model.Grid;

import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class GridView implements Observer {
    private Canvas canvas;
    private GraphicsContext gc;
    public GridView(Canvas canvas)
    {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();

        gc.beginPath();
    }

    @Override
    public void update(Observable o, Object arg) {
        //GridCase[][] grid = Grid.getGrid();
        Vector<Vector<AtomicInteger>> cells = Grid.getTab();
        Cell[][] cellViews = new Cell[50][50];

        for (int i = 0; i< cells.size(); i++) {
            Vector row = cells.get(i);
            for (int j = 0; j<row.size(); j++  ) {

                cellViews[j][i] = new Cell(i , j , ((AtomicInteger)row.get(j)).get());
            }
        }

        double caseWidth = 800.0/cellViews.length;

        for(int i=0; i<cellViews.length; i++)
        {
            double caseHeight = 800.0/cellViews[i].length;
            for(int j=0; j<cellViews[i].length; j++)
            {
                double caseX = i*caseWidth;
                double caseY = j*caseHeight;
                gc.strokeLine(caseX, caseY, caseX+caseWidth, caseY);
                gc.strokeLine(caseX+caseWidth, caseY, caseX+caseWidth, caseY+caseHeight);
                gc.strokeLine(caseX+caseWidth, caseY+caseHeight, caseX, caseY+caseHeight);
                gc.strokeLine(caseX, caseY+caseHeight, caseX, caseY);

                if(cellViews[i][j].getContent() == 1)
                {
                    gc.setFill(Color.BLUE);
                    gc.fillOval(caseX+(caseWidth/4), caseY+(caseHeight/4), caseWidth/2, caseHeight/2);
                }
                else if(cellViews[i][j].getContent() == 2)
                {
                    gc.setFill(Color.RED);
                    gc.fillOval(caseX+(caseWidth/4), caseY+(caseHeight/4), caseWidth/2, caseHeight/2);
                }

            }
        }
    }
}
