package com.battlesnake.starter;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;

public class Board {
    private BattleSnake you;
    private BattleSnake opp1;
    private BattleSnake opp2;
    private BattleSnake opp3;
    private BattleSnake opp4;
    private ArrayList<Point> occupied = new ArrayList<>();
    private ArrayList<Point> preoccupied = new ArrayList<>();
    private ArrayList<Food> food = new ArrayList<>();

    public Board(JsonNode moveRequest) {
        you = new BattleSnake(moveRequest.get("you"));
        occupied.addAll(you.getBody());
        occupied.remove(you.getTail());

        if (moveRequest.get("board").get("snakes").get(0) != null && moveRequest.get("board").get("snakes").get(0).get("name").toString() != "battlesnake-test") {
            opp1 = new BattleSnake(moveRequest.get("board").get("snakes").get(0));
            occupied.addAll(opp1.getBody());
            occupied.remove(opp1.getTail());
            preoccupied.addAll(opp1.getAroundHead());
        }

        if (moveRequest.get("board").get("snakes").get(1) != null && moveRequest.get("board").get("snakes").get(1).get("name").toString() != "battlesnake-test") {
            opp2 = new BattleSnake(moveRequest.get("board").get("snakes").get(1));
            occupied.addAll(opp2.getBody());
            occupied.remove(opp2.getTail());
            preoccupied.addAll(opp2.getAroundHead());
        }

        if (moveRequest.get("board").get("snakes").get(2) != null && moveRequest.get("board").get("snakes").get(2).get("name").toString() != "battlesnake-test") {
            opp3 = new BattleSnake(moveRequest.get("board").get("snakes").get(2));
            occupied.addAll(opp3.getBody());
            occupied.remove(opp3.getTail());
            preoccupied.addAll(opp3.getAroundHead());
        }

        if (moveRequest.get("board").get("snakes").get(3) != null && moveRequest.get("board").get("snakes").get(3).get("name").toString() != "battlesnake-test") {
            opp4 = new BattleSnake(moveRequest.get("board").get("snakes").get(3));
            occupied.addAll(opp4.getBody());
            occupied.remove(opp4.getTail());
            preoccupied.addAll(opp4.getAroundHead());
        }

        for (JsonNode food : moveRequest.get("board").get("food")) {
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

    public ArrayList<Point> getPreoccupied() {
        return preoccupied;
    }

    public ArrayList<Food> getFood() {
        return food;
    }
}
