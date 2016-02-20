package minesweeper.Game;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Sehnsucht
 */
public class Game {

    private final ArrayList<GameListener> gameListeners;

    private final int width, height, numberOfMines;

    private int[][] board;
    private int[][] revealed; // -1: not revealed, X: X adjacent mines

    public final int DEBUG_LEVEL = 0; // 0 = off, 1 = show heatmap, 2 = show heatmap + scores

    public Game(int width, int height, int numberOfMines) {
        this.gameListeners = new ArrayList<>();
        this.width = width;
        this.height = height;
        this.numberOfMines = numberOfMines;
        if(numberOfMines > width*height){
            System.out.println("Too many mines! Terminating.");
            System.exit(0);
        }
        newGame();
    }

    //Update game by 1 tick
    public void tick() {

        notifyListeners();
    }

    private void newGame() {
        this.board = new int[width][height];
        this.revealed = new int[width][height];
        
        //Place mines
        int minesPlaced = 0;
        while (minesPlaced < this.numberOfMines) {
            int x = ThreadLocalRandom.current().nextInt(0, width);
            int y = ThreadLocalRandom.current().nextInt(0, height);
            if (board[x][y] == 0) {
                board[x][y] = 1;
                minesPlaced++;
            }
        }
        System.out.println("Mines placed!");
    }
    
    public void revealBlock(int x, int y){
        revealed[x][y] = 1;
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

    public int[][] getBoard() {
        return this.board;
    }
    
    public int[][] getRevealed(){
        return revealed;
    }

}
