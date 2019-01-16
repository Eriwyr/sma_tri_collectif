package view;

import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Cell;
import model.Grid;
import model.Position;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class GridView implements Observer {

    private int slowDown = 0;
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
        //if(slowDown%100 == 0) {
            //GridCase[][] grid = Grid.getGrid();

            gc.clearRect(0, 0, 800,800);


            Vector<Vector<AtomicInteger>> cells = Grid.getTab();
            Cell[][] cellViews = new Cell[50][50];


            for (int i = 0; i< cells.size(); i++) {
                Vector row = cells.get(i);
                for (int j = 0; j<row.size(); j++  ) {
                    cellViews[i][j] = new Cell(i , j , ((AtomicInteger)row.get(j)).get());
                }
            }

            List<Position> positionAgents = Grid.getPositionsAgents();

            for (Position positionAgent : positionAgents) {
                cellViews[positionAgent.getY()][positionAgent.getX()] = new Cell(positionAgent.getX() , positionAgent.getX() , 3);
            }

           /* StringBuilder stringBuilder = new StringBuilder();

            for (int a = 0; a< cells.size(); a++) {

                for (int b = 0; b<cells.size(); b++  ) {

                    stringBuilder.append( cellViews[b][a].toString())
                            .append(" ");

                }
                stringBuilder.append("\n");
            }
            System.out.println(stringBuilder.toString());*/

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
                    else if(cellViews[i][j].getContent() == 3)
                    {
                        gc.setFill(Color.BLACK);
                        gc.fillOval(caseX+(caseWidth/4), caseY+(caseHeight/4), caseWidth/2, caseHeight/2);
                    }

                }
            }
        /*}
        slowDown = (slowDown+1)%100;*/
    }

    }
