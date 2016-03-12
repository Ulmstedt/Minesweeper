/**
 * Loki AI
 *
 * MemoryDB.java
 * Created on 2015-12-30
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
import java.util.HashMap;
import java.util.Map;

public class MemoryDB implements ILokiDB {
    public static final boolean LOSS = false;
    public static final boolean WIN = true;
    public static final byte LOSSES = 0;
    public static final byte WINS = 1;

    private Map<String, Map<Point, long[]>> db;

    public MemoryDB() {
        db = new HashMap<>();
    }

    @Override
    public void addToDB(String hash, Point move, boolean result) {
        if (db.containsKey(hash)) {
            // Get inner database.
            Map<Point, long[]> dbInner = db.get(hash);

            if (dbInner.containsKey(move)) {
                // Get draws, losses and wins.
                long[] dbData = dbInner.get(move);
                dbData[LOSSES] = dbData[LOSSES] + (result == LOSS ? 1 : 0);
                dbData[WINS] = dbData[WINS] + (result == WIN ? 1 : 0);

                // Store with new stats.
                dbInner.remove(move);
                dbInner.put(move, dbData);
            } else {
                // Set draws, losses and wins.
                long[] dbData = new long[]{(result == LOSS ? +1 : 0), (result == WIN ? 1 : 0)};

                // Store stats.
                dbInner.put(move, dbData);
            }
        } else {
            // Set draws, losses and wins.
            long[] dbData = new long[]{(result == LOSS ? +1 : 0), (result == WIN ? 1 : 0)};

            // Create inner database.
            Map<Point, long[]> dbInner = new HashMap<>();

            // Store stats.
            dbInner.put(move, dbData);

            // Store inner database..
            db.put(hash, dbInner);
        }
    }

    @Override
    public void addToDBDone() {

    }

    @Override
    public ArrayList<MoveData> getAvailableMovesFromDB(String hash, int startX, int startY, boolean mirror,
                                                       int rotations, int size) {
        ArrayList<MoveData> availableMoves = new ArrayList<>();

        if (db.containsKey(hash)) {
            // Get inner database.
            Map<Point, long[]> dbInner = db.get(hash);

            // Get available moves from Loki DB.
            for (Map.Entry<Point, long[]> entry : dbInner.entrySet()) {
                Point move = entry.getKey();
                long[] dbData = entry.getValue();

                // De-rotate, de-mirror and de-scale move.
                move = Utils.scaleMirrorAndRotate(move, startX, startY, mirror, rotations, size);

                // Add to available moves.
                availableMoves.add(new MoveData(move, dbData[LOSSES], dbData[WINS]));
            }
        }

        return availableMoves;
    }
}