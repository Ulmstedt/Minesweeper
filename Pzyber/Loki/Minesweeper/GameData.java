/**
 * Loki AI
 *
 * GameData.java
 * Created on 2015-12-28
 * Version 0.7.0 Beta
 *
 * Written by Jimmy Nordström.
 * © 2015-2016 Jimmy Nordström.
 *
 * Licenced under GNU GPLv3.
 * http://www.gnu.org/licenses/gpl-3.0.html
 *
 * If you have questions, contact me at pzyber@pzyber.net
 */

package Pzyber.Loki.Minesweeper;

import java.awt.Point;

public class GameData {
    private int[][] board;
    private Point move;

    public GameData(int[][] board, Point move) {
        this.board = board;
        this.move = move;
    }

    public int[][] getBoard() {
        return Utils.cloneMatrix(board);
    }

    public Point getMove() {
        return new Point(move.x, move.y);
    }
}
