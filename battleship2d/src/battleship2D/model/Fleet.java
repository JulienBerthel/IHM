package battleship2D.model;

import java.util.ArrayList;

/**
 * Fleet of Ships
 * @author xaviator
 */
public class Fleet {
    /*=========================================================================*/
    /* Members                                                                 */       
    /*=========================================================================*/
    
    /** Checks whether the whole fleet has been destroyed */
    private Boolean fleetDestroyed = false;
    
    /** Checks whether the last hit ship has been entirely destroyed */
    private Boolean lastHitShipDestroyed = false;
    
    /** Counts ships parts which have not been hit  */
    private final int[] remainingParts;
    
    /** Count still remaining ships */
    private int remainingShips;
            
    /** A fleet is made of different ships */
    private final ArrayList<Ship> ships;
    
    
    /*=========================================================================*/
    /* Public methods                                                          */       
    /*=========================================================================*/
    
    /**
     * Constructor
     */
    public Fleet() {
        this.ships = new ArrayList<>();
        initShips();
        
        this.remainingParts = new int[this.ships.size()];
        initRemainingParts();
        
        this.remainingShips = this.ships.size();
    }
    
    /**
     * @param shipType - type of the ship to find
     * @return the ship matching shiptype, null otherwise
     */
    public Ship findShipFromType(ShipType shipType) {
        for (Ship ship : this.ships) {
            if (ship.getShipType() == shipType) {
                return ship;
            }
        }
        return null;
    }
    
    /**
     * @return the number of ships in the fleet
     */
    public int numberOfShips() {
        return this.ships.size();
    }
    
    /**
     * Updates the number of ship parts that have been hit
     * @param shipType - type of the ship to deal with
     */
    public void updateHits(ShipType shipType) {
        Ship lastHitShip = findShipFromType(shipType);
        
        if (lastHitShip != null) {
            int shipIndex = this.ships.indexOf(lastHitShip);
            this.remainingParts[shipIndex] --;            

            /* Is the ship completely terminated? */
            if (this.remainingParts[shipIndex] == 0) {                
                updateRemaininghips();                        
            }   
            else {
                this.lastHitShipDestroyed = false;
            }
        }
    }
    
    /*
     * Getters / setters
     */
   
    public ArrayList<Ship> getShips() {
        return this.ships;
    }
    
    public Boolean isFleetDestroyed() {
        return this.fleetDestroyed;
    }
    
    public Boolean isLastHitShipDestroyed() {
        return this.lastHitShipDestroyed;
    }
    
    /*=========================================================================*/
    /* Private methods                                                         */       
    /*=========================================================================*/
    
    /**
     * All ships are unscathed at first
     * @see Fleet()
     */
    private void initRemainingParts() {
        for (int i = 0; i < this.remainingParts.length; i++) {
            this.remainingParts[i] = this.ships.get(i).getSize();
        }
    }
    
    /**
     * Fills up the collection of ships
     * @see Fleet()
     */
    private void initShips() {         
        this.ships.add(new Ship(ShipType.CARRIER));
        this.ships.add(new Ship(ShipType.BATTLESHIP));
        this.ships.add(new Ship(ShipType.CRUISER));
        this.ships.add(new Ship(ShipType.DESTROYER));
        this.ships.add(new Ship(ShipType.SUBMARINE));
    }
    
    /**
     * Updates fleet counters after a hit
     * @see updateHits()
     */
    private void updateRemaininghips() {
        this.remainingShips --;
        this.lastHitShipDestroyed = true;
     
        if (this.remainingShips == 0) {
            this.fleetDestroyed = true;
        }   
    }
}
