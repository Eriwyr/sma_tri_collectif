package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Grid;
import model.GridCase;
import model.ObjectType;

import java.util.Observable;
import java.util.Observer;
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
        AtomicInteger cell = Grid.get
        double caseWidth = 800.0/grid.length;

        for(int i=0; i<grid.length; i++)
        {
            double caseHeight = 800.0/grid[i].length;
            for(int j=0; j<grid[i].length; j++)
            {
                double caseX = i*caseWidth;
                double caseY = j*caseHeight;
                gc.strokeLine(caseX, caseY, caseX+caseWidth, caseY);
                gc.strokeLine(caseX+caseWidth, caseY, caseX+caseWidth, caseY+caseHeight);
                gc.strokeLine(caseX+caseWidth, caseY+caseHeight, caseX, caseY+caseHeight);
                gc.strokeLine(caseX, caseY+caseHeight, caseX, caseY);

                if(grid[i][j].getObjectType() == ObjectType.OBJECTA)
                {
                    gc.setFill(Color.BLUE);
                    gc.fillOval(caseX+(caseWidth/4), caseY+(caseHeight/4), caseWidth/2, caseHeight/2);
                }
                else if(grid[i][j].getObjectType() == ObjectType.OBJECTB)
                {
                    gc.setFill(Color.RED);
                    gc.fillOval(caseX+(caseWidth/4), caseY+(caseHeight/4), caseWidth/2, caseHeight/2);
                }

            }
        }
    }
}
