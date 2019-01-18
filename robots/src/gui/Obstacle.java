package gui;

import java.awt.*;
import java.awt.geom.Line2D;
import java.io.Serializable;

public class Obstacle extends Rectangle implements Serializable {

    private Point leftUp;
    private Point rightUp;
    private Point leftDown;
    private Point rightDown;

    private Point anotherLeftUp;
    private Point anotherLeftDown;
    private Point anotherRightUp;
    private Point anotherRightDown;
    private Obstacle(){}
    public Obstacle(Point p) {
        super.width=100;
        super.height=50;

        leftUp = new Point(p.x-width/2,p.y-height/2);
        rightUp = new Point(p.x+width/2, p.y-height/2);
        leftDown = new Point(p.x-width/2, p.y+height/2);
        rightDown = new Point(p.x+width/2,p.y+height/2);

        super.x=leftUp.x;
        super.y=leftUp.y;


        anotherLeftUp = new Point(this.leftUp.x-2, this.leftUp.y-2);//точки графа(использовал немного отступающие в поле точки от вершины)
        anotherRightUp = new Point(this.rightUp.x+2, this.rightUp.y-2);
        anotherLeftDown = new Point(this.leftDown.x-2, this.leftDown.y+2);
        anotherRightDown = new Point(this.rightDown.x+2, this.rightDown.y+2);
    }



    boolean intersect(Line2D line){//проверка на пересечение
        //Line2D line2D = new Line2D.Double(line.getStartX(),line.getStartY(),line.getEndX(),line.getEndY());
        return this.intersectsLine(line);
        //return this.intersectsLine(line.getStartX(),line.getStartY(),line.getEndX(),line.getEndY());
    }

    public Point getAnother(Point p){//получение точки графа по вершине фигуры
        if(p.equals(leftDown))return getAnotherLeftDown();
        else if(p.equals(leftUp))
            return getAnotherLeftUp();
        else if(p.equals(rightDown))
            return getAnotherRightDown();
        else return getAnotherRightUp();
    }

    public Point getAnotherLeftUp(){return anotherLeftUp;}

    public Point getAnotherLeftDown(){return anotherLeftDown;}

    public Point getAnotherRightUp(){return anotherRightUp;}

    public Point getAnotherRightDown(){return anotherRightDown;}


    public Point getLeftUp() {
        return this.leftUp;
    }

    public Point getRightUp() {
        return this.rightUp;
    }

    public Point getLeftDown() {
        return this.leftDown;
    }

    public Point getRightDown() {
        return this.rightDown;
    }
}
