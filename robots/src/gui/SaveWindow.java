package gui;

import java.awt.*;
import java.io.Serializable;

public class SaveWindow implements Serializable {
    char name;
    Point point;
    Dimension dimension;

    private SaveWindow(){}

    SaveWindow(char name,int x, int y, int width, int height) {
        this.name=name;
        this.point = new Point(x,y);
        this.dimension=new Dimension(width,height);
    }
    SaveWindow(char name,Point point, Dimension dimension){
        this.name=name;
        this.point = point;
        this.dimension = dimension;
    }
    SaveWindow(char name,SaveWindow saveWindow){
        this.name=name;
        this.point=saveWindow.point;
        this.dimension=saveWindow.dimension;
    }
    SaveWindow(char name,String[] args){
        this.name=name;
        this.point=new Point(Integer.parseInt(args[0]),Integer.parseInt(args[1]));
        this.dimension=new Dimension(Integer.parseInt(args[2]),Integer.parseInt(args[3]));
    }
    SaveWindow(char name,int[] args){
        this.name=name;
        this.point=new Point(args[0],args[1]);
        this.dimension=new Dimension(args[2],args[3]);
    }

}
