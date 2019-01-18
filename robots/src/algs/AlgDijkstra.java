package algs;


import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class AlgDijkstra {

    public Point alg(Point start, Point finish,HashMap<Point,ArrayList<Point>> map) {
        ArrayList<Point> vertices = new ArrayList<>(map.keySet());
        HashMap<Point,Double> distance = new HashMap<>();
        HashMap<Point, Point> prev = new HashMap<>();//список предыдущих, по которому восстановится маршрут
        ArrayList<Point> track = new ArrayList<>();//маршрут

        for (Point p : map.keySet()) {
            if (map.containsKey(start)) {
                if (map.get(start).contains(p)) {//инициализация
                    map.get(p).remove(start);
                    distance.put(p, distance(start.x, start.y, p.x, p.y));
                    prev.put(p, start);//старт - перед p
                } else distance.put(p, 1000000.0);
            }
        }
        map.remove(finish);
        int n = map.keySet().size();//количество вершин
        for (int k = 1; k < n; k++) {//количество итераций
            Point w = minV(vertices,distance);//беру точку с наименьшим расстоянием от начала
            vertices.remove(w);//удаляю из множества вершин
            if (!map.containsKey(w))
                continue;
            for (Point v : map.get(w)) {//высчитывание кратчайшего расстояния
                if (distance.containsKey(v)) {
                    if (distance.get(w) + distance(w.x, w.y, v.x, v.y) < distance.get(v)) {
                        distance.put(v, distance.get(w) + distance(w.x, w.y, v.x, v.y));
                        prev.put(v, w);
                    }
                } else {
                    if (distance.containsKey(w)) {
                        distance.put(v, distance.get(w) + distance(w.x, w.y, v.x, v.y));
                        prev.put(v, w);
                    }
                }
            }
        }
        try {//возврат ближайшей точки
            Point t = finish;
            track.add(t);
            while (!(t.equals(start))) {
                t = prev.get(t);
                track.add(t);
            }
            if (track.size() == 2)//если вершины 2, то это старт и финиш
                return finish;
            else if (track.size() > 2)//если больше, то сразу после стартовой ближайшая
                return track.get(track.size() - 2);//стартовая записана последней
            else return start;//необъяснимая ситуация, вернуть стартовую точку
        } catch (NullPointerException e) {
            if (track.size() > 2)
                return track.get(1);
            else return start;
        }
    }

    private Point minV(ArrayList<Point> list,HashMap<Point,Double> distance) {//наименьшее значение из данных
        Point min = list.get(0);
        for (Point p : list) {
            if (distance.containsKey(p) && distance.containsKey(min))
                if (distance.get(p) < distance.get(min))
                    min = p;
        }
        return min;
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }
}
