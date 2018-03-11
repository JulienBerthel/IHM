package battleship2D.model;

/**
 * Ship types
 * @author xaviator
 */
public enum ShipType {
    /*=========================================================================*/
    /* Members                                                                 */       
    /*=========================================================================*/
    
    BATTLESHIP("Battleship", "-fx-background-image: url(\"battleship2D/pictures/battleship.jpg\")"),
    CARRIER("Carrier", "-fx-background-image: url(\"battleship2D/pictures/carrier.jpg\")"),
    CRUISER("Cruiser", "-fx-background-image: url(\"battleship2D/pictures/cruiser.jpg\")"),
    DESTROYER("Destroyer", "-fx-background-image: url(\"battleship2D/pictures/destroyer.jpg\")"),
    SUBMARINE("Submarine", "-fx-background-image: url(\"battleship2D/pictures/submarine.jpeg\")");

    /** Rendering either as a color or an image */
    private final String appearance;

    /** Short description */    
    private final String description;
    
    
    /*=========================================================================*/
    /* Public methods                                                          */       
    /*=========================================================================*/
    
    /*
     * Getters / Setters     
     */
    
    /**
     * @return the description field
     */
    public String getDescription() {
        return this.description;    
    }
    
    /**
     * 
     * @return the appearance field
     */
    public String getAppearance() {
        return this.appearance;
    }
    
    
    /*=========================================================================*/
    /* Private methods                                                         */       
    /*=========================================================================*/
    
    /**
     * Constructor
     * @param description - short description
     * @param color - related color
     */
    private ShipType (final String description, final String appearance) {
        this.description = description;
        this.appearance = appearance;
    }
}
