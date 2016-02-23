package Minesweeper.Game.Enums;

/**
 *
 * @author Sehnsucht
 */
public enum MoveType {

    REVEAL, //Reveal block
    FLAG,  //Flag block (if it is believed to be a bomb)
    QUESTIONMARK, //Mark block as uncertain
    CLEAR //Clear flag/questionmark
}
