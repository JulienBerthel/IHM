
package battleship2D.ui;

/** 
 * Game states 
 */
public enum GameStages {
    /*=========================================================================*/
    /* Members                                                                 */       
    /*=========================================================================*/
    
    PLACE_SHIPS_ON_PLAYER_BOARD, /* Lets the user place its ships on the board */
    INIT_COMPUTER_BOARD,  /* Shows the computer board */
    PLAY /* Both player and computer target their foes and launch missiles each other in turn */
}
