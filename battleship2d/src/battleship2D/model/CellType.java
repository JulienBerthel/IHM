package battleship2D.model;

/**
 * Cell Types 
 * @author xaviator
 */
public enum CellType{ 
    /*=========================================================================*/
    /* Members                                                                 */       
    /*=========================================================================*/
    
    AVAILABLE_LOCATION("Available", "-fx-background-color: aquamarine"), // the cell is available to receive a (piece of) ship,
    BATTLESHIP(ShipType.BATTLESHIP),
    CARRIER(ShipType.CARRIER),
    CRUISER(ShipType.CRUISER),
    DESTROYER(ShipType.DESTROYER),
    HIT("Hit", "-fx-background-image: url(\"battleship2D/pictures/ship-explosion.jpg\")"), // a piece of ship has been hit by a foe missile
    OCEAN("Ocean", "-fx-background-image: url(\"battleship2D/pictures/ocean.jpeg\")"), // nothing on this tile    
    SUBMARINE(ShipType.SUBMARINE),   
    UNKNOWN("Unknown", "-fx-background-color: black"); // the cell is hidden by some "fog of war"

    /** Rendering either as a color or an image */
    private final String appearance;
    
    /** Short description */    
    private final String description;
    
        
    /*=========================================================================*/
    /* Public methods                                                          */       
    /*=========================================================================*/
    
    /**
     * Check whether this CellType is a ship
     * @return true if this is a ship, false otherwise
     */
    public boolean isAShip() {
        return (this == CellType.BATTLESHIP || this == CellType.CARRIER
                || this == CellType.CRUISER || this == CellType.DESTROYER
                || this == CellType.SUBMARINE);
    }
    
    /**
     * Converts a CellType into a ShipType
     * @param cellType - cell type to deal with
     * @return the matching ship type
     */
    public static ShipType cellTypeToShipType(CellType cellType) {
        switch (cellType) {
            case BATTLESHIP:
                return ShipType.BATTLESHIP;
            case CARRIER:
                return ShipType.CARRIER;
            case CRUISER:
                return ShipType.CRUISER;
            case DESTROYER:
                return ShipType.DESTROYER;
            case SUBMARINE:
                return ShipType.SUBMARINE;
            default:
                return null;
        }
    }
    
    /**
     * Converts a ShipType into a CellType
     * @param shipType - the ship type to convert
     * @return the matching cell type
     */
    public static CellType shipTypeToCellType(ShipType shipType) {
        switch (shipType) {
            case BATTLESHIP:
                return CellType.BATTLESHIP;
            case CARRIER:
                return CellType.CARRIER;
            case CRUISER:
                return CellType.CRUISER;
            case DESTROYER:
                return CellType.DESTROYER;
            case SUBMARINE:
                return CellType.SUBMARINE;
            default:
                return null;
        }
    }
    
    /*
     * Getters / Setters     
     */
    
    public String getDescription() {
        return this.description;    
    }
    
    public String getAppearance() {
        return this.appearance;
    }

    
    /*=========================================================================*/
    /* Private methods                                                         */       
    /*=========================================================================*/
    
    /**
     * Constructor
     * @param description - short description
     * @param appearance - related appearance
     */
    private CellType (final String description, final String appearance) {
        this.description = description;
        this.appearance = appearance;
    }

    /**
     * Constructor
     * @param description - short description
     * @param appearance - related appearance
     */
    private CellType (ShipType shipType) {
        this.description = shipType.getDescription();
        this.appearance = shipType.getAppearance();
    }
}