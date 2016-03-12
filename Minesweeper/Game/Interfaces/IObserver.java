package Minesweeper.Game.Interfaces;

import Minesweeper.Game.Move;

/**
 *
 * @author Sehnsucht
 */
public interface IObserver {
    public void gameEnded(boolean win, int blocksRevealed, int blocksLeft);
    public void moveMade(Move move);
}
