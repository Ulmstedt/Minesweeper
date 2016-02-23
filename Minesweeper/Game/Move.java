package Minesweeper.Game;

import Minesweeper.Game.Enums.MoveType;

/**
 *
 * @author Sehnsucht
 */
public class Move {

    public int x, y;
    public MoveType moveType;

    public Move(int x, int y, MoveType moveType) {
        this.x = x;
        this.y = y;
        this.moveType = moveType;
    }

}
