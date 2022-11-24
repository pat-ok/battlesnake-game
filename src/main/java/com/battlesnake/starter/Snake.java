package com.battlesnake.starter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.*;

import static spark.Spark.*;

/**
 * This is a simple Battlesnake server written in Java.
 * 
 * For instructions see
 * https://github.com/BattlesnakeOfficial/starter-snake-java/README.md
 */
public class Snake {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final Handler HANDLER = new Handler();
    private static final Logger LOG = LoggerFactory.getLogger(Snake.class);

    /**
     * Main entry point.
     *
     * @param args are ignored.
     */
    public static void main(String[] args) {
        String port = System.getProperty("PORT");
        if (port == null) {
            LOG.info("Using default port: {}", port);
            port = "8080";
        } else {
            LOG.info("Found system provided port: {}", port);
        }
        port(Integer.parseInt(port));
        get("/", HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/start", HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/move", HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/end", HANDLER::process, JSON_MAPPER::writeValueAsString);
    }

    /**
     * Handler class for dealing with the routes set up in the main method.
     */
    public static class Handler {

        /**
         * For the start/end request
         */
        private static final Map<String, String> EMPTY = new HashMap<>();

        /**
         * Generic processor that prints out the request and response from the methods.
         *
         * @param req
         * @param res
         * @return
         */
        public Map<String, String> process(Request req, Response res) {
            try {
                JsonNode parsedRequest = JSON_MAPPER.readTree(req.body());
                String uri = req.uri();
                LOG.info("{} called with: {}", uri, req.body());
                Map<String, String> snakeResponse;
                if (uri.equals("/")) {
                    snakeResponse = index();
                } else if (uri.equals("/start")) {
                    snakeResponse = start(parsedRequest);
                } else if (uri.equals("/move")) {
                    snakeResponse = move(parsedRequest);
                } else if (uri.equals("/end")) {
                    snakeResponse = end(parsedRequest);
                } else {
                    throw new IllegalAccessError("Strange call made to the snake: " + uri);
                }

                LOG.info("Responding with: {}", JSON_MAPPER.writeValueAsString(snakeResponse));

                return snakeResponse;
            } catch (JsonProcessingException e) {
                LOG.warn("Something went wrong!", e);
                return null;
            }
        }

        /**
         * This method is called everytime your Battlesnake is entered into a game.
         * 
         * Use this method to decide how your Battlesnake is going to look on the board.
         *
         * @return a response back to the engine containing the Battlesnake setup
         *         values.
         */
        public Map<String, String> index() {
            Map<String, String> response = new HashMap<>();
            response.put("apiversion", "1");
            response.put("author", "Manish"); // TODO: Your Battlesnake Username
            response.put("color", "#00FF00"); // TODO: Personalize
            response.put("head", "default"); // TODO: Personalize
            response.put("tail", "default"); // TODO: Personalize
            return response;
        }

        /**
         * This method is called everytime your Battlesnake is entered into a game.
         * 
         * Use this method to decide how your Battlesnake is going to look on the board.
         *
         * @param startRequest a JSON data map containing the information about the game
         *                     that is about to be played.
         * @return responses back to the engine are ignored.
         */
        public Map<String, String> start(JsonNode startRequest) {
            LOG.info("START");
            return EMPTY;
        }

        /**
         * This method is called on every turn of a game. It's how your snake decides
         * where to move.
         * 
         * Use the information in 'moveRequest' to decide your next move. The
         * 'moveRequest' variable can be interacted with as
         * com.fasterxml.jackson.databind.JsonNode, and contains all of the information
         * about the Battlesnake board for each move of the game.
         * 
         * For a full example of 'json', see
         * https://docs.battlesnake.com/references/api/sample-move-request
         *
         * @param moveRequest JsonNode of all Game Board data as received from the
         *                    Battlesnake Engine.
         * @return a Map<String,String> response back to the engine the single move to
         *         make. One of "up", "down", "left" or "right".
         */
        public Map<String, String> move(JsonNode moveRequest) {

            try {
                LOG.info("Data: {}", JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(moveRequest));
            } catch (JsonProcessingException e) {
                LOG.error("Error parsing payload", e);
            }

            /*
             * Example how to retrieve data from the request payload:
             * 
             * String gameId = moveRequest.get("game").get("id").asText();
             * 
             * int height = moveRequest.get("board").get("height").asInt();
             * 
             */

            // Current board state
            Board state = new Board(moveRequest);
            // Current head position
            Point head = state.getYou().getHead();
            // Directions around head
            Point moveUp = new Point(head.getX(), head.getY() + 1);
            Point moveDown = new Point(head.getX(), head.getY() - 1);
            Point moveLeft = new Point(head.getX() - 1, head.getY());
            Point moveRight = new Point(head.getX() + 1, head.getY());


            // Determine safe moves
            ArrayList<Point> goodMoves = new ArrayList<Point>();

            ArrayList<Point> smarterMoves = getSmarterMoves(state, moveUp, moveDown, moveLeft, moveRight);
            for (Point point : smarterMoves) {
                LOG.info("SAFE MOVE {}", point.getX());
                LOG.info("SAFE MOVE {}", point.getY());
            }

            goodMoves.addAll(smarterMoves);

//            for (Point point : goodMoves) {
//                if (state.getPreoccupied().contains(point)) {
//                    goodMoves.remove(point);
//                }
//            }
//
//            if (goodMoves.isEmpty()) {
//                goodMoves.addAll(smarterMoves);
//            }

//            for (Point move : goodMoves) {
//                if (goodMoves.size() != 0) {
//                    if (state.getPreoccupied().contains(move)) {
//                        goodMoves.remove(move);
//                    }
//                } else {
//                    break;
//                }
//            }

//            ArrayList<Point> evenSmarterMoves = getEvenSmarterMoves(state, smarterMoves);
//            if (evenSmarterMoves.size() != 0) {
//                goodMoves.addAll(evenSmarterMoves);
//            } else {
//                goodMoves.addAll(smarterMoves);
//            }

            String direction = "up";
            Map<String, String> response = new HashMap<>();

            // If snake is hungry or if the snake is shorter than 6, chase food
            if ((moveRequest.get("you").get("health").asInt() < 17 ||
                    moveRequest.get("you").get("length").asInt() < 4 || moveRequest.get("you").get("length").asInt() % 2 != 0) &&
                    !state.getFood().isEmpty()) {
                direction = pointToString(chaseFood(goodMoves, state), moveUp, moveDown, moveLeft);
                response.put("move", direction);
                return response;

                // Otherwise it will chase tail
            } else {
                direction = pointToString(chaseTail(goodMoves, state), moveUp, moveDown, moveLeft);
                response.put("move", direction);
                return response;
            }
        }

        public static Point chaseTail(ArrayList<Point> smarterMoves, Board state) {
            // find tail
            Point tail = state.getYou().getTail();

            // find move that brings snake closer to food
            Point closerMove = null; // closer move

            double closestDirection = 20; // closest direction to food
            for (Point point : smarterMoves) {
                double temp = calculateDistance(point, tail);
                if (temp < closestDirection) {
                    closestDirection = temp;
                    closerMove = point;
                }
            }
            return closerMove;
        }

        public static Point chaseFood(ArrayList<Point> smarterMoves, Board state) {
            // find closest food point
            ArrayList<Food> foods = state.getFood();
            Point food = null; // closest food
            double closestFood = 20; // closest food location
            for (Food piece : foods) {
                double temp = calculateDistance(state.getYou().getHead(), piece.getLocation());
                if (temp < closestFood) {
                    closestFood = temp;
                    food = piece.getLocation();
                }
            }

            // find move that brings snake closer to food
            Point closerMove = null; // closer move
            double closestDirection = 20; // closest direction to food
            for (Point point : smarterMoves) {
                double temp = calculateDistance(point, food);
                if (temp < closestDirection) {
                    closestDirection = temp;
                    closerMove = point;
                }
            }
            return closerMove;
        }


        // Calculate the Euclidian distance between two points
        public static double calculateDistance(Point a, Point b) {
            return Math.sqrt(Math.pow((a.getX() - b.getX()), 2) + Math.pow((a.getY() - b.getY()), 2));
        }

        public String pointToString(Point point, Point moveUp, Point moveDown, Point moveLeft) {
            if (point == moveUp) {
                return "up";
            } else if (point == moveDown) {
                return "down";
            } else if (point == moveLeft) {
                return "left";
            } else {
                return "right";
            }
        }

        // Given a board, list the moves that are not immediately dangerous
        // Exclude moves that will be in own or opponent snake bodies and out of bounds
        public ArrayList<Point> getSmarterMoves(Board state, Point moveUp, Point moveDown, Point moveLeft, Point moveRight) {
            ArrayList<Point> smarterMoves = new ArrayList<>(Arrays.asList(moveUp, moveDown, moveLeft, moveRight));

            if (moveUp.getY() > 10 || state.getOccupied().contains(moveUp)) {
                smarterMoves.remove(moveUp);
            }

            if (moveDown.getY() < 0 || state.getOccupied().contains(moveDown)) {
                smarterMoves.remove(moveDown);
            }

            if (moveLeft.getX() < 0 || state.getOccupied().contains(moveLeft)) {
                smarterMoves.remove(moveLeft);
            }

            if (moveRight.getX() > 10 || state.getOccupied().contains(moveRight)) {
                smarterMoves.remove(moveRight);
            }
            return smarterMoves;
        }

//        public ArrayList<Point> getEvenSmarterMoves(Board state, ArrayList<Point> smarterMoves) {
//            ArrayList<Point> evenSmarterMoves = new ArrayList<>();
//            evenSmarterMoves.addAll(smarterMoves);
//
//            for (Point smarterMove : evenSmarterMoves) {
//                if (state.getPreoccupied().contains(smarterMove)) {
//                    evenSmarterMoves.remove(smarterMove);
//                }
//            }
//            return evenSmarterMoves;
//        }

        /**
         * Remove the 'neck' direction from the list of possible moves
         * 
         * @param head          JsonNode of the head position e.g. {"x": 0, "y": 0}
         * @param body          JsonNode of x/y coordinates for every segment of a
         *                      Battlesnake. e.g. [ {"x": 0, "y": 0}, {"x": 1, "y": 0},
         *                      {"x": 2, "y": 0} ]
         * @param possibleMoves ArrayList of String. Moves to pick from.
         */
        public void avoidMyNeck(JsonNode head, JsonNode body, ArrayList<String> possibleMoves) {
            JsonNode neck = body.get(1);

            if (neck.get("x").asInt() < head.get("x").asInt()) {
                possibleMoves.remove("left");
            } else if (neck.get("x").asInt() > head.get("x").asInt()) {
                possibleMoves.remove("right");
            } else if (neck.get("y").asInt() < head.get("y").asInt()) {
                possibleMoves.remove("down");
            } else if (neck.get("y").asInt() > head.get("y").asInt()) {
                possibleMoves.remove("up");
            }
        }

        /**
         * This method is called when a game your Battlesnake was in ends.
         * 
         * It is purely for informational purposes, you don't have to make any decisions
         * here.
         *
         * @param endRequest a map containing the JSON sent to this snake. Use this data
         *                   to know which game has ended
         * @return responses back to the engine are ignored.
         */
        public Map<String, String> end(JsonNode endRequest) {
            LOG.info("END");
            return EMPTY;
        }
    }

}
