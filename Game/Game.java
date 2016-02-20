package minesweeper.Game;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author Sehnsucht
 */
public class Game {

    private final ArrayList<GameListener> gameListeners;

    private final int width, height;

    private int[][] board;

    public final int DEBUG_LEVEL = 0; // 0 = off, 1 = show heatmap, 2 = show heatmap + scores

    public Game(int width, int height) {
        this.gameListeners = new ArrayList<>();
        this.width = width;
        this.height = height;
        initGame();
    }

    //Update game by 1 tick
    public void tick() {

    }

    private void initGame() {
        this.board = new int[width][height];

    }

    public void resetGame() {

    }


    public void addGameListener(GameListener gl) {
        gameListeners.add(gl);
    }

    
    //Notify all listeners that the game has changed
    private void notifyListeners() {
        for (GameListener gl : gameListeners) {
            gl.gameChanged();
        }
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
