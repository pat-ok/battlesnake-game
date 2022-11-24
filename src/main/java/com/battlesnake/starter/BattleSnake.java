package com.battlesnake.starter;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;

public class BattleSnake {
    private Point head;
    private Point neck;
    private Point tail;
    private ArrayList<Point> body;
    private int health;
    private int length;

    public BattleSnake(JsonNode snake) {
        // record health
        this.health = snake.get("health").asInt();

        // record length
        this.length = snake.get("length").asInt();

        for (int i = 0; i < length; i++) {
            // point for snake section
            Point temp = new Point(snake.get("body").get(i).get("x").asInt(),
                    snake.get("body").get(i).get("y").asInt());

            // record head
            if (i == 0) {
                this.head = temp;
            }

            // record neck
            if (i == 1) {
                this.neck = temp;
            }

            // record tail
            if (i == (length - 1)) {
                this.tail = temp;
            }

            // record body
            body.add(temp);
        }
    }

    public Point getHead() {
        return this.head;
    }

    public Point getNeck() {
        return this.neck;
    }

    public Point getTail() {
        return this.tail;
    }

    public ArrayList<Point> getBody() {
        return this.body;
    }

    public int getHealth() {
        return this.health;
    }

    public int getLength() {
        return this.length;
    }
}
