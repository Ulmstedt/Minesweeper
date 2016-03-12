/**
 * Loki AI
 *
 * ILokiDB.java
 * Created on 2015-12-31
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
import java.util.ArrayList;

public interface ILokiDB {
    void addToDB(String hash, Point move, boolean result);

    void addToDBDone();

    ArrayList<MoveData> getAvailableMovesFromDB(String hash, int startX, int startY, boolean mirror, int rotations,
                                                int size);
}
