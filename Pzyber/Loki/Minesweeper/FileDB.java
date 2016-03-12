/**
 * Loki AI
 *
 * FileDB.java
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
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileDB implements ILokiDB {

    public static final byte DRAW = 0;
    public static final byte LOSS = 1;
    public static final byte WIN = 2;
    public static final byte DRAWS = 0;
    public static final byte LOSSES = 1;
    public static final byte WINS = 2;

    private FileSystem fs;
    private Map<String, String> env = new HashMap<>();
    private String folderPath;
    private URI uri;

    public FileDB(String folderPath, String filename) throws IOException {
        this.folderPath = folderPath;

        // Init and open zip-file.
        env.put("create", "true");
        Path p = Paths.get(folderPath + "/" + filename);
        uri = URI.create("jar:" + p.toUri());
        Files.createDirectories(Paths.get(folderPath));
        fs = FileSystems.newFileSystem(uri, env);
    }

    @Override
    public void addToDB(String hash, Point move, byte result) {
        String filePath = hash + "/" + move.x + "_" + move.y + ".txt";
        if (Files.exists(fs.getPath(folderPath + "/" + hash))) {
            if (Files.exists(fs.getPath(filePath))) {
                // Get draws, losses and wins.
                long[] dbData = readDataFromDBFile(fs, filePath);

                // Write data to file.
                writeDataToDBFile(fs, filePath, result, dbData[DRAWS], dbData[LOSSES], dbData[WINS]);
            } else {
                // Write data to file.
                writeDataToDBFile(fs, filePath, result, 0, 0, 0);
            }
        } else {
            // Create hashed folder and write data to file.
            try {
                Files.createDirectories(fs.getPath(hash));
            } catch (IOException ignored) {
            }
            writeDataToDBFile(fs, filePath, result, 0, 0, 0);
        }
    }

    @Override
    public void addToDBDone() {
        reopenZipFile();
    }

    @Override
    public ArrayList<MoveData> getAvailableMovesFromDB(String hash, int startX, int startY, boolean mirror,
                                                       int rotations, int size) {
        ArrayList<MoveData> availableMoves = new ArrayList<>();

        if (Files.exists(fs.getPath(hash))) {
            try {
                // Get list of available moves from Loki DB.
                Files.walk(fs.getPath(hash)).forEach(filePath -> {
                    if (Files.isRegularFile(filePath)) {
                        String m = filePath.getFileName().toString();

                        // Get inner move.
                        String[] posDot = m.split("\\.");
                        String[] pos = posDot[0].split("_");
                        Point move = new Point(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]));

                        // De-rotate, de-mirror and de-scale move.
                        move = Utils.scaleMirrorAndRotate(move, startX, startY, mirror, rotations, size);

                        // Get draws, losses and wins.
                        long[] dbData = readDataFromDBFile(fs, hash + "/" + m);

                        // Add to available moves.
                        availableMoves.add(new MoveData(move, dbData[DRAWS], dbData[LOSSES], dbData[WINS]));
                    }
                });
            } catch (IOException ignored) {
            }
        }

        return availableMoves;
    }

    private long[] readDataFromDBFile(FileSystem fs, String filePath) {
        // Get draws, losses and wins.
        long dbData[] = {0, 0, 0};
        try {
            List<String> rows = Files.readAllLines(fs.getPath(filePath), StandardCharsets.UTF_8);
            dbData[DRAWS] = Long.parseLong(rows.get(DRAWS));
            dbData[LOSSES] = Long.parseLong(rows.get(LOSSES));
            dbData[WINS] = Long.parseLong(rows.get(WINS));

        } catch (IOException ignored) {
        }

        return new long[]{dbData[DRAWS], dbData[LOSSES], dbData[WINS]};
    }

    private void reopenZipFile() {
        // Close and reopen zip-file.
        try {
            fs.close();
            this.fs = FileSystems.newFileSystem(uri, env);
        } catch (IOException ignored) {
        }
    }

    // Write data to file.
    private void writeDataToDBFile(FileSystem fs, String filePath, byte result, long previousDraws, long previousLosses,
                                   long previousWins) {
        try {
            BufferedWriter bw = Files.newBufferedWriter(fs.getPath(filePath), StandardCharsets.UTF_8, StandardOpenOption.CREATE);

            bw.write(Long.toString((result == DRAW ? 1 : 0) + previousDraws));    // Draw.
            bw.newLine();
            bw.write(Long.toString((result == LOSS ? 1 : 0) + previousLosses));   // Losses.
            bw.newLine();
            bw.write(Long.toString((result == WIN ? 1 : 0) + previousWins));   // Wins.
            bw.flush();

            bw.close();
        } catch (IOException ignored) {
        }
    }
}