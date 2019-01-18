package gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.*;

public class GameWindow extends JInternalFrame
{
    private int c;
    private final GameVisualizer m_visualizer;
    GameWindow()
    {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        setJMenuBar(generateMenuBar());
        getContentPane().add(panel);
        pack();
    }

    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(generateRobot());
        return menuBar;
    }

    private JMenu generateRobot() {
        JMenu otherMenu = new JMenu("Создать робота");
        ActionListener act = (event) -> {
            m_visualizer.addRobot();
        };
        addSubMenu(otherMenu, "новый робот", act, KeyEvent.VK_A);
        return otherMenu;
    }

    private void addSubMenu(JMenu bar, String name, ActionListener listener, int keyEvent){
        JMenuItem subMenu = new JMenuItem(name, keyEvent);
        subMenu.addActionListener(listener);
        bar.add(subMenu);
    }

    ArrayList<RobotMove> getRobots(){
        return m_visualizer.robots;
    }


    void addObstacle(Obstacle o){
        m_visualizer.robot.obstacles.add(o);
    }

    ArrayList<Obstacle> getObstacles(){
        return m_visualizer.robot.obstacles;
    }

    void addObs(RobotCoordWindow window){//добавление наблюдателей
        for (int i = 0;i<m_visualizer.robots.size();i++) {
            RobotMove robotMove = m_visualizer.robots.get(i);
            robotMove.observable.add(window);
          //  m_visualizer.robot.observable.add(window);
        }
    }

    JInternalFrame getObserver(){//получить объект наблюдателя
        return  (JInternalFrame) m_visualizer.robot.observable.get(0);
    }

    boolean isNoObs(){//проверка на существование наблюдателей
        return  m_visualizer.robot.observable.isEmpty();
    }

    void removeObs(RobotCoordWindow window){//удаление наблюдателей
        m_visualizer.robot.observable.remove(window);
    }

    double getRobotX(){
        return m_visualizer.robot.m_robotPositionX;
    }

    double getRobotY(){
        return m_visualizer.robot.m_robotPositionY;
    }

    double getDirection(){
        return m_visualizer.robot.m_robotDirection;
    }

    void setDirection(double direction, int i){
        m_visualizer.robots.get(i).m_robotDirection = direction;
    }

    void setRobotPosition(double x, double y,int i){
        m_visualizer.robots.get(i).m_robotPositionX=x;
        m_visualizer.robots.get(i).m_robotPositionY= y;
    }

    String getCoords(){
        return "x: " + m_visualizer.robot.m_robotPositionX + "\r\ny: " + m_visualizer.robot.m_robotPositionY + "\r\n";
    }

    Point getTargetPosition(){
        return m_visualizer.robot.getTargetPosition();
    }

    void setTargetPosition(Point position,int i){
        m_visualizer.robots.get(i).setTargetPosition(position);
    }

    void addRobots(int i){
        for (int k=1;k<i;k++){
            m_visualizer.addRobot();
        }
    }
}

