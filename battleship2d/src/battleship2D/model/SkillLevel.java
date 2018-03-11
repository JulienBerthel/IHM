package battleship2D.model;

/**
 * Determines the computer skill level
 * @author xskapin
 */
public enum SkillLevel {
    /*=========================================================================*/
    /* Members                                                                 */
    /*=========================================================================*/

    BEGINNER, /* The computer only remembers the player's cells already hit by a missile */
    MEDIUM,   /* The computer targets successive cells in a row or a column, starting with a ship hit by a missile */
    EXPERT    /* The computers searches for the regions that have the maximum likelihood to contain a ship */
}
