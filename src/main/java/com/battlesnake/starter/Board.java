package com.battlesnake.starter;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;

public class Board {
    private BattleSnake you;
    private BattleSnake opp1;
    private BattleSnake opp2;
    private BattleSnake opp3;
    private ArrayList<Point> occupied;
    private ArrayList<Food> food;

    public Board(JsonNode moveRequest) {
        you = new BattleSnake(moveRequest.get("you"));
        occupied.addAll(you.getBody());
        occupied.remove(you.getTail());
        if (moveRequest.get("snakes").size() > 1) {
            opp1 = new BattleSnake(moveRequest.get("snakes").get(1));
            occupied.addAll(opp1.getBody());
            occupied.remove(opp1.getTail());
        }
        if (moveRequest.get("snakes").size() > 2) {
            opp2 = new BattleSnake(moveRequest.get("snakes").get(2));
            occupied.addAll(opp2.getBody());
            occupied.remove(opp2.getTail());
        }
        if (moveRequest.get("snakes").size() > 3) {
            opp3 = new BattleSnake(moveRequest.get("snakes").get(3));
            occupied.addAll(opp3.getBody());
            occupied.remove(opp3.getTail());
        }

        for (JsonNode food : moveRequest.get("food")) {
            this.food.add(new Food(new Point(food.get("x").asInt(), food.get("y").asInt())));
        }
    }

    public BattleSnake getYou() {
        return you;
    }

    public BattleSnake getOpp1() {
        return opp1;
    }

    public BattleSnake getOpp2() {
        return opp2;
    }

    public BattleSnake getOpp3() {
        return opp3;
    }

    // Does not include tails
    public ArrayList<Point> getOccupied() {
        return occupied;
    }

    public ArrayList<Food> getFood() {
        return food;
    }
}
