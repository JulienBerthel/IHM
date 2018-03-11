package battleship2D.ui;

import battleship2D.model.BoardModel;
import battleship2D.model.CellModel;
import battleship2D.model.CellType;
import battleship2D.model.Coord2D;
import battleship2D.model.Direction;
import battleship2D.model.Fleet;
import battleship2D.model.Ship;
import battleship2D.model.SkillLevel;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * BoardUI dedicated to computer side
 * @author xaviator
 */
public class BoardUIComputer extends BoardUI {
    /*=========================================================================*/
    /* Members                                                                 */
    /*=========================================================================*/
    
    /** Store adverse ship information to improve the performance of future targets search 
     * Each string represents the ship's name, associated with its size as an Integer.
     * This HasMap is updated each time an adverse ship is destroyed.
     */
    private final HashMap<String, Integer> adverseShipInformation;
    
    /** Store a selection of cells for future targets 
     * Each CellModel is associated with an Integer representing its score for selection as the next actual target
     */
    private final HashMap<CellModel,Integer> futureTargets;
    
    /** Store a copy the last targeted cell in the player board */
    private CellModel lastCellTargeted;
    
    /** Store moves played against the player */
    private final BoardModel playerBoardModelCopy;
    
    /** Skill level for the computer */
    private final SkillLevel skillLevel;
    
    /*=========================================================================*/
    /* Public methods                                                          */       
    /*=========================================================================*/
    
    /**
     * Constructor
     * @param name - This board's name
     * @param boardModel - related model
     * @param isBound - determines whether CellUI's styles are bound to cell models'
     * @param skillLevel - level associated with the computer
     */
    public BoardUIComputer(String name, BoardModel boardModel, Boolean isBound,
            SkillLevel skillLevel) {
        super(name, boardModel, isBound);
        
        this.futureTargets = new HashMap<>();
        this.lastCellTargeted = null;
        this.skillLevel = skillLevel;
        
        /* At first, every cell of the player board copy is unknown. */
        this.playerBoardModelCopy = new BoardModel(CellType.UNKNOWN);
                
        this.adverseShipInformation = new HashMap<>();
        initAdverseShipInformation();
        
        initCellUIListener();
    }
    
    /**
     * Computes the location of the player cell targeted by a missile
     * The method to find this cell depends on the computer skill level
     * @return - thecell coordinates
     */
    public Coord2D findMissileDestinationCell() {
        Coord2D coord2D = null;
        
        switch (this.skillLevel) {
            case BEGINNER:
                coord2D = findMissileDestinationCellBeginner();
                break;
                
            case MEDIUM:
                coord2D = findMissileDestinationCellMedium();
                break;
                
            case EXPERT:
                coord2D = findMissileDestinationCellExpert();
                break;            
        }
        return coord2D;
    }
    
    
    /**
     * Automatically places a whole fleet of ships on this board,
     * at random locations
     */
    public void placeShipsOnBoardAtRandom() {
        CellModel cellModel;
        int shipSize;
        ArrayList<CellModel> randomCellSpan;
        Boolean nextShip;
        Boolean validLocation;
        
        for (Ship ship : this.boardModel.getFleet().getShips()) {
            nextShip = false;            
            
            while (! nextShip) {
                /* Chooses both random free cell and direction */                
                cellModel = this.boardModel.randomCell(this.boardModel.getDefaultCellType(), true);
                Direction direction = randomDirection();
                shipSize = ship.getSize();
                randomCellSpan = cellSpan(cellModel, direction, shipSize);
                
                /* If there were not enough room to fill randomCellSpan, try again. */
                if (randomCellSpan.isEmpty()) {
                    continue;
                }
                
                /* Checks whether all cells in randomCellSpan are available for placing the ship */
                validLocation = true;
                for (CellModel currentCell : randomCellSpan) {            
                    validLocation = ((currentCell.getCellType() == this.boardModel.getDefaultCellType()) || 
                        (currentCell.getCellType() == CellType.AVAILABLE_LOCATION));
                    if (! validLocation) {
                        break;
                    }
                }
        
                /* Places the ship on the board */
                if (validLocation) {           
                    CellType cellType = CellType.shipTypeToCellType(ship.getShipType());
                    
                    for (CellModel currentCell : randomCellSpan) {            
                       currentCell.setCellType(cellType);
                    }                    
                    nextShip = true;
                }
                else {
                    nextShip = false;
                }
            }
        }    
    }

    /**
     * Each time an adverse (player) ship has been destroyed, the number
     * of target ships decreases
     * @param shipDescription - name of the last adverse destroyed ship
     */
    public void updateInfoAboutAdverseDestroyedShip(String shipDescription) {
        if (this.adverseShipInformation.containsKey(shipDescription)) {
            this.adverseShipInformation.remove(shipDescription);
        }
    }
    
    /*
     * Getters / Setters
     */
    
    public CellModel getLastCellTargeted() {
        return this.lastCellTargeted;
    }
    
    /*=========================================================================*/
    /* Protected methods                                                         */       
    /*=========================================================================*/
    
    /**
     * Manages notification sent by CellUIs
     * @param propertyChangeEvent - event to deal with
     */  
    @Override
    protected void initSpecificCellUIListener(PropertyChangeEvent propertyChangeEvent) {
        String property = propertyChangeEvent.getPropertyName();
        this.missileDestination = (CellUI) propertyChangeEvent.getSource();

        /* Propagates an event triggered by a CellUI */
        if ("cellUIUnknownOrShip".equals(property)) {
            this.pcsListeners.firePropertyChange("boardUIComputerUnknownOrShip", null, this);
        }   
    }

    
    /*=========================================================================*/
    /* Private methods                                                         */       
    /*=========================================================================*/
    
    /**
     * Create and fill a set of cells amongst which the next missile future target
     * will be selected, in the medium skill level.
     * A cell is inserted in the set if it is at the center of a span 
     * (horizontal and/or vertical) of UNKNOWN cells
     * @param size - size of the span
     * @pre size > 1
     * @return the set of cells
     * @see findMissileDestinationCellMedium()
     */
    private ArrayList<CellModel> collectCandidatesForFutureTarget(int size) {
        ArrayList<CellModel> candidateCells = new ArrayList<>();
        
        /* Each cell of the copy board is scanned */
        for (int row = 0; row < BoardModel.BOARD_SIZE; row++) {
            for (int column = 0; column < BoardModel.BOARD_SIZE; column++) {
                CellModel cellModel = this.playerBoardModelCopy.getCellModel(row, column);
                if (cellModel.getCellType() == CellType.UNKNOWN) {
                    if (IsCellCenterOfUnknownSpan(cellModel, size)) {
                        candidateCells.add(cellModel);
                    }
                }   
            }     
        }
        return candidateCells;
    }
    
    /**
     * Fill a set with cells adjacent (along cardinal directions) to a specific cell
     * @param cellModel - cell centering adjacent cells
     * @return the set of adjacent cells
     * @see findMissileDestinationCellExpert()
     */
    private ArrayList<CellModel> findAdjacentCells(CellModel cellModel) {
        ArrayList<CellModel> adjacentCellsList = new ArrayList<CellModel>();
        adjacentCellsList.add(this.playerBoardModelCopy.adjacentCell(cellModel, Direction.NORTH));
        adjacentCellsList.add(this.playerBoardModelCopy.adjacentCell(cellModel, Direction.WEST));
        adjacentCellsList.add(this.playerBoardModelCopy.adjacentCell(cellModel, Direction.SOUTH));
        adjacentCellsList.add(this.playerBoardModelCopy.adjacentCell(cellModel, Direction.EAST));

        return adjacentCellsList;
    }
    
    
    /**
     * Computes the location of the player cell targeted by a missile
     * The computation is done at beginner level: The computer only searches
     * for a random location which has not already been visited
     * 
     * @return - the cell coordinates of the next cell to target
     * @see findMissileDestinationCell()
     */
    private Coord2D findMissileDestinationCellBeginner() {
        /* A non-visited cell is tagged as UNKNOWN */
        CellModel cellModel = this.playerBoardModelCopy.randomCell(CellType.UNKNOWN, Boolean.TRUE);
        if (cellModel != null) {
            this.lastCellTargeted = cellModel; 
            return this.playerBoardModelCopy.cellCoords(this.lastCellTargeted);
        }
        else {
            System.err.println("BoardUIComputer::findMissileDestinationCellBeginner: cellModel = null");
            return null;
        }
    }    
     
    /**
     * Computes the location of the player cell targeted by a missile
     * The computation is done at expert level: once a ship has been hit,
     * the next missile is targeted to an adjacent cell to increase the 
     * odds to hit another part of a ship.
     * @return - the cell coordinates  of the next cell to target
     * @see findMissileDestinationCell()
     */
    private Coord2D findMissileDestinationCellExpert() {
        /* lastCellTargeted gives no hint about a ship: back to the default method */
        if (this.lastCellTargeted == null) {
            return this.findMissileDestinationCellMedium();
        }
        
        if (this.lastCellTargeted.getCellType() == CellType.OCEAN) {
            return findMissileDestinationFromFutureTargets();                      
        }
        else { /* this.lastCellTargeted is a ship: update score of adjacent cells
                to increase the odds to select one of them the next turn. */
            
            ArrayList<CellModel>adjacentCells = findAdjacentCells(this.lastCellTargeted);
            
            for (CellModel cellModel : adjacentCells) {
                if (cellModel != null) {
                    if (cellModel.getCellType() == CellType.UNKNOWN) {
                        updateScoreOfFutureTarget(cellModel);
                    }
                    else if (cellModel.getCellType().isAShip() || cellModel.getCellType() == CellType.HIT){
                        CellModel oppositeCellModel = findOppositeAdjacentCell(cellModel, adjacentCells);
                        if (oppositeCellModel != null && oppositeCellModel.getCellType() == CellType.UNKNOWN) {
                            updateScoreOfFutureTarget(oppositeCellModel);
                        }
                    }
                }
            }
            return findMissileDestinationFromFutureTargets();
        }
    }
    
    /**
     * Computes the location of the player cell targeted by a missile
     * The computation is done at medium level: this method searches for a zone 
     * in the player board, in which the ship with the greatest size (>1) that has
     * not been already destroyed, could lie.
     * 
     * Note: this method could be optimized since every time a missile is going
     * to be launched, the whole copy board is scanned for
     * collecting every cell representing an interesting zone. 
     * Since only a relatively small part of the board is modified after each
     * missile launch, it might be better
     * to modify incrementally the collection of zones, instead of recreating
     * a collection each time. But the code would be complexified, we prefer to use
     * the "brute-force" solution.
     * 
     * @return - the cell coordinates of the next cell to target
     * @see findMissileDestinationCell()
     */
    private Coord2D findMissileDestinationCellMedium() {
        int largestSize = findSizeOfTheBiggestShipNotDestroyed();

        /* If largestSize = 1, any UNKNOWN cell in the copy board can be elected
            as the future target : the easiest solution is getting a random position */
        if (largestSize == 1) {
            return findMissileDestinationCellBeginner();
        }
        else {
            ArrayList<CellModel> candidateCells = collectCandidatesForFutureTarget(largestSize);
            if (candidateCells.isEmpty()) {
                return findMissileDestinationCellBeginner();
            }
            else { /* Choose a cell randomly */
                Random generator = new Random();
                CellModel cellModel = candidateCells.get(generator.nextInt(candidateCells.size()));    
    
                this.lastCellTargeted = cellModel; 
                candidateCells.clear();
                return this.playerBoardModelCopy.cellCoords(this.lastCellTargeted);
            }
        }
    }
   
    /** 
     * Select the future missile target
     * @return - the target cell coordinates 
     * @see findMissileDestinationCellExpert()
     */
    private Coord2D findMissileDestinationFromFutureTargets() {
        CellModel cellModel = selectAndRemoveFutureTarget();
        
        if (cellModel != null) {
            this.lastCellTargeted = cellModel;
            return this.playerBoardModelCopy.cellCoords(cellModel);
        }
        else {
            return this.findMissileDestinationCellMedium();
        }    
    }
    
    
    /**
     * In order to increase the odds to find a cell belonging to the lat ship hit,
     * the set adjacentCells of cells adjacent to the one representing the ship is provided,
     * together with cellModel, which represents also a ship (cellModel is an element of adjacentCells).
     * We search in adjacentCells if there is an UNKNOWN cell in the cardinal direction opposite to cellModel's
     * If it exists, this cell will be a future target.
     * @param cellModel - cell in a cardinal direction
     * @pre cellModel is not null
     * @param adjacentCells - set of cells adjacent to the current ship
     * @return the element of adjacentCalls placed in the direction opposite to cellModel's     *
     * @see findMissileDestinationCellExpert()
     */
    private CellModel findOppositeAdjacentCell(CellModel cellModel, ArrayList<CellModel> adjacentCells) {
        CellModel oppositeCellModel = null;
        
        /* adjacentCells has exactly 4 elements (for directions NORTH (index 0), WEST (1), SOUTH (2), EAST (3)), some may be null. */
        for (int i = 0; i < 4; i++) {
            if (cellModel == adjacentCells.get(i)) {
                oppositeCellModel = adjacentCells.get((i+2)%4);
                break;
            }
        }
        return oppositeCellModel;
    }
    
    
    /**
     * Search for the size of the biggest adverse ship that has not been
     * already destroyed
     * @return the biggest size
     * @see findMissileDestinationCellMedium()
     */
    private int findSizeOfTheBiggestShipNotDestroyed() {
        /* adverseShipDescription contains the ships not destroyed only. */
        Integer maxSize = -1;
        for (String description : this.adverseShipInformation.keySet()) {
            if (this.adverseShipInformation.get(description) > maxSize) {
                maxSize = this.adverseShipInformation.get(description);
            }
        }
        return maxSize;
    }
    
    /**
     * Initialize adverseShipInformation
     * We assume that the adverse fleet is composed of the same ships as 
     * the computer's fleet.
     * @param fleet - set of ships to deal with
     * @see BoardUIComputer()
     */
    private void initAdverseShipInformation() {
        Fleet fleet = this.boardModel.getFleet();
        for (Ship ship  : fleet.getShips()) {
            this.adverseShipInformation.put(ship.getDescription(),ship.getSize());
        }
    }
    
    /**
     * Determines whether a CellModel is at the center of a "size"-lenth span of UNKNOWN cells
     * Noting C the coordinate (either row or column) of cellModel, spans are defined as follows:
     * - if size is odd: [C-2][C-1][cellModel][C+1][C+2]: test in both horizontal and vertical directions
     * - if size is even: [C-2][C-1][cellModel][C+1] or [C-1][cellModel][C+1][C+2]: test in both horizontal and vertical directions
     * @param cellModel - span center
     * @param size - span length
     * @pre size > 1
     * @return true if cellModel is a valid span center, false otherwise
     * @see collectCandidateCellsForFutureTargets()
     */
    private boolean IsCellCenterOfUnknownSpan(CellModel cellModel, int size) {
        Coord2D coord2D = this.playerBoardModelCopy.cellCoords(cellModel);

        if (size % 2 == 1) /* odd */ {
            /* A complete span along either verticel (NORTH-SOUTH) or horizontal (WEST-EAST) direction is valid. */
            return (isCellCenterOfUnknownSpanAlongOneDirection(cellModel, Direction.NORTH, size/2) 
                    && isCellCenterOfUnknownSpanAlongOneDirection(cellModel, Direction.SOUTH, size/2)) ||
                    
                    (isCellCenterOfUnknownSpanAlongOneDirection(cellModel, Direction.WEST, size/2) 
                    && isCellCenterOfUnknownSpanAlongOneDirection(cellModel, Direction.EAST, size/2));
        }
        else { /* size is even */
            return (isCellCenterOfUnknownSpanAlongOneDirection(cellModel, Direction.NORTH, size/2 - 1) 
                    && isCellCenterOfUnknownSpanAlongOneDirection(cellModel, Direction.SOUTH, size/2)) ||
                    
                    (isCellCenterOfUnknownSpanAlongOneDirection(cellModel, Direction.NORTH, size/2) 
                    && isCellCenterOfUnknownSpanAlongOneDirection(cellModel, Direction.SOUTH, size/2 - 1)) ||
                    
                    (isCellCenterOfUnknownSpanAlongOneDirection(cellModel, Direction.WEST, size/2 - 1) 
                    && isCellCenterOfUnknownSpanAlongOneDirection(cellModel, Direction.EAST, size/2)) ||
                    
                    (isCellCenterOfUnknownSpanAlongOneDirection(cellModel, Direction.WEST, size/2) 
                    && isCellCenterOfUnknownSpanAlongOneDirection(cellModel, Direction.EAST, size/2 - 1));
        }
    }
    
    /**
     * Determines whether a span along one direction is made of UNKNOWN cells
     * @param cellModel - span center
     * @param direction - one cardinal direction
     * @param spanLength - number of cells to test
     * @return true if a valid span exists, false otherwise
     * @see IsCellCenterOfUnknownSpan()
     */
    private boolean isCellCenterOfUnknownSpanAlongOneDirection(CellModel cellModel, Direction direction, int spanLength) {        
        for (int i = 1; i <= spanLength; i++) {
            CellModel adjacentCell = this.playerBoardModelCopy.adjacentCell(cellModel, direction, i);
            if (adjacentCell == null || adjacentCell.getCellType() != CellType.UNKNOWN) {
                return false;
            }
        }        
        return true;
    }
    
    /**
     * @return a random Direction
     * @see placeShipsOnBoardAtRandom()
     */
    private Direction randomDirection() {
        Random generator = new Random();
        Direction[] dirValues = Direction.values();
        Direction direction = Direction.NORTH;        
        
        switch(generator.nextInt(dirValues.length)) {
            case 0: 
                direction = Direction.NORTH;
                break;  
            case 1: 
                direction = Direction.WEST;
                break;  
            case 2: 
                direction = Direction.SOUTH;
                break;  
            case 3: 
                direction = Direction.EAST;
                break;  
            default:
                break;
        }
        return direction;
    }
    
    /**
     * @return the CellModel associated with the best score, null if there is no CellModel available
     * If not null, the selectedCellModel is removed from the set of future targets
     * @see findMissileDestinationFromFutureTargets()
     */
    private CellModel selectAndRemoveFutureTarget() {
        if (this.futureTargets.isEmpty()) {
           return null;
        } 
        else {
            Integer maxScore = -1 ;
            CellModel selectedCellModel = null;

            for (CellModel cellModel : this.futureTargets.keySet()) {
                if (this.futureTargets.get(cellModel) > maxScore) {
                    maxScore = this.futureTargets.get(cellModel);
                    selectedCellModel = cellModel;
                }
            }
            if (selectedCellModel != null) {
                this.futureTargets.remove(selectedCellModel);
            }
            return selectedCellModel;
        }
    }
    
    
    /**
     * Add a CellModel as a future target or increases it score if it is already a future target     * 
     * @see findMissileDestinationCellExpert()
     */
    private void updateScoreOfFutureTarget(CellModel cellModel) {
        if (this.futureTargets.containsKey(cellModel)) {
            this.futureTargets.replace(cellModel, this.futureTargets.get(cellModel) + 1);
        }
        else {
            this.futureTargets.put(cellModel, 1);
        }
    }
}
