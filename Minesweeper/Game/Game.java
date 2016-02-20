package minesweeper.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The minesweeper game.
 *
 * @author Sehnsucht
 */
public class Game {

    private final ArrayList<GameListener> gameListeners;

    private final int width, height, numberOfMines;

    private int[][] board;
    private int[][] revealed; // -1: not revealed, -2: flagged, X: X adjacent mines

    private boolean gameOver = false;

    public final int DEBUG_LEVEL = 0; // 0 = off, 1 = show heatmap, 2 = show heatmap + scores

    public Game(int width, int height, int numberOfMines) {
        this.gameListeners = new ArrayList<>();
        this.width = width;
        this.height = height;
        this.numberOfMines = numberOfMines;
        if (numberOfMines > width * height) {
            System.out.println("Too many mines! Terminating.");
            System.exit(0);
        }
        newGame();
    }

    /**
     * Update game by 1 tick
     */
    public void tick() {
        notifyListeners();
    }

    /**
     * Creates a new game board.
     */
    private void newGame() {
        board = new int[width][height];
        revealed = new int[width][height];
        for (int i = 0; i < revealed.length; i++) {
            Arrays.fill(revealed[i], -1);

        }
        this.gameOver = false;

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

    /**
     * Reveal a block and see if there is a mine. If no mine is found, count
     * adjacent mines and if 0 are found, recursively reveal adjacent blocks as
     * well.
     *
     * @param x x coordinate to reveal
     * @param y y coordinate to reveal
     */
    public void revealBlock(int x, int y) {
        if (board[x][y] == 1) {
            gameOver = true;
            System.out.println("Game over!");
            return;
        }
        int adjacentMines = 0;
        //Count adjacent mines
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if ((dx == x && dy == y) || x + dx < 0 || y + dy < 0 || x + dx >= width || y + dy >= height) {
                    continue;
                } else if (board[x + dx][y + dy] == 1) {
                    adjacentMines++;
                }
            }
        }
        revealed[x][y] = adjacentMines;

        //Reveal all adjacent blocks if there are no adjacent mines
        if (adjacentMines == 0) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == x && dy == y) {
                        continue;
                    } else if (!(x + dx < 0 || y + dy < 0 || x + dx >= width || y + dy >= height) && revealed[x + dx][y + dy] < 0) {
                        revealBlock(x + dx, y + dy);
                    }
                }
            }
        }
        System.out.println("Revealed (" + x + ", " + y + ")");
    }

    /**
     * Marks a block with a flag, question mark or unmarks (cycles)
     *
     * @param x x to mark
     * @param y y to mark
     */
    public void markBlock(int x, int y) {
        if (revealed[x][y] == -1) {
            revealed[x][y] = -2;
        } else if (revealed[x][y] == -2) {
            revealed[x][y] = -3;
        } else if (revealed[x][y] == -3) {
            revealed[x][y] = -1;
        }
        System.out.println("Marked (" + x + ", " + y + ")");
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

    public int[][] getRevealed() {
        return revealed;
    }

    public boolean getGameOver() {
        return gameOver;
    }

}
