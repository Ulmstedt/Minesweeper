/**
 * Loki AI
 *
 * LokiResult.java
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

public class LokiResult {
    private float[][] calculatedData;
    private int width, height;
    private Point move;

    public LokiResult(Point move, float[][] calculatedData, int width, int height) {
        this.move = move;
        this.calculatedData = calculatedData;
        this.width = width;
        this.height = height;
    }

    public float[][] getCalculatedData() {
        return calculatedData;
    }

    public int[][] getCalculatedDataRounded() {
        return getCalculatedDataRoundedScaled(0);
    }

    public int[][] getCalculatedDataRoundedScaled(int scale) {
        int[][] calculatedDataRounded = new int[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                calculatedDataRounded[y][x] = Math.round(calculatedData[y][x] * scale);
            }
        }
        return calculatedDataRounded;
    }

    public Point getMove() {
        return move;
    }
}
