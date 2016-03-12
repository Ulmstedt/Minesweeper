package Minesweeper.Game;

/**
 *
 * @author Sehnsucht
 */
public class Utils {

    public static int findHighestScore(int[][] pointGrid) {
        int highest = 1;
        for (int x = 0; x < pointGrid.length; x++) {
            for (int y = 0; y < pointGrid[0].length; y++) {
                if (pointGrid[x][y] > highest) {
                    highest = pointGrid[x][y];
                }
            }
        }
        return highest;
    }
}
