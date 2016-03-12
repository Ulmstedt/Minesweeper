/**
 * Loki AI
 *
 * FolderDB.java
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FolderDB implements ILokiDB {
    public static final boolean LOSS = false;
    public static final boolean WIN = true;
    public static final byte LOSSES = 0;
    public static final byte WINS = 1;

    private String path;

    public FolderDB(String path) throws IOException {
        this.path = path;

        Files.createDirectories(Paths.get(path));
    }

    @Override
    public void addToDB(String hash, Point move, boolean result) {
        String filePath = path + "/" + hash + "/" + move.x + "_" + move.y + ".txt";
        if (Files.exists(Paths.get(path + "/" + hash))) {
            if (Files.exists(Paths.get(filePath))) {
                // Get draws, losses and wins.
                long[] dbData = readDataFromDBFile(filePath);

                // Write data to file.
                writeDataToDBFile(filePath, result, dbData[LOSSES], dbData[WINS]);
            } else {
                // Write data to file.
                writeDataToDBFile(filePath, result, 0, 0);
            }
        } else {
            // Create hashed folder and write data to file..
            File hashedFolder = new File(path + "/" + hash);
            if (hashedFolder.mkdirs()) {
                writeDataToDBFile(filePath, result, 0, 0);
            }
        }
    }

    @Override
    public void addToDBDone() {

    }

    @Override
    public ArrayList<MoveData> getAvailableMovesFromDB(String hash, int startX, int startY, boolean mirror,
                                                       int rotations, int size) {
        ArrayList<MoveData> availableMoves = new ArrayList<>();

        String baseHashPath = path + "/" + hash;
        if (Files.exists(Paths.get(baseHashPath))) {
            // Get list of available moves from Loki DB.
            File lokiDB = new File(baseHashPath);
            String[] availableMovesDB = lokiDB.list();
            if (availableMovesDB != null) {
                for (String m : availableMovesDB) {
                    // Get inner move.
                    String[] posDot = m.split("\\.");
                    String[] pos = posDot[0].split("_");
                    Point move = new Point(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]));

                    // De-rotate, de-mirror and de-scale move.
                    move = Utils.scaleMirrorAndRotate(move, startX, startY, mirror, rotations, size);

                    // Get draws, losses and wins.
                    long[] dbData = readDataFromDBFile(baseHashPath + "/" + m);

                    // Add to available moves.
                    availableMoves.add(new MoveData(move, dbData[LOSSES], dbData[WINS]));
                }
            }
        }

        return availableMoves;
    }

    private long[] readDataFromDBFile(String path) {
        // Get draws, losses and wins.
        long dbData[] = {0, 0, 0};
        try {
            List<String> rows = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
            dbData[LOSSES] = Long.parseLong(rows.get(LOSSES));
            dbData[WINS] = Long.parseLong(rows.get(WINS));

        } catch (IOException ignored) {
        }

        return new long[]{dbData[LOSSES], dbData[WINS]};
    }

    // Write data to file.
    private void writeDataToDBFile(String path, boolean result, long previousLosses,
                                   long previousWins) {
        try {
            FileWriter fw = new FileWriter(path, false);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(Long.toString((result == LOSS ? 1 : 0) + previousLosses));   // Losses.
            bw.newLine();
            bw.write(Long.toString((result == WIN ? 1 : 0) + previousWins));   // Wins.
            bw.flush();

            bw.close();
            fw.close();
        } catch (IOException ignored) {
        }
    }
}
