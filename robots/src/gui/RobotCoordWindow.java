package gui;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class RobotCoordWindow extends JInternalFrame implements Observer {

    private final JPanel panel = new JPanel(new BorderLayout());
    private JTextArea text = new JTextArea();

    public RobotCoordWindow(){
        super("Координаты робота", true, true, true, true);
        getContentPane().add(panel);
    }

    public void setText(String string){
        text.setText(string);
        panel.add(text);
    }

    @Override
    public void update(Observable o, Object arg) {
        RobotMove move = (RobotMove) o;
        text.setText("x: "+move.m_robotPositionX+"\r\ny: "+move.m_robotPositionY);
        panel.add(text);
    }
}
