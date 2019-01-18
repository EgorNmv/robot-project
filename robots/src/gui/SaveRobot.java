package gui;

import java.awt.*;
import java.io.Serializable;

public class SaveRobot implements Serializable {
    double x;
    double y;
    Point aim;
    double orientation;

    private SaveRobot(){}

    public SaveRobot(int x, int y, Point aim, double orientation){
        this.x = x;
        this.y = y;
        this.aim = aim;
        this.orientation = orientation;
    }

    public SaveRobot(GameWindow gameWindow){
        this.x = gameWindow.getRobotX();
        this.y = gameWindow.getRobotY();
        this.aim = gameWindow.getTargetPosition();
        this.orientation = gameWindow.getDirection();
    }

    public SaveRobot(RobotMove robot){
        this.x = robot.m_robotPositionX;
        this.y=robot.m_robotPositionY;
        this.aim=new Point(robot.m_targetPositionX,robot.m_targetPositionY);
        this.orientation=robot.m_robotDirection;
    }

}
