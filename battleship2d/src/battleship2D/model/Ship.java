package battleship2D.model;

/**
 * Ship representation on the board
 * A ship is made of tiles aligned along either the X-axis or the Y-axis
 * @author xaviator
 */
public class Ship {
    /*=========================================================================*/
    /* Members                                                                 */       
    /*=========================================================================*/
    
    /** Short description*/
    private String description;
    
    /** Ship's type */
    private final ShipType shipType;
    
    /** Ship's size in cells */
    private int size;
    
    
    /*=========================================================================*/
    /* Public methods                                                          */       
    /*=========================================================================*/
    
    /**
     * Constructor
     * @param shipType ship's type
     */
    public Ship (ShipType shipType) {
        this.shipType = shipType;  
        initShip();
    }

    
    /* 
     * Getters / Setters 
     */
    
    public int getSize() {
        return this.size;
    }
    
    public ShipType getShipType() {
        return this.shipType;       
    }
    
    public String getDescription() {
        return this.description;
    }
    
    /*=========================================================================*/
    /* Private methods                                                         */       
    /*=========================================================================*/
    
    /**
     * Sets Ship's characteristics
     * @see Ship()
     */
    private void initShip() {
        switch (this.shipType) {
            case CARRIER:
                this.size = 5;
                break;
            case BATTLESHIP:
                this.size = 4;
                break;
            case CRUISER:
                this.size = 3;
                break;
            case DESTROYER:
                this.size = 2;
                break;
            case SUBMARINE:
                this.size = 1;
                break;
            default:
                throw new AssertionError(this.shipType.name());            
        }
        this.description  = this.shipType + " (" + size + ")";
    }
    
}

