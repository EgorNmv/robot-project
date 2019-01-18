package gui;

import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается.
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    private int syncWindows = 0; //количество связных окон
    private int countLogs = 0;
    private int countGames = 0;
    private int countCoords = 0;
    private int countObstacles=0;

    private final String WINDOWDATA = "window";
    private final String ROBOTPOSITION="Robot_Position";
    private final String OBSTACLE = "Obstacles";

    public MainApplicationFrame()throws IOException {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane);

        try {
            ObjectInputStream pos = new ObjectInputStream(new BufferedInputStream(new FileInputStream(ROBOTPOSITION)));
            ObjectInputStream win = new ObjectInputStream(new BufferedInputStream(new FileInputStream(WINDOWDATA)));
            ObjectInputStream obst = new ObjectInputStream(new BufferedInputStream(new FileInputStream(OBSTACLE)));
            int robots = pos.read();
            syncWindows = win.read();
            int windows = win.read();
            for (int i = 0; i < syncWindows; i++) {//добавление всех свзяных окон
                GameWindow gameWindow = createGameWindow((SaveWindow) win.readObject());//окно игры

                RobotCoordWindow coordWindow = createCoordWin((SaveWindow) win.readObject());//окно координат
                gameWindow.addObs(coordWindow);//привязка окна координат к игровому

                gameWindow.addRobots(robots);
                for (int k=0;k<robots;k++) {
                    setRobotPosition(gameWindow, (SaveRobot) pos.readObject(),k);//восстановление позиций робота
                }
                coordWindow.setText(gameWindow.getCoords());


                countObstacles = obst.read();//восстановление препятствий
                for (int o = 0;o<countObstacles;o++){
                    gameWindow.addObstacle((Obstacle) obst.readObject());
                }

                addWindow(gameWindow);
                addWindow(coordWindow);
            }
            for (int i = 0; i < windows; i++) {//добавление оставшихся независимых окон
                SaveWindow window = (SaveWindow) win.readObject();
                switch (window.name) {
                    case 'l':
                        addWindow(createLogWindow(window));//создание окна лога
                        break;
                    case 'g':
                        GameWindow gameWindow = createGameWindow(window);//создание окна игры
                        gameWindow.addRobots(robots);
                        for (int k=0;k<robots;k++) {
                            setRobotPosition(gameWindow, (SaveRobot) pos.readObject(),k);//восстановление позиций робота
                        }

                        countObstacles = obst.read();//восстановление препятствий
                        for (int o = 0;o<countObstacles;o++){
                            gameWindow.addObstacle((Obstacle) obst.readObject());
                        }
                        addWindow(gameWindow);
                        break;
                    case 'c':
                        RobotCoordWindow coordWindow = createCoordWin(window);//создание окна координат
                        coordWindow.setText("Нет наблюдаемых");
                        addWindow(coordWindow);
                }
            }
            if (countLogs == 0) addWindow(createLogWindow());
            if (countGames == 0) {//если нет и игрового и координатного окна то создать сразу два и связать их
                if (countCoords == 0) {
                    GameWindow gameWindow = createGameWindow();
                    RobotCoordWindow coordWindow = createCoordWin();
                    gameWindow.addObs(coordWindow);
                    coordWindow.setText("x: " + gameWindow.getRobotX() + "\r\ny: " + gameWindow.getRobotY());
                    addWindow(gameWindow);
                    addWindow(coordWindow);
                } else addWindow(createGameWindow());//иначе просто добавить игровое независимое
            }
            ;
            if (countCoords == 0) addWindow(createCoordWin());
            pos.close();
            win.close();
        }
        catch (IOException e){
            addWindow(createLogWindow());
            GameWindow window = createGameWindow();
            addWindow(window);
            RobotCoordWindow coordWindow=createCoordWin();
            addWindow(coordWindow);
            window.addObs(coordWindow);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(getClosingAdapter());
    }

    private void setRobotPosition(GameWindow gameWindow, SaveRobot robot, int i){
        gameWindow.setRobotPosition(robot.x,robot.y,i);//позиция робота
        gameWindow.setTargetPosition(robot.aim,i);//позиция цели
        gameWindow.setDirection(robot.orientation,i);//ориентация робота
    }

    private LogWindow createLogWindow(){
        return createLogWindow(new SaveWindow('l',10,10,300,300));
    }

    private GameWindow createGameWindow(){
        return createGameWindow(new SaveWindow('g',100,100,300,300));
    }

    private RobotCoordWindow createCoordWin(){
        return createCoordWin(new SaveWindow('c',400,100,300,300));
    }

    private GameWindow createGameWindow(SaveWindow game){
        GameWindow gameWindow = new GameWindow();
        gameWindow.setLocation(game.point);
        gameWindow.setSize(game.dimension);
        //gameWindow.setTitle(gameWindow.getTitle()+(String.valueOf(countGames)));
        countGames++;
        setMinimumSize(gameWindow.getSize());

        return gameWindow;
    }

    private RobotCoordWindow createCoordWin(SaveWindow game){
        RobotCoordWindow coordWindow = new RobotCoordWindow();
        coordWindow.setLocation(game.point);
        coordWindow.setSize(game.dimension);
        //coordWindow.setTitle(coordWindow.getTitle()+(String.valueOf(countCoords)));
        countCoords++;
        setMinimumSize(coordWindow.getSize());
        return coordWindow;
    }

    private LogWindow createLogWindow(SaveWindow log)
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(log.point);
        logWindow.setSize(log.dimension);
        //logWindow.setTitle(logWindow.getTitle()+(String.valueOf(countLogs)));
        countLogs++;
        setMinimumSize(logWindow.getSize());

        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    private WindowAdapter getClosingAdapter(){
        return  new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                String ObjButtons[] = {"Да","Нет"};
                int PromptResult = JOptionPane.showOptionDialog(null,"Вы уверены, что хотите выйти?","Выход",JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE,null,ObjButtons,ObjButtons[1]);
                if(PromptResult== JOptionPane.YES_OPTION)
                {
                    writeWindows(readWindows());
                    System.exit(0);
                }
            }
        };
    }


    private void saveRobotPositionAndObstacles(ArrayList<GameWindow> gameWindows){//сохранение позиции робота
        try{
            ObjectOutputStream robotStream =new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(ROBOTPOSITION)));
            ObjectOutputStream obstacleStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(OBSTACLE)));
            for (GameWindow g:gameWindows){
                ArrayList<RobotMove> robots=g.getRobots();
                robotStream.write(robots.size());
                for (RobotMove robotMove:robots) {
                    robotStream.writeObject(new SaveRobot(robotMove));//сохранение роботов
                }
                robotStream.flush();
                ArrayList<Obstacle> list = g.getObstacles();//сохранение препятствий
                obstacleStream.write(list.size());
                for (Obstacle o:list){
                    obstacleStream.writeObject(o);
                }
                obstacleStream.flush();
            }
            robotStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private ArrayList<SaveWindow> readWindows() {//считывание окошек
        syncWindows =0;
        countGames=0;
        ArrayList<SaveWindow> windows = new ArrayList<>();
        ArrayList<LogWindow> logWindows=new ArrayList<>();
        ArrayList<GameWindow> gameWindows=new ArrayList<>();
        ArrayList<RobotCoordWindow> coordWindows = new ArrayList<>();
        for (Component comp: desktopPane.getAllFrames())
            if (comp instanceof RobotCoordWindow)
                coordWindows.add((RobotCoordWindow) comp);
        for (int i = 0; i< desktopPane.getComponents().length;i++){
            Component c = desktopPane.getComponents()[i];
            String s = c.getAccessibleContext().getAccessibleName();

            switch (s.replaceAll("\\d","")) {
                case "Игровое поле":

                    if ( !(((GameWindow)c).isNoObs()||((GameWindow)c).getObserver().isClosed())) {//если есть наблюдатели и не закрыты
                        RobotCoordWindow coordWindow =(RobotCoordWindow) ((GameWindow) c).getObserver();
                        if (Arrays.asList(desktopPane.getComponents()).contains(coordWindow)) {
                            windows.add(new SaveWindow('g', c.getLocation(), c.getSize()));
                            windows.add(new SaveWindow('c', ((GameWindow) c).getObserver().getLocation(), ((GameWindow) c).getObserver().getSize()));
                            coordWindows.remove(coordWindow);
                            syncWindows++;
                        }
                    }
                    gameWindows.add((GameWindow)c);
                    countGames++;
                    break;
                case "Протокол работы":
                    logWindows.add((LogWindow) c);
                    break;
            }
        }
        for (LogWindow l:logWindows) {
            SaveWindow logWindow = new SaveWindow('l',l.getLocation(), l.getSize());
            windows.add(logWindow);
        }
        for (int i = syncWindows; i<countGames;++i) {
            SaveWindow gameWindow = new SaveWindow('g',gameWindows.get(i).getLocation(), gameWindows.get(i).getSize());
            windows.add(gameWindow);
            windows.add(new SaveWindow('r',gameWindow.point,new Dimension()));
        }
        for (RobotCoordWindow c:coordWindows) {
            SaveWindow coordWindow = new SaveWindow('c',c.getLocation(), c.getSize());
            if (!windows.contains(coordWindow))
            windows.add(coordWindow);
        }
        saveRobotPositionAndObstacles(gameWindows);
        return windows;
    }

    private void writeWindows(ArrayList<SaveWindow> windows){//запись данных в файл
        try {
            ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(WINDOWDATA)));
            os.write(syncWindows);
            os.write(windows.size()-syncWindows*2);
            for (SaveWindow e:windows){
                os.writeObject(new SaveWindow(e.name,e));
                os.flush();
            }
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private void addSubMenu(JMenu bar, String name, ActionListener listener, int keyEvent){
        JMenuItem subMenu = new JMenuItem(name, keyEvent);
        subMenu.addActionListener(listener);
        bar.add(subMenu);
    }

    private JMenu generateLookAndFeel() {
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");


        ActionListener act = (event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        };
        addSubMenu(lookAndFeelMenu, "Системная схема", act, KeyEvent.VK_S);

        act = (event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        };
        addSubMenu(lookAndFeelMenu, "Универсальная схема", act, KeyEvent.VK_S);
        return lookAndFeelMenu;
    }

    private JMenu generateTest(){
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        ActionListener act = (event) -> {
            Logger.debug("Новая строка");
        };
        addSubMenu(testMenu, "Сообщение в лог",act, KeyEvent.VK_S);
        testMenu.setMnemonic(KeyEvent.VK_O);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");
        return testMenu;
    }

    private JMenu generateOther(){
        JMenu otherMenu = new JMenu("Другое");
        ActionListener act = (event) -> {
            addWindow(createLogWindow());
            GameWindow game = createGameWindow();
            RobotCoordWindow win = createCoordWin();
            game.addObs(win);
            win.setText("x: "+game.getRobotX() + "\r\ny: " + game.getRobotY());
            addWindow(game);
            addWindow(win);
        };
        addSubMenu(otherMenu, "Нужно больше окон!",act, KeyEvent.VK_A);

        act = (event) -> {
            System.exit(0);
        };
        addSubMenu(otherMenu, "Выход",act, KeyEvent.VK_E);
        return otherMenu;
    }

    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu lookAndFeelMenu = generateLookAndFeel();
        JMenu testMenu = generateTest();
        JMenu otherMenu = generateOther();
        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        menuBar.add(otherMenu);
        return menuBar;
    }

    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
}
