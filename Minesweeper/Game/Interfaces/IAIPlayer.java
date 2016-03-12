package Minesweeper.Game.Interfaces;

import Minesweeper.Game.Move;

/**
 *
 * @author Sehnsucht
 */
public interface IAIPlayer {
    public Move makeMove();
    public int[][] getPointGrid();
}
