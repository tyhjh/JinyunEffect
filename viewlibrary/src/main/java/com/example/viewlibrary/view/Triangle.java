package com.example.viewlibrary.view;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Triangle {

    Point topPoint1, topPoint2, topPoint3;

    public Triangle(Point topPoint1, Point topPoint2, Point topPoint3) {
        this.topPoint1 = topPoint1;
        this.topPoint2 = topPoint2;
        this.topPoint3 = topPoint3;
    }


    public static Random random = new Random(System.currentTimeMillis());
    public static List<Point> pointList = new ArrayList<>();


    public static Triangle getRandomTriangle() {
        Triangle triangle = null;
        pointList.clear();
        for (int i = -5; i <= 5; i++) {
            for (int k = -5; k < 5; k++) {
                pointList.add(new Point(i, k));
            }
        }

        Point topPoint1, topPoint2, topPoint3;
        topPoint1 = pointList.get(random.nextInt(120));
        pointList.remove(topPoint1);
        topPoint2 = pointList.get(random.nextInt(119));
        pointList.remove(topPoint2);
        topPoint3 = pointList.get(random.nextInt(118));

        triangle = new Triangle(topPoint1, topPoint2, topPoint3);
        if (isTriangle(triangle)) {
            return triangle;
        } else {
            return getRandomTriangle();
        }
    }


    public static boolean isTriangle(Triangle triangle) {
        int a = (int) Math.sqrt((triangle.topPoint1.x - triangle.topPoint2.x) * (triangle.topPoint1.y - triangle.topPoint2.y));
        int b = (int) Math.sqrt((triangle.topPoint1.x - triangle.topPoint3.x) * (triangle.topPoint1.y - triangle.topPoint3.y));
        int c = (int) Math.sqrt((triangle.topPoint2.x - triangle.topPoint3.x) * (triangle.topPoint2.y - triangle.topPoint3.y));
        if (a + b <= c || a + c <= b || b + c >= a) {
            return false;
        }
        if (a == 0 || b == 0 || c == 0) {
            return false;
        }

        return true;
    }

}
