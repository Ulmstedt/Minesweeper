/**
 * Loki AI
 *
 * Loki.java
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
import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class Loki {
    public static final byte DRAW = 0;
    public static final byte LOSS = 1;
    public static final byte WIN = 2;
    public static final int NO_ID_FLIP = 0;

    private int[][] previousBoard;
    private int size;
    private int width, height;
    private ILokiDB lokiDB;
    private List<GameData> gameData = new ArrayList<>();
    private Random random = new Random();

    // Memory DB.
    public Loki(int width, int height, boolean aggressive) {
        size = width <= height ? width : height;
        this.width = width;
        this.height = height;
        MoveData.aggressive = aggressive;
        previousBoard = new int[height][width];

        lokiDB = new MemoryDB();
    }

    // Folder DB.
    public Loki(String folderPath, int width, int height, boolean aggressive) {
        size = width <= height ? width : height;
        this.width = width;
        this.height = height;
        MoveData.aggressive = aggressive;
        previousBoard = new int[height][width];

        try {
            lokiDB = new FolderDB(folderPath);
        } catch (IOException e) {
            lokiDB = new MemoryDB();
        }
    }

    // File DB.
    public Loki(String folderPath, String filename, int width, int height, boolean aggressive) {
        size = width <= height ? width : height;
        this.width = width;
        this.height = height;
        MoveData.aggressive = aggressive;
        previousBoard = new int[height][width];

        try {
            lokiDB = new FileDB(folderPath, filename);
        } catch (IOException e) {
            lokiDB = new MemoryDB();
        }
    }

    // SQL DB.
    public Loki(InetAddress address, int port, String database, String username, String password, int width, int height,
                boolean aggressive) {
        size = width <= height ? width : height;
        this.width = width;
        this.height = height;
        MoveData.aggressive = aggressive;
        previousBoard = new int[height][width];

        try {
            lokiDB = new SQLDB(address, port, database, username, password);
        } catch (SQLException e) {
            lokiDB = new MemoryDB();
        }
    }

    private ArrayList<MoveData> getMovesFromDB(int[][] board, int id, int searchWidth) {
        ArrayList<MoveData> data = new ArrayList<>();

        // Search for patterns in DB.
        for (int idFlip = 0; idFlip < 2; idFlip++) { // Flip id loop.
            for (int searchPatternMirror = 0; searchPatternMirror < 2; searchPatternMirror++) { // Mirror search pattern loop.
                for (int searchPatternRotation = 0; searchPatternRotation < 4; searchPatternRotation++) { // Rotate search pattern loop.
                    int startX = 0;
                    int endX = searchWidth - 1;
                    int startY = 0;
                    int endY = searchWidth - 1;
                    while (endY < height) {
                        while (endX < width) {
                            // Get, mirror and rotate search pattern before hashing.
                            int[][] searchPattern = Utils.getSearchPattern(board, startX, startY, endX, endY);
                            for (int i = 2 - searchPatternMirror; i < 2; i++) {
                                searchPattern = Utils.mirrorSearchPatternVertically(searchPattern);
                            }
                            for (int i = 4 - searchPatternRotation; i < 4; i++) {
                                searchPattern = Utils.rotateSearchPatternClockwise(searchPattern);
                            }

                            // Calculate hash.
                            String hash = Utils.calculateHash(searchPattern, idFlip, id);

                            // Get available moves for current hash from loki db and add do data if move is available.
                            ArrayList<MoveData> availableMoves = lokiDB.getAvailableMovesFromDB(hash, startX, startY,
                                    searchPatternMirror == 1, searchPatternRotation, searchWidth);
                            data.addAll(availableMoves.stream().collect(Collectors.toList()));

                            startX++;
                            endX++;
                        }

                        startX = 0;
                        endX = searchWidth - 1;
                        startY++;
                        endY++;
                    }
                }
            }
        }

        return data;
    }

    public void registerMoveInDB(int[][] board) {
        // Clone board.
        int[][] clonedBoard = Utils.cloneMatrix(board);

        // Get move by doing a board comparison.
        Point move = Utils.getMoveByBoardComparison(previousBoard, clonedBoard);

        // Get player ID.
        int id = clonedBoard[move.y][move.x];

        // Update previous board to equal current.
        previousBoard[move.y][move.x] = id;

        // Reverse recent move.
        clonedBoard[move.y][move.x] = 0;

        // Store previous board and the new move.
        gameData.add(new GameData(clonedBoard, move, id));
    }

    private void storeMovesInDB(GameData gd, int winnerID, int searchWidth) {
        int[][] board = gd.getBoard();
        Point move = gd.getMove();
        int id = gd.getID();

        // TODO: Search if exists with flips (will reduce data storage by factor 16).

        int startX = 0;
        int endX = searchWidth - 1;
        int startY = 0;
        int endY = searchWidth - 1;
        while (endY < height) {
            while (endX < width) {
                if (Utils.hasAdjecentMoveOrFullSize(board, startX, startY, endX, endY) &&
                        move.x >= startX && move.x <= endX && move.y >= startY && move.y <= endY) {
                    // Get search pattern and calculate hash.
                    int[][] searchPattern = Utils.getSearchPattern(board, startX, startY, endX, endY);
                    String hash = Utils.calculateHash(searchPattern, NO_ID_FLIP, id);

                    // Descale move to board.
                    Point descaledMove = new Point(move.x - startX, move.y - startY);

                    // Store data to database.
                    lokiDB.addToDB(hash, descaledMove, winnerID == DRAW ? DRAW : (winnerID == id ? WIN : LOSS));
                }

                startX++;
                endX++;
            }

            startX = 0;
            endX = searchWidth - 1;
            startY++;
            endY++;
        }
    }

    // The actual learning mechanism. Requires that all moves are registered with registerMoveInDB.
    public void storeRegisteredMovesInDB(int winnerID) {
        // Start time of start of storing process.
        System.out.println("Loki: Storing game data in DB...");
        long startTime = System.currentTimeMillis();

        // Store move data in DB
        for (GameData gd : gameData) {
            int searchWidth = size;
            while (searchWidth > 1) {
                storeMovesInDB(gd, winnerID, searchWidth);

                searchWidth--;
            }
        }

        // Clear previous board and all registered move data.
        previousBoard = new int[height][width];
        gameData.clear();

        // Signal DB that all data has been stored.
        lokiDB.addToDBDone();

        // End time of storing process.
        long endTime = System.currentTimeMillis();
        long timeSpent = endTime - startTime;
        System.out.println("Loki: Game data stored in " + timeSpent + " ms.");
    }

    public LokiResult thinkOfAMove(int[][] board, int id) {
        // Start time of AI's turn.
        System.out.println("Loki: Thinking out a move...");
        long startTime = System.currentTimeMillis();

        // Create new data hash for available moves found.
        Map<Point, MoveData> data = new HashMap<>();

        // Search for patterns and add statistics to data.
        int searchWidth = size;
        int searchLevelOfResult = 0;
        ArrayList<MoveData> resultData;
        boolean positiveSearchResultFound = false;
        while (!positiveSearchResultFound && searchWidth > 1) {
            // Clone board and consider a move.
            int[][] clonedBoard = Utils.cloneMatrix(board);
            resultData = getMovesFromDB(clonedBoard, id, searchWidth);

            if (resultData != null) {
                // Go through result data and add to data.
                for (MoveData md : resultData) {
                    Point move = md.getMove();
                    if (data.containsKey(move)) {
                        MoveData existingMoveData = data.get(move);

                        existingMoveData.addDraws(md.getDraws());
                        existingMoveData.addLosses(md.getLosses());
                        existingMoveData.addWins(md.getWins());
                    } else {
                        data.put(move, md);
                    }
                }

                // Check if any win chance, if not clear data and proceed to the next level.
                for (Point p : data.keySet()) {
                    if (data.get(p).thoughtResult() > 0) {
                        searchLevelOfResult = searchWidth;
                        positiveSearchResultFound = true;
                    }
                }
            }

            searchWidth--;
        }

        // Build board of conceived data.
        float bestThoughtResult = 0;
        float[][] thoughtData = new float[height][width];
        ArrayList<Point> bestMoves = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get result for given point.
                Point coord = new Point(x, y);
                float thoughtResult = 0;
                if (data.containsKey(coord)) {
                    thoughtResult = data.get(coord).thoughtResult();
                }
                thoughtData[y][x] = thoughtResult;

                // Check if better move.
                if (board[y][x] == 0) {
                    if (thoughtResult > bestThoughtResult) {
                        bestThoughtResult = thoughtResult;
                        bestMoves.clear();
                        bestMoves.add(coord);
                    } else if (thoughtResult == bestThoughtResult) {
                        bestMoves.add(coord);
                    }
                }
            }
        }

        System.out.println("Loki search level of results: " + searchLevelOfResult);
        System.out.println("Loki result percentage: " + bestThoughtResult);

        // Select the best move and store in returning LokiResult, if more then one then randomly select one of them.
        Point bestMove = bestMoves.get(random.nextInt(bestMoves.size()));
        LokiResult ret = new LokiResult(bestMove, thoughtData, width, height);

        // End time of AI's turn.
        long endTime = System.currentTimeMillis();
        long timeSpent = endTime - startTime;
        Point move = ret.getMove();
        System.out.println("Loki: Move " + move.x + ":" + move.y + " chosen in " + timeSpent + " ms.");

        return ret;
    }
}