package battleship2D.model;

/**
 * Directions along which ships are placed
 * @author xaviator
 */
public enum Direction {
    /*=========================================================================*/
    /* Members                                                                 */       
    /*=========================================================================*/
    
    NORTH,
    WEST, 
    SOUTH, 
    EAST;
    
    /*=========================================================================*/
    /* Public methods                                                          */       
    /*=========================================================================*/
    
    /**
     * @return the opposite direction of a given direction 
     * @param direction - direction to deal with
     */
    public static Direction opposite(Direction direction) {
        Direction oppositeDirection = NORTH;
        switch(direction) {
            case NORTH: 
                oppositeDirection = SOUTH;
                break;
                
            case SOUTH: 
                oppositeDirection = NORTH;
                break;
                
            case WEST: 
                oppositeDirection = EAST;
                break;
                
            case EAST: 
                oppositeDirection = WEST;
                break;
        }
        return oppositeDirection;
    }
}
