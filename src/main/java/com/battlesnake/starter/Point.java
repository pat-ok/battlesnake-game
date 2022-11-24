package com.battlesnake.starter;

import java.util.Objects;

public class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Point)) {
            return false;
        }

        Point object = (Point) obj;

        return this.x == object.getX() && this.y == object.getY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
