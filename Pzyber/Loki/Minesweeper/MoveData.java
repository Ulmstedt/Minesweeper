/**
 * Loki AI
 *
 * MoveData.java
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

public class MoveData {
    protected static boolean aggressive = false;

    private long draws, losses, wins;
    private Point move;

    public MoveData(Point move, long draws, long losses, long wins) {
        this.move = move;
        this.draws = draws;
        this.losses = losses;
        this.wins = wins;
    }

    public void addDraws(long amount) {
        draws += amount;
    }

    public void addLosses(long amount) {
        losses += amount;
    }

    public void addWins(long amount) {
        wins += amount;
    }

    public float thoughtResult() {
        long total = draws + losses + wins;
        if (aggressive) {
            return (float) wins / (float) total;
        }
        return ((float) draws + (float) wins) / (float) total;
    }

    public long getDraws() {
        return draws;
    }

    public long getLosses() {
        return losses;
    }

    public Point getMove() {
        return new Point(move.x, move.y);
    }

    public long getWins() {
        return wins;
    }
}