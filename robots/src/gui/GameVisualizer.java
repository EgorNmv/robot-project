package gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.*;
import java.util.Timer;

import javax.swing.*;

public class GameVisualizer extends JPanel
{
    private final Timer m_timer = initTimer();
    private static Timer initTimer() 
    {
        Timer timer = new Timer("events generator", true);
        return timer;
    }


    RobotMove robot = new RobotMove();
    ArrayList<RobotMove> robots = new ArrayList<>();

    public GameVisualizer() {
        robots.add(robot);

        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 50);
            m_timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    for (int i = 0;i<robots.size();i++) {
                        robots.get(i).onModelUpdateEvent();
                    }
                }
            }, 0, 10);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    for (int i = 0;i<robots.size();i++) {
                        robots.get(i).setTargetPosition(e.getPoint());
                        repaint();
                    }
                }
                else if (e.getButton() == MouseEvent.BUTTON3) {
                    for (int i = 0;i<robots.size();i++) {
                        robots.get(i).obstacles.add(new Obstacle(e.getPoint()));
                    }
                }
                else if (e.getButton() == MouseEvent.BUTTON2) {
                    for (int i = 0; i < robots.size(); i++) {
                        robots.get(i).obstacles.removeIf(obstacle -> obstacle.contains(e.getPoint()));
                    }
                }
            }
        });
        setDoubleBuffered(true);
    }

    void addRobot() {
        LoadClass lc = new LoadClass("C:\\Users\\skalo\\Desktop\\Java\\Robots-master\\robots\\out\\production\\Robots-master\\algs");
        String[] choices = new String[lc.classes.size()];
        choices = lc.classes.keySet().toArray(choices);
        String in = (String) JOptionPane.showInputDialog(null, "Выберите алгоритм", "Выбор алгоритма", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
        if (in != null) {
            RobotMove robotMove = new RobotMove();
            robotMove.cls = lc.classes.get(in);
            robots.add(robotMove);
            robotMove.obstacles = robot.obstacles;
        }
    }

    protected void onRedrawEvent()
    {
        EventQueue.invokeLater(this::repaint);
    }

    private static int round(double value)
    {
        return (int)(value + 0.5);
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (int k = 0;k<robots.size();k++) {
        Graphics2D g2d = (Graphics2D) g;
            RobotMove robotMove = robots.get(k);
            drawRobot(g2d, round(robotMove.m_robotPositionX), round(robotMove.m_robotPositionY), robotMove.m_robotDirection);
            drawTarget(g2d, robotMove.m_targetPositionX, robotMove.m_targetPositionY);
            g.drawLine((int) robotMove.m_robotPositionX, (int) robotMove.m_robotPositionY, robotMove.m_targetPositionX, robotMove.m_targetPositionY);

            for (int i = 0; i < robotMove.obstacles.size(); i++) {
                drawObstacle(robotMove.obstacles.get(i), g2d);
            }
        }
    }

    private void drawObstacle(Obstacle obstacle,Graphics g){//рисовка робота
        g.setColor(Color.blue);
        g.fillRect(obstacle.getLeftUp().x+1,obstacle.getLeftUp().y+1,99,49);
        g.setColor(Color.BLACK);
        g.drawRect(obstacle.getLeftUp().x,obstacle.getLeftUp().y,100,50);
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }
    
    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }
    
    private void drawRobot(Graphics2D g, int x, int y, double direction)
    {
        int robotCenterX =x;
        int robotCenterY =y;
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY); 
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX  + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX  + 10, robotCenterY, 5, 5);
    }

    private void drawRect(Graphics g, int x, int y){
        g.drawRect(x-50,y-50, 100, 100);
    }
    
    private void drawTarget(Graphics2D g, int x, int y)
    {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0); 
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }


}
