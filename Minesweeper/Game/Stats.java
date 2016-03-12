package Minesweeper.Game;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Sehnsucht
 */
public class Stats {

    private int historyLength, winnersAdded, totalWins, totalLosses;

    private final int[] winnerArray;
    private final int[] blocksLeftArray;

    public Stats(int historyLength) {
        this.historyLength = historyLength;
        this.winnersAdded = 0;
        winnerArray = new int[historyLength];
        blocksLeftArray = new int[historyLength];
    }

    //Winners are added at the back of the array (queue)
    public void saveWinner(int id, int minesLeft) {
        //Hack for minesweeper (cuz im lazy)
        if (id == 1) {
            totalWins++;
        } else {
            totalLosses++;
        }
        if (winnersAdded < historyLength) {
            winnersAdded++;
        }
        for (int i = 0; i < historyLength - 1; i++) {
            winnerArray[i] = winnerArray[i + 1];
            blocksLeftArray[i] = blocksLeftArray[i + 1];
        }
        winnerArray[historyLength - 1] = id;
        blocksLeftArray[historyLength - 1] = minesLeft;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public int getTotalLosses() {
        return totalLosses;
    }

    public void printTest() {
        for (int i = 0; i < historyLength; i++) {
            System.out.print(winnerArray[i] + " ");
        }
    }

    public HashMap<Integer, Integer> getWinnerCount() {
        HashMap<Integer, Integer> winners = new HashMap<>();
        for (int i = 0; i < historyLength; i++) {
            if (winnerArray[i] != 0) {
                winners.put(winnerArray[i], (winners.get(winnerArray[i]) == null ? 0 : winners.get(winnerArray[i])) + 1);
            }
        }
        return winners;
    }

    public HashMap<Integer, Double> getWinnerPercent() {
        HashMap<Integer, Integer> winnerCount = getWinnerCount();
        HashMap<Integer, Double> winnerPercent = new HashMap<>();

        //Iterate over winnerCount
        Set set = winnerCount.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry) iterator.next();
            winnerPercent.put((Integer) mentry.getKey(), (double) winnerCount.get(mentry.getKey()) / (double) winnersAdded);
        }

        return winnerPercent;
    }

    public int getHistoryLength() {
        return historyLength;
    }
    
    /**
     * 
     * @return avg number of blocks left last *history length*
     */
    public double getAverageMinesLeft(){
        int sum = 0;
        for (int i = 0; i < historyLength; i++) {
            sum += blocksLeftArray[i];
        }
        return (double)sum/historyLength;
    }
}
