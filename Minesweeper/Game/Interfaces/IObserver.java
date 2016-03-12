package Minesweeper.Game.Interfaces;

/**
 *
 * @author Sehnsucht
 */
public interface IObserver {
    public void gameEnded(boolean win, int blocksRevealed, int blocksLeft);
}
